-- 운영(prod) 반영 필요 SQL 모음 (2026-11-04 기준 정리)
--
-- ⚠️ 2026-11-08 추가: 이 파일은 "CLAUDE.md에 미적용으로 기록된 항목"만 훑어서 만들었기 때문에
--    문서에 기록이 안 남아있던 컬럼 누락분(crm_sales.payment_method 등)을 놓쳤고, 실제로
--    2026-11-08 운영에서 "회원에게 상품 부여" 500 에러로 터졌다. 누락분은
--    `PROD_DEPLOY_20261108_missing_columns.sql`에 따로 정리했으니 **그 파일도 함께 실행할 것.**
--    (그 파일은 문서가 아니라 information_schema 전수 비교로 만들었다.)
--
-- 아래는 "linkfit_prod_check"(2026-10-22 운영 덤프 복사본)에 직접 쿼리를 날려 실제로
-- 아직 반영되지 않은 것만 확인한 목록이다. CLAUDE.md에 "운영 미적용"으로 적혀 있던 항목 중
-- 상당수는 이미 다른 경로로 반영되어 있었음(gym_id 스코핑 전체, crm_user_trainer_employment,
-- user_profiles.join_date_unknown, gym_holiday 복합 유니크, settlement_ledger.product_id
-- VARCHAR(100), member_tickets.paid_remaining, crm_users_legacy_admin_role_fix 등) —
-- 이 파일에는 그런 이미 끝난 것들은 넣지 않았다.
--
-- 실행 전: 반드시 운영 DB 백업 먼저 뜨고, 트래픽 적은 시간대에 실행할 것.
-- 전부 재실행 안전(idempotent) — 이미 적용된 상태에서 다시 실행해도 에러/중복 없음.

-- ============================================================
-- 1. 관리자페이지 전용 계정(매니저/직원) role 추가 — crm_users.role ENUM 확장
--    (원본: docs/sql/crm_user_manager_employee_role_20260824.sql)
--    확인: 운영 DB의 role enum이 아직 ('super_admin','gym_admin','trainer')뿐이라
--    매니저/직원 계정 생성 기능(2026-08-24 배포분) 자체가 운영에서 동작 불가능한 상태.
-- ============================================================
ALTER TABLE crm_users MODIFY COLUMN role ENUM('super_admin','gym_admin','trainer','manager','employee') NOT NULL;


-- ============================================================
-- 2. 만료 및 예정 회원 관리 — 메모 스택 테이블 신설
--    (원본: docs/sql/crm_reregistration_notes_20260825.sql)
--    확인: 운영 DB에 이 테이블이 아직 없음 — 이 화면의 메모 기능이 운영에서 전부 에러.
-- ============================================================
CREATE TABLE IF NOT EXISTS crm_reregistration_notes (
  id CHAR(36) NOT NULL,
  reregistration_id CHAR(36) NOT NULL,
  gym_id BIGINT UNSIGNED NOT NULL,
  author_id CHAR(36) DEFAULT NULL COMMENT 'crm_users.id',
  content TEXT NOT NULL,
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  KEY idx_crn_reregistration (reregistration_id),
  KEY idx_crn_gym (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 3. PT/원포인트 회원 분류 오류 백필 (2026-11-04 신규)
--    (원본: docs/sql/trainer_members_pt_backfill_20261101.sql)
--    확인: 운영 데이터에 실제로 PT 세션을 보유한 채 ONE_POINT로 잘못 분류된 회원
--    최소 2명(70~300회, 80회 보유) 존재 — 트레이너 앱에서 "원포인트 회원"으로 잘못 표시됨.
--    함께 배포해야 하는 코드 수정: lof-admin CLAUDE.md "3-1-24", lof-backend CLAUDE.md
--    "trainer_members.member_type" 항목 참고 — 코드가 먼저 나가야 재발을 막고, 이 백필은
--    이미 잘못 저장된 기존 데이터를 교정한다(코드 배포 순서와 무관하게 안전하게 실행 가능).
-- ============================================================
UPDATE trainer_members tm
JOIN user_profiles up ON up.user_id = tm.member_id
SET tm.member_type = 'PT'
WHERE tm.member_type <> 'PT'
  AND (COALESCE(up.pt_sessions_left, 0) + COALESCE(up.service_pt_sessions_left, 0)) > 0;


-- ============================================================
-- 4. [선택/파괴적] 죽은 레거시 쪽지 테이블 삭제 — 아래는 DROP TABLE이라 별도로 분리함
--    (원본: docs/sql/drop_legacy_message_tables_20260826.sql)
--    확인: 운영 DB에 message/message_recipient 테이블이 아직 남아있음 — 다만 이미
--    2026-08-26에 코드(컨트롤러/매퍼)는 전부 삭제돼 어디서도 참조하지 않는 죽은 테이블이라
--    운영에 실제 장애를 일으키지는 않는다. 급하지 않음 — DROP이므로 실행 전 데이터 보존이
--    필요없는지(죽은 기능이라 데이터도 의미 없음) 한 번 더 확인 후 아래 두 줄만 별도로 실행할 것.
-- ============================================================
-- DROP TABLE IF EXISTS message_recipient;
-- DROP TABLE IF EXISTS message;
