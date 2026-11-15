-- 운영 DB에 누락된 컬럼 보충 (2026-11-08)
--
-- 배경: 2026-11-08 운영에서 "관리자페이지 → 회원에게 상품 부여" 시 500 발생.
--   Unknown column 'payment_method' in 'field list'  (CrmSalesMapper.insert)
-- 코드는 배포됐는데 해당 컬럼 마이그레이션이 운영에 적용되지 않은 상태였다.
-- (2026-11-04 PROD_DEPLOY_20261104.sql 정리 당시 이 항목을 빠뜨린 것 — 그래서 이번에는
--  information_schema로 로컬 linkfit vs 운영 덤프의 전체 컬럼을 전수 비교해 누락분을 모두 찾음.)
--
-- 전수 비교 결과 "운영에 없고 로컬에만 있으며 실제 코드가 쓰는" 컬럼은 아래 3개뿐이었다.
-- (onepoint_requests.note는 로컬에만 있는 미사용 잔재 — 매퍼는 `notes`를 쓰고 그 컬럼은
--  운영에도 있으므로 대상 아님. exercises_backup_local_* 은 로컬 백업 테이블이라 대상 아님.)
--
-- 전부 재실행 안전(idempotent) — 이미 있으면 건너뛴다.
-- 실행 전 운영 DB 백업 권장.

-- ============================================================
-- 1. crm_sales.payment_method — 지금 500을 내고 있는 원인 (최우선)
--    CRM 매출에 결제수단 기록(2026-10-25 배포분)이 쓰는 컬럼.
--    이게 없으면 "회원에게 상품 부여"가 recordSale 단계에서 전부 실패(트랜잭션 롤백)한다.
-- ============================================================
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'crm_sales'
       AND COLUMN_NAME = 'payment_method') = 0,
  'ALTER TABLE crm_sales ADD COLUMN payment_method VARCHAR(50) NULL AFTER amount',
  'SELECT ''crm_sales.payment_method 이미 존재 — 건너뜀'' AS msg');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ============================================================
-- 2. product.duration_days / product.session_count
--    상품 관리(/products) 등록·수정(ProductMapper.insert/update)이 쓰는 컬럼.
--    아직 리포트는 없었지만 1번과 완전히 같은 원인으로 운영에서 이미 깨져 있는 상태다
--    (등록/수정 시 Unknown column 오류 → 위 1번과 동일한 증상).
-- ============================================================
SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product'
       AND COLUMN_NAME = 'duration_days') = 0,
  'ALTER TABLE product ADD COLUMN duration_days INT NOT NULL DEFAULT 0 AFTER price',
  'SELECT ''product.duration_days 이미 존재 — 건너뜀'' AS msg');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

SET @sql := IF(
  (SELECT COUNT(*) FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'product'
       AND COLUMN_NAME = 'session_count') = 0,
  'ALTER TABLE product ADD COLUMN session_count INT NULL AFTER duration_days',
  'SELECT ''product.session_count 이미 존재 — 건너뜀'' AS msg');
PREPARE s FROM @sql; EXECUTE s; DEALLOCATE PREPARE s;

-- ============================================================
-- 적용 후 확인 — 3개 행이 모두 나와야 정상
-- ============================================================
SELECT TABLE_NAME, COLUMN_NAME, COLUMN_TYPE, IS_NULLABLE, COLUMN_DEFAULT
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = DATABASE()
  AND ((TABLE_NAME = 'crm_sales' AND COLUMN_NAME = 'payment_method')
    OR (TABLE_NAME = 'product'   AND COLUMN_NAME IN ('duration_days', 'session_count')))
ORDER BY TABLE_NAME, COLUMN_NAME;
