# LINK_Fit Admin — DB 마이그레이션 이력

> 실제 DB에 적용한 DDL/DML 이력을 날짜순으로 기록한다.  
> 신규 변경 시 최상단에 추가.

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
