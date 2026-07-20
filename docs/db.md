# LINK_Fit Admin — DB 마이그레이션 이력

> 실제 DB에 적용한 DDL/DML 이력을 날짜순으로 기록한다.  
> 신규 변경 시 최상단에 추가.

---

## 2026-07-15 — CRM 테이블 적용 상태 점검 (⚠️ DB 미수정, 확인만 진행)

### 배경
전체적인 정리 작업에 앞서 실제 `linkfit` DB 상태를 코드(mapper XML) 및 문서(`docs/sql.md`, 이 문서의 2026-06-08 항목)와 대조 점검. **이번 점검에서는 DB를 전혀 수정하지 않았다** — 아래는 확인된 현황 정리.

### 핵심 발견: 2026-06-08 항목은 "적용 완료"로 기록돼 있으나, 실제로는 `crm_users` 1개만 적용되어 있음

`SHOW TABLES`로 직접 확인한 결과, 2026-06-08 항목에 나열된 CRM 테이블 18개 중 **`crm_users`만 존재**하고 나머지 17개는 로컬 `linkfit` DB에 없음. `crm_users`에는 실제 로그인 계정(`admin` / `super_admin` / `gym_id=1`)도 들어있어 이 부분만 정상 적용된 것은 확실함.

DDL 자체는 `docs/sql.md`의 "CRM 전용 테이블 DDL" 섹션에 18개 전부 이미 작성되어 있고, `crm_users` 부분과 실제 테이블 구조를 대조해본 결과 완전히 일치함 — 즉 **설계는 끝나 있고 실행만 안 된 상태**로 보인다.

### 미적용 테이블 17개 및 영향받는 기능

**A. 현재 코드에서 실제로 참조 중 — 해당 기능 호출 시 500 에러 발생**

| 테이블 | 참조하는 Mapper | 영향 기능 / 엔드포인트 |
|---|---|---|
| `crm_member_assignments` | `MemberMapper`, `StaffMapper`, `CrmMemberMapper` | 회원 담당 트레이너 지정·조회, 트레이너 대시보드, 담당 회원 목록(`/api/staff/{id}/members`) |
| `crm_member_notes` | `CrmMemberMapper` | 회원 CRM 메모 (`/api/members/{id}/notes`) |
| `crm_member_tags` | `CrmMemberMapper` | 회원 상태 태그 (`/api/members/{id}/tags`) |
| `crm_membership_history` | `CrmMemberMapper` | 이용권 변경 이력 기록 (`/api/memberships/member/{id}/actions`) |
| `crm_feedback_requests` | `FeedbackRequestMapper`, `StaffMapper` | 피드백 요청 목록·배정·답변 (`/api/feedback/requests/**`) |
| `crm_feedback_tickets` | `FeedbackTicketMapper`, `CrmDailyStatsMapper` | 피드백 티켓 발급·조회, 자동 만료 배치, 일별 통계 집계 |
| `crm_ticket_settings` | `FeedbackTicketMapper` | 헬스장별 티켓 발행 설정 (`/api/feedback/settings`) |
| `crm_re_registration` | `ReRegistrationMapper`, `StaffMapper` | 재등록 관리 전체 (`/api/reregistration/**`), 자동 분류 배치 |
| `crm_sales` | `CrmSalesMapper` | CRM 매출 등록·조회·엑셀 내보내기 (`/api/crm-sales/**`) |
| `crm_sales_targets` | `CrmSalesMapper` | 월 매출 목표 설정 |
| `crm_cs_tickets` | `CrmCsTicketMapper` | CS 티켓 관리 (`/api/cs/tickets/**`) |
| `crm_announcements` | `CrmAnnouncementMapper` | 공지사항 작성·발송 (`/api/announcements/**`) |
| `crm_messages` | `CrmMessageMapper` | CRM 내부 쪽지함 (`/api/inbox/**`) — 앱 회원↔트레이너 쪽지(`message_conversation`/`chat_message`)와는 별개 시스템 |
| `crm_daily_stats` | `CrmDailyStatsMapper` | 일별 통계 자동 집계 배치(`DailyStatsScheduler`), 대시보드 추이 차트 |

**B. 아직 코드에서 참조되지 않음 — 우선순위 낮음 (추후 기능 구현 시 필요)**

| 테이블 | 용도 |
|---|---|
| `crm_pt_registration_type` | PT 신규/재등록/소개 구분 — `admin-todo.md`에 "CRM 매출 등록으로 대체 가능"으로 메모됨 |
| `crm_ticket_purchases` | 티켓 구매 이력 — Sector 9, "정식 출시 후 구현 예정" |
| `crm_ticket_inventory` | 티켓 재고 — Sector 9, 위와 동일 |

### 참고: 2026-06-06 항목(지점 다중화)은 정상 적용 확인됨
`gym` 테이블, `admin_user.gym_id` 컬럼 모두 실제 DB에 존재. 이 마이그레이션은 기록과 실제 상태가 일치.

### 다음 단계 (실행 전 검토 필요, 이번엔 미실행)
1. ~~`docs/sql.md`의 CRM DDL 17개 블록 재검토~~ → 아래 2026-07-15 충돌 검사 항목에서 완료
2. ~~앱 서비스 쪽 스키마와 충돌 없는지 재확인~~ → 완료 (충돌 없음, 아래 참고)
3. 로컬 DB에 우선 적용 → 동작 확인 → 운영 DB 적용 순서로 진행 권장 (아직 미실행)
4. 적용 후 이 문서에 새 항목으로 결과 기록

---

## 2026-07-15 (2) — CRM DDL 17개 충돌 검사 (⚠️ 여전히 DB 미수정, dry-run만 진행)

### 검사 방법
실제 `linkfit` DB는 건드리지 않고, 같은 MariaDB 서버에 임시 스크래치 DB(`linkfit_dryrun`)를 만들어
`gym`/`users`/`crm_users`/`admin_user`(구조+실データ 복사) 위에 `docs/sql.md`의 CRM DDL 17개 블록
(`crm_member_assignments` ~ `crm_daily_stats`, `crm_users` 제외)을 그대로 실행해봤다.
검사 후 `linkfit_dryrun`은 즉시 삭제했고, 실제 `linkfit` DB에는 어떤 DDL/DML도 실행하지 않았다.

### 결과: 충돌 없음 — 17개 테이블 전부 에러 없이 생성됨

| 확인 항목 | 결과 |
|---|---|
| 테이블명 충돌 (기존 91개 테이블과 중복) | 없음 — 17개 전부 신규 이름 |
| CREATE TABLE 실행 (문법/타입 오류) | 전부 성공 (0 에러) |
| 예약어 충돌 (`role`, `action`, `target`, `status` 등 컬럼명) | 문제 없음 — 실제 실행에서 에러 없이 통과 |
| FK 제약 충돌 | 해당 없음 — 이 DDL은 FK 제약 없이 논리적 참조만 사용 (컬럼명·타입 일치만 확인하면 됨) |
| 시드 INSERT (`crm_ticket_settings`, `crm_ticket_inventory` — gym 테이블 기준 자동 생성) | 정상 동작, `gym` 1행 기준 1행씩 생성됨 |
| lof-backend 앱 코드가 `crm_` 접두사를 참조하는지 | 없음 — 앱 쪽은 이 네임스페이스를 전혀 모름, 충돌 가능성 없음 |
| 문자셋/콜레이션 (`utf8mb4`/`utf8mb4_unicode_ci`) | 기존 DB 전체와 동일 |

**결론**: 지금 로컬 `linkfit` DB에 `docs/sql.md`의 CRM DDL 17개 블록을 그대로 적용해도 충돌 요소는 발견되지 않았다.
실행 자체는 안전한 것으로 보이며, 남은 판단은 "지금 적용할지 타이밍"뿐이다 (공유 DB라 앱 서비스 배포 일정과 조율 권장).

---

## 2026-07-15 (3) — CRM DDL 17개 로컬 DB 실제 적용 완료

### 적용 내역
위 dry-run 검사가 충돌 없음으로 확인되어, 같은 DDL을 **로컬 `linkfit` DB에 실제로 적용**했다.
`docs/sql.md`의 CRM 전용 테이블 DDL 중 `crm_member_assignments` ~ `crm_daily_stats` (17개 블록,
`crm_users`는 기존 적용분이라 제외) 그대로 실행.

- 적용 전: 90개 테이블 (`crm_users`만 CRM 계열 존재)
- 적용 후: **107개 테이블** (17개 증가 — 정확히 일치)
- 시드 데이터: `crm_ticket_settings`, `crm_ticket_inventory` 각 1행 자동 생성 (`gym` 테이블 1개 지점 기준)
- 에러: 없음

### 반영된 테이블 (17개)
`crm_member_assignments`, `crm_member_tags`, `crm_member_notes`, `crm_membership_history`,
`crm_pt_registration_type`, `crm_feedback_tickets`, `crm_ticket_settings`, `crm_ticket_purchases`,
`crm_ticket_inventory`, `crm_feedback_requests`, `crm_messages`, `crm_cs_tickets`, `crm_sales`,
`crm_sales_targets`, `crm_re_registration`, `crm_announcements`, `crm_daily_stats`

### 상태 및 남은 작업
- **로컬 DB만 적용됨. 운영(prod) DB는 아직 미적용.**
- 위 "알려진 이슈 1번"(CLAUDE.md)에서 설명한 500 에러 기능들은 로컬 환경에서는 이제 정상 동작할 것으로 예상됨
  — 실제 애플리케이션 기동 후 기능별 동작 확인은 아직 안 함 (다음 단계로 권장).
- 운영 DB 적용 시점은 별도 협의 필요 (공유 DB, 배포 일정 조율).

---

## 연결 정보

| 항목 | 값 |
|------|-----|
| DBMS | MariaDB 10.11.14 |
| Host | localhost:3306 |
| Database | `linkfit` |
| Username | `linkfit` |
| Password | `link_fit!` |

---

## 2026-06-08 — CRM 전체 스키마 도입 및 JWT 인증 전환

### 배경
- admin-srs.md 기반 17개 섹터 CRM 전체 스키마 도입
- JWT 인증 + `crm_users` 테이블로 인증 계층 전환
- 기존 `admin_user` 테이블은 유지 (하위 호환 보장)

### 신규 CRM 테이블 (crm_* prefix)

| 테이블 | 섹터 | 설명 |
|--------|------|------|
| `crm_users` | Auth | JWT 인증용 CRM 계정 |
| `crm_member_assignments` | Sector 2 | 담당 트레이너 지정 |
| `crm_member_tags` | Sector 2 | 회원 커스텀 태그 |
| `crm_member_notes` | Sector 2 | 회원 메모 |
| `crm_membership_history` | Sector 3 | 이용권 변경 이력 |
| `crm_pt_registration_type` | Sector 4 | PT 등록 유형 |
| `crm_feedback_tickets` | Sector 7 | 피드백 티켓 발행/사용 |
| `crm_ticket_settings` | Sector 8 | 헬스장별 티켓 설정 |
| `crm_ticket_purchases` | Sector 9 | 티켓 구매 이력 |
| `crm_ticket_inventory` | Sector 9 | 티켓 재고 |
| `crm_feedback_requests` | Sector 10 | 피드백 요청 처리 |
| `crm_messages` | Sector 11 | CRM 내부 쪽지 |
| `crm_cs_tickets` | Sector 12 | CS 문의 티켓 |
| `crm_sales` | Sector 14 | CRM 매출 통합 |
| `crm_sales_targets` | Sector 14 | 월별 매출 목표 |
| `crm_re_registration` | Sector 15 | 재등록 관리 |
| `crm_announcements` | Sector 16 | 공지/알림 발송 |
| `crm_daily_stats` | Sector 17 | 일별 통계 집계 |

### 적용 쿼리

전체 DDL은 `docs/sql.md` 의 **"CRM 전용 테이블 DDL"** 섹션 참고.

```sql
-- admin_user → crm_users 데이터 이관 (DDL 안에 포함됨)
-- 이관 확인
SELECT cu.username, cu.role, g.branch_code, cu.is_active
FROM crm_users cu
JOIN gym g ON cu.gym_id = g.id
ORDER BY g.branch_code, cu.username;
```

### 인증 변경 사항
- **이전**: Spring Security FormLogin + 세션 + `admin_user`
- **이후**: JWT Bearer Token (HttpOnly Cookie) + `crm_users`
- 기존 `admin_user` 테이블 유지 (삭제 금지)
- `crm_token` HttpOnly 쿠키로 JWT 저장 및 전송

### 코드 변경 파일

| 파일 | 변경 내용 |
|------|---------|
| `build.gradle` | jjwt-api 0.12.6 의존성 추가 |
| `application-dev.yml` | JWT secret/expiration 설정 추가 |
| `domain/CrmUser.java` | 신규 도메인 클래스 |
| `mapper/CrmUserMapper.java` | 신규 mapper 인터페이스 |
| `mapper/CrmUserMapper.xml` | 신규 mapper XML |
| `security/JwtUtil.java` | JWT 토큰 생성/검증 유틸 |
| `security/JwtCookieFilter.java` | JWT 쿠키 인증 필터 |
| `config/SecurityConfig.java` | FormLogin → JWT 필터 교체 |
| `controller/api/AuthApiController.java` | 로그인/로그아웃 REST API |
| `service/CrmUserService.java` | CRM 사용자 서비스 인터페이스 |
| `service/mybatis/MyBatisCrmUserService.java` | MyBatis 구현체 |
| `templates/login.html` | fetch API 기반 JWT 로그인 폼 |

---

## 2026-06-06 — 다중 지점(gym) 지원 및 로그인 개편

### 배경
- 여러 헬스장에 각각의 관리 페이지를 부여하기 위해 지점(gym) 개념 도입
- 기존 `admin_user.username` 전역 유일 → 지점 내 유일로 변경
- 로그인 시 **지점코드 + 아이디 + 비밀번호** 3종 입력으로 변경

### 지점코드 규칙
- CHAR(4): **영문 대문자 2자리 + 숫자 2자리** (예: `LF01`, `GY02`, `AB10`)

### 적용 쿼리

```sql
-- 1. gym 테이블 신규 생성
CREATE TABLE IF NOT EXISTS gym (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    branch_code CHAR(4)         NOT NULL COMMENT '영문2+숫자2, e.g. LF01',
    name        VARCHAR(100)    NOT NULL,
    is_active   TINYINT(1)      NOT NULL DEFAULT 1,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_branch_code (branch_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 2. 기본 지점 삽입 (기존 admin 계정 배정용)
INSERT IGNORE INTO gym (branch_code, name) VALUES ('LF01', 'LINK_Fit 본점');

-- 3. admin_user에 gym_id 컬럼 추가 (nullable로 먼저 추가)
ALTER TABLE admin_user
    ADD COLUMN gym_id BIGINT UNSIGNED NULL AFTER id;

-- 4. 기존 계정을 기본 지점(LF01)에 배정
UPDATE admin_user
SET gym_id = (SELECT id FROM gym WHERE branch_code = 'LF01')
WHERE gym_id IS NULL;

-- 5. gym_id NOT NULL 제약 + FK 추가
ALTER TABLE admin_user
    MODIFY COLUMN gym_id BIGINT UNSIGNED NOT NULL,
    ADD CONSTRAINT fk_admin_gym FOREIGN KEY (gym_id) REFERENCES gym (id);

-- 6. username UNIQUE 범위를 전역 → 지점 내로 변경
ALTER TABLE admin_user DROP INDEX username;
ALTER TABLE admin_user ADD UNIQUE KEY uq_gym_username (gym_id, username);
```

### 변경 후 admin_user 스키마

```sql
CREATE TABLE admin_user (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    gym_id      BIGINT UNSIGNED NOT NULL,              -- 추가
    username    VARCHAR(50)     NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    name        VARCHAR(50)     NOT NULL,
    role        VARCHAR(20)     NOT NULL,
    is_active   TINYINT(1)      NOT NULL DEFAULT 1,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_gym_username (gym_id, username),     -- 변경 (전역→지점내)
    CONSTRAINT fk_admin_gym FOREIGN KEY (gym_id) REFERENCES gym (id)
);
```

### 롤백 쿼리

```sql
-- 롤백 시 실행 순서
ALTER TABLE admin_user DROP FOREIGN KEY fk_admin_gym;
ALTER TABLE admin_user DROP INDEX uq_gym_username;
ALTER TABLE admin_user ADD UNIQUE KEY username (username);
ALTER TABLE admin_user DROP COLUMN gym_id;
DROP TABLE IF EXISTS gym;
```

### 코드 변경 파일
| 파일 | 변경 내용 |
|------|---------|
| `domain/AdminUser.java` | `gymId`, `branchCode` 필드 추가 |
| `mapper/AdminUserMapper.java` | `findByUsername` → `findByBranchCodeAndUsername` |
| `mapper/AdminUserMapper.xml` | gym JOIN 쿼리로 교체 |
| `service/AdminUserDetailsService.java` | `branchCode::username` 파싱 처리 |
| `templates/login.html` | 지점코드 입력 필드 추가, JS 합성 로직 |
| `static/css/login.css` | `.field-hint` 스타일 추가 |

---

## 신규 지점 추가 방법

```sql
-- 1. 지점 등록
INSERT INTO gym (branch_code, name) VALUES ('GY01', '강남 지점');

-- 2. 관리자 계정 생성 (password는 BCrypt 해시값)
--    admin1234 해시: $2b$10$f2PFkWjuVXSrtRcbONISbeM8tCNfLEVaTSLzPiQm0R7O4ElQbMv0u
INSERT INTO admin_user (gym_id, username, password, name, role)
SELECT id, 'admin', '$2b$10$해시값', '관리자', 'SUPER_ADMIN'
FROM gym WHERE branch_code = 'GY01';

-- 3. 현황 확인
SELECT g.branch_code, g.name AS gym_name, a.username, a.role, a.is_active
FROM admin_user a
JOIN gym g ON a.gym_id = g.id
ORDER BY g.branch_code, a.username;
```

---

## 지점 현황 조회

```sql
-- 전체 지점 + 계정 수
SELECT g.branch_code, g.name, g.is_active,
       COUNT(a.id) AS admin_count
FROM gym g
LEFT JOIN admin_user a ON a.gym_id = g.id
GROUP BY g.id
ORDER BY g.branch_code;
```
