# LINK_Fit Admin — DB 테이블 정의

MariaDB 기준 DDL. 문자셋: `utf8mb4`, 콜레이션: `utf8mb4_unicode_ci`.

---

## 실제 DB 연결 정보

| 항목 | 값 |
|------|-----|
| DBMS | MariaDB 10.11.14 |
| Host | localhost |
| Port | 3306 |
| **Database** | `linkfit` |
| Username | `linkfit` |
| Password | `link_fit!` |

> ⚠️ DB 이름은 `linkfit` (`linkfit_admin` 아님). 어드민과 앱 서비스가 같은 DB를 공유한다.

---

## 기존 `linkfit` DB 테이블 현황

아래 테이블은 앱 서비스에서 이미 사용 중이므로 **재생성 금지**.

### 회원 / 인증

| 테이블 | 설명 | 어드민 매핑 |
|--------|------|------------|
| `users` | 앱 사용자 계정 (email, role: MEMBER\|TRAINER, is_active, **grade**) | → `member` / `staff` 도메인 |
| `user_auth` | 소셜/이메일 인증 정보 | — |
| `user_profiles` | 프로필 상세 (name, birth_date, gender, contact, **member_type**: null\|OT\|PT) | → `member` 상세 |
| `user_daily_habits` | 일상 습관 정보 (**daily_note** 추가) | — |
| `user_exercise_info` | 운동 계획 (plan_frequency, **exercise_note** — 기존 복잡 필드 제거) | — |
| `user_exercise_purposes` | 운동 목적 (다중) | — |
| `user_medical_history` | 의료 이력 (조건 그룹화 + **adult_disease_detail**, **joint_detail**, **other_detail**) | — |
| `user_visit_routes` | 방문 경로 (다중) | — |

### 트레이너 / 수업

| 테이블 | 설명 | 어드민 매핑 |
|--------|------|------------|
| `trainer_members` | 트레이너-회원 배정 | — |
| `trainer_schedules` | PT/OT 수업 일정 | → `class_session` 참고 |
| `schedules` | 사용자 개인 일정 | — |

### 운동 / 식단 / 커뮤니티

| 테이블 | 설명 |
|--------|------|
| `exercise` | 운동 마스터 |
| `exercise_records` / `exercise_record_sets` | 운동 기록 |
| `diet_records` | 식단 기록 |
| `record_comment` | 기록 댓글 |
| `posts` / `post_comments` / `post_likes` | 커뮤니티 |
| `body_part`, `muscle`, `equipment` 등 | 운동 분류 메타 |

---

## 어드민 vs 기존 DB 매핑 주의사항

### member 도메인
어드민의 `member` 는 실제 DB에서 두 테이블로 분리되어 있다.

```
admin: member { id, name, phone, gender, status, joinDate, email, memberType, ... }
  ↕
DB: users { user_id, email, role='MEMBER', is_active, created_at }
    + user_profiles { user_id, name, contact, gender, birth_date, member_type, ... }
```

- `users.role = 'MEMBER'` → 회원
- `users.role = 'TRAINER'` → 직원(staff)
- `users.is_active = 1` → `ACTIVE`, `0` → `SUSPENDED`
- **gender 값**: `'남자'` / `'여자'` (Korean enum, 대문자 MALE/FEMALE 아님)

### staff 도메인
```
admin: staff { id, name, phone, role, hireDate, status }
  ↕
DB: users WHERE role='TRAINER'
    + user_profiles { user_id, name, contact, ... }
```

### 회원 등급 및 유형 권한 체계

#### grade (등급) — `users.grade`

| 값 | 명칭 | 결정 방식 |
|----|------|---------|
| `BASIC` | 기본 | 기본값 (미결제) |
| `MEMBERSHIP` | 멤버십 | 구독권 결제 |
| `PREMIUM` | 프리미엄 | 구독권 결제 |
| `VIP` | VIP | 구독권 결제 |

#### memberType (유형) — `user_profiles.member_type`

| 값 | 명칭 | 실효 권한 | 부여 주체 |
|----|------|---------|---------|
| `null` | 일반 | grade 그대로 | — |
| `OT` | OT 회원 | max(grade, MEMBERSHIP) | 관리자 전용 |
| `PT` | PT 회원 | VIP (등급 무관) | 관리자 전용 |

**실효 권한 계산 로직:**
```
effectiveGrade =
  memberType == 'PT'  → VIP
  memberType == 'OT'  → max(grade, MEMBERSHIP)
  memberType == null  → grade
```

> grade는 구독권 결제로만 변경. memberType은 관리자 페이지에서만 부여/회수.

---

### 도메인 컬럼 매핑 요약

| 어드민 도메인 필드 | DB 테이블.컬럼 | 비고 |
|-------------------|--------------|------|
| `member.id` | `users.user_id` | |
| `member.email` | `users.email` | |
| `member.name` | `user_profiles.name` | |
| `member.phone` | `user_profiles.contact` | |
| `member.gender` | `user_profiles.gender` | `'남자'`/`'여자'` |
| `member.birthDate` | `user_profiles.birth_date` | |
| `member.memberType` | `user_profiles.member_type` | `null`/`OT`/`PT` — 관리자 전용 |
| `member.grade` | `users.grade` | `BASIC`/`MEMBERSHIP`/`PREMIUM`/`VIP` |
| `member.status` | `users.is_active` | `1=ACTIVE`, `0=SUSPENDED` |
| `member.joinDate` | `users.created_at` | DATE() 변환 |
| `staff.id` | `users.user_id` | role='TRAINER' |
| `staff.name` | `user_profiles.name` | |
| `staff.phone` | `user_profiles.contact` | |
| `attendance.memberId` | `attendance.user_id` | |
| `consult.memberId` | `consult.user_id` | |
| `membership.userId` | `membership.user_id` | |

---

## 기존 테이블 스키마 변경 (ALTER)

앱 프론트엔드 개편에 따라 기존 `linkfit` DB 테이블에 적용할 변경사항.
**앱 백엔드와 협의 후 실행할 것.**

```sql
-- ─────────────────────────────────────────────────────────────
-- users: grade 컬럼 추가 (등급 체계)
-- ─────────────────────────────────────────────────────────────
ALTER TABLE users
    ADD COLUMN grade VARCHAR(20) NOT NULL DEFAULT 'BASIC'
        CHECK (grade IN ('BASIC', 'MEMBERSHIP', 'PREMIUM', 'VIP'))
    AFTER is_active;

-- ─────────────────────────────────────────────────────────────
-- user_daily_habits: daily_note 컬럼 추가
-- ─────────────────────────────────────────────────────────────
ALTER TABLE user_daily_habits
    ADD COLUMN daily_note TEXT NULL;

-- ─────────────────────────────────────────────────────────────
-- user_medical_history: 질환 그룹화 + 상세 컬럼 추가
-- conditions 가능 값 변경:
--   구: 고혈압, 저혈압, 당뇨, 심근경색, 천식 및 폐질환, 허리, 어깨, 무릎, 기타 병력 및 수술이력, 없음
--   신: 성인병, 천식 및 폐질환, 관절, 기타 병력 및 수술이력, 없음
-- ─────────────────────────────────────────────────────────────
ALTER TABLE user_medical_history
    ADD COLUMN adult_disease_detail VARCHAR(255) NULL,
    ADD COLUMN joint_detail VARCHAR(255) NULL,
    ADD COLUMN other_detail VARCHAR(255) NULL;
-- 기존 other_detail 컬럼이 있으면 생략. conditions 값 마이그레이션은 별도 스크립트 필요.

-- ─────────────────────────────────────────────────────────────
-- user_exercise_info: 구조 단순화
-- 제거: exercise_type, exercise_period, pt_experience, pt_satisfaction, pt_dissatisfaction_reason
-- 추가: exercise_note (트레이너 전달 메모)
-- plan_frequency 가능 값 변경: 주2~3회 | 주4~5회 | 매일 | 미정
-- ─────────────────────────────────────────────────────────────
ALTER TABLE user_exercise_info
    ADD COLUMN exercise_note TEXT NULL;
-- 제거 컬럼은 앱 백엔드 배포 후 DROP COLUMN 실행 (안전 처리)
-- ALTER TABLE user_exercise_info
--     DROP COLUMN exercise_type,
--     DROP COLUMN exercise_period,
--     DROP COLUMN pt_experience,
--     DROP COLUMN pt_satisfaction,
--     DROP COLUMN pt_dissatisfaction_reason;
```

---

## 어드민 전용 추가 테이블 DDL

아래 테이블들은 `linkfit` DB에 신규 생성된 어드민 전용 테이블이다.

### 실행 순서

1. `gym`
2. `admin_user`
3. `product`
4. `gym_setting`
5. `member_freeze`
6. `membership`
7. `class_session`
8. `class_attendee`
9. `attendance`
10. `consult`
11. `message`
12. `message_recipient`
13. `sale`

### DDL

```sql
-- ─────────────────────────────────────────────────────────────
-- gym (지점 — 다중 헬스장 지원)
-- branch_code: 영문 2자리 + 숫자 2자리 (예: LF01, GY02)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS gym (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    branch_code CHAR(4)         NOT NULL COMMENT '영문2+숫자2, e.g. LF01',
    name        VARCHAR(100)    NOT NULL,
    is_active   TINYINT(1)      NOT NULL DEFAULT 1,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_branch_code (branch_code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기본 지점 (최초 1회)
INSERT IGNORE INTO gym (branch_code, name) VALUES ('LF01', 'LINK_Fit 본점');

-- ─────────────────────────────────────────────────────────────
-- admin_user (어드민 로그인 계정 — users 테이블과 별개)
-- gym_id FK → gym.id (지점별 계정 분리)
-- username 유일성: 지점 내에서만 (uq_gym_username)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS admin_user (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    gym_id      BIGINT UNSIGNED NOT NULL,
    username    VARCHAR(50)     NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    name        VARCHAR(50)     NOT NULL,
    role        VARCHAR(20)     NOT NULL
                    CHECK (role IN ('SUPER_ADMIN','ADMIN','TRAINER')),
    is_active   TINYINT(1)      NOT NULL DEFAULT 1,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_gym_username (gym_id, username),
    CONSTRAINT fk_admin_gym FOREIGN KEY (gym_id) REFERENCES gym (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 슈퍼어드민 계정 (비밀번호: admin1234 → BCrypt, LF01 지점)
INSERT IGNORE INTO admin_user (gym_id, username, password, name, role)
SELECT id, 'admin', '$2b$10$f2PFkWjuVXSrtRcbONISbeM8tCNfLEVaTSLzPiQm0R7O4ElQbMv0u', '관리자', 'SUPER_ADMIN'
FROM gym WHERE branch_code = 'LF01';

-- ─────────────────────────────────────────────────────────────
-- product (상품)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS product (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    name            VARCHAR(100) NOT NULL,
    type            VARCHAR(20)  NOT NULL
                        CHECK (type IN ('MEMBERSHIP','GROUP','PT','LOCKER','ITEM')),
    price           INT          NOT NULL DEFAULT 0,
    description     TEXT,
    is_active       TINYINT(1)   NOT NULL DEFAULT 1,
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_product_type      (type),
    KEY idx_product_is_active (is_active)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- gym_setting (헬스장 기본 설정 — 단일 행, id=1 고정)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS gym_setting (
    id          TINYINT      NOT NULL DEFAULT 1,
    gym_name    VARCHAR(100),
    gym_phone   VARCHAR(20),
    gym_address VARCHAR(255),
    is_open     TINYINT(1)   NOT NULL DEFAULT 1,
    mon_open    VARCHAR(5),
    mon_close   VARCHAR(5),
    mon_closed  TINYINT(1)   NOT NULL DEFAULT 0,
    tue_open    VARCHAR(5),
    tue_close   VARCHAR(5),
    tue_closed  TINYINT(1)   NOT NULL DEFAULT 0,
    wed_open    VARCHAR(5),
    wed_close   VARCHAR(5),
    wed_closed  TINYINT(1)   NOT NULL DEFAULT 0,
    thu_open    VARCHAR(5),
    thu_close   VARCHAR(5),
    thu_closed  TINYINT(1)   NOT NULL DEFAULT 0,
    fri_open    VARCHAR(5),
    fri_close   VARCHAR(5),
    fri_closed  TINYINT(1)   NOT NULL DEFAULT 0,
    sat_open    VARCHAR(5),
    sat_close   VARCHAR(5),
    sat_closed  TINYINT(1)   NOT NULL DEFAULT 0,
    sun_open    VARCHAR(5),
    sun_close   VARCHAR(5),
    sun_closed  TINYINT(1)   NOT NULL DEFAULT 1,
    notice      TEXT,
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT chk_gym_setting_single_row CHECK (id = 1)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기본 데이터 삽입 (id=1 고정)
INSERT IGNORE INTO gym_setting (id, gym_name, gym_phone, gym_address, is_open,
    mon_open, mon_close, tue_open, tue_close, wed_open, wed_close,
    thu_open, thu_close, fri_open, fri_close, sat_open, sat_close,
    sat_closed, sun_closed)
VALUES (1, 'LINK_Fit 헬스장', '010-0000-0000', '서울시 강남구', 1,
    '09:00', '22:00', '09:00', '22:00', '09:00', '22:00',
    '09:00', '22:00', '09:00', '22:00', '09:00', '18:00',
    0, 1);

-- ─────────────────────────────────────────────────────────────
-- member_freeze (회원 유증 — users.user_id 참조)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS member_freeze (
    id           BIGINT   NOT NULL AUTO_INCREMENT,
    user_id      BIGINT UNSIGNED NOT NULL,
    freeze_start DATE     NOT NULL,
    freeze_end   DATE     NOT NULL,
    reason       VARCHAR(255),
    created_at   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_freeze_user (user_id),
    CONSTRAINT fk_freeze_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- membership (회원권 구매 이력 — users.user_id 참조)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS membership (
    id          BIGINT   NOT NULL AUTO_INCREMENT,
    user_id     BIGINT UNSIGNED NOT NULL,
    product_id  BIGINT,
    type        VARCHAR(20)  NOT NULL
                    CHECK (type IN ('MEMBERSHIP','GROUP','PT','LOCKER','ITEM')),
    start_date  DATE     NOT NULL,
    end_date    DATE,
    price       INT      NOT NULL DEFAULT 0,
    memo        VARCHAR(255),
    created_at  DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_membership_user (user_id),
    CONSTRAINT fk_membership_user    FOREIGN KEY (user_id)    REFERENCES users   (user_id) ON DELETE CASCADE,
    CONSTRAINT fk_membership_product FOREIGN KEY (product_id) REFERENCES product (id)      ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- class_session (그룹/PT 수업 — trainer_id → users.user_id(TRAINER))
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS class_session (
    id           BIGINT       NOT NULL AUTO_INCREMENT,
    title        VARCHAR(100) NOT NULL,
    type         VARCHAR(10)  NOT NULL
                     CHECK (type IN ('GROUP','PT','OT')),
    category     VARCHAR(20)  NOT NULL
                     CHECK (category IN ('헬스','필라테스','골프','기타')),
    trainer_id   BIGINT UNSIGNED,
    trainer_name VARCHAR(50),
    class_date   DATE         NOT NULL,
    start_time   TIME         NOT NULL,
    end_time     TIME         NOT NULL,
    capacity     INT          NOT NULL DEFAULT 1,
    enrolled     INT          NOT NULL DEFAULT 0,
    status       VARCHAR(20)  NOT NULL DEFAULT 'OPEN'
                     CHECK (status IN ('OPEN','CANCELLED')),
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_class_date    (class_date),
    KEY idx_class_trainer (trainer_id),
    KEY idx_class_status  (status),
    CONSTRAINT fk_class_trainer FOREIGN KEY (trainer_id) REFERENCES users (user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- class_attendee (수업 신청자)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS class_attendee (
    id               BIGINT   NOT NULL AUTO_INCREMENT,
    class_session_id BIGINT   NOT NULL,
    user_id          BIGINT UNSIGNED NOT NULL,
    registered_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    attended         TINYINT(1) NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    UNIQUE KEY uq_class_attendee (class_session_id, user_id),
    KEY idx_attendee_user (user_id),
    CONSTRAINT fk_attendee_class FOREIGN KEY (class_session_id) REFERENCES class_session (id)     ON DELETE CASCADE,
    CONSTRAINT fk_attendee_user  FOREIGN KEY (user_id)          REFERENCES users         (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- attendance (출석 체크)
-- type 기본값: GENERAL (앱 일반 출석)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS attendance (
    id             BIGINT   NOT NULL AUTO_INCREMENT,
    user_id        BIGINT UNSIGNED NOT NULL,
    type           VARCHAR(20)  NOT NULL DEFAULT 'GENERAL',
    attend_date    DATE     NOT NULL,
    check_in_time  TIME     NOT NULL,
    created_at     DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_attendance_user (user_id),
    KEY idx_attendance_date (attend_date),
    CONSTRAINT fk_attendance_user FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- consult (상담 — user_id는 기존 회원 상담 시에만 설정)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS consult (
    id            BIGINT   NOT NULL AUTO_INCREMENT,
    type          VARCHAR(20)  NOT NULL
                      CHECK (type IN ('NEW','EXISTING')),
    name          VARCHAR(50)  NOT NULL,
    phone         VARCHAR(20),
    gender        VARCHAR(10)
                      CHECK (gender IN ('남자','여자')),
    user_id       BIGINT UNSIGNED,
    interest      VARCHAR(100),
    content       TEXT,
    result        VARCHAR(20)  NOT NULL DEFAULT 'PENDING'
                      CHECK (result IN ('REGISTERED','PENDING','NO_SHOW')),
    consult_date  DATE     NOT NULL,
    staff_id      BIGINT UNSIGNED,
    staff_name    VARCHAR(50),
    created_at    DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_consult_date   (consult_date),
    KEY idx_consult_result (result),
    CONSTRAINT fk_consult_user  FOREIGN KEY (user_id)  REFERENCES users (user_id) ON DELETE SET NULL,
    CONSTRAINT fk_consult_staff FOREIGN KEY (staff_id) REFERENCES users (user_id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- message (메시지 발송)
-- type: SMS(기본값), status: DRAFT(기본값)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS message (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    title       VARCHAR(200) NOT NULL,
    content     TEXT         NOT NULL,
    type        VARCHAR(20)  NOT NULL DEFAULT 'SMS',
    status      VARCHAR(20)  NOT NULL DEFAULT 'DRAFT'
                    CHECK (status IN ('DRAFT','SENT','FAILED')),
    sent_at     DATETIME,
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_message_status  (status),
    KEY idx_message_sent_at (sent_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- message_recipient (메시지 수신자)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS message_recipient (
    id          BIGINT UNSIGNED NOT NULL AUTO_INCREMENT,
    message_id  BIGINT          NOT NULL,
    user_id     BIGINT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_msg_recipient (message_id, user_id),
    CONSTRAINT fk_msg_recipient_message FOREIGN KEY (message_id) REFERENCES message (id)     ON DELETE CASCADE,
    CONSTRAINT fk_msg_recipient_user    FOREIGN KEY (user_id)    REFERENCES users   (user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- sale (매출 내역)
-- product_name: 결제 시점 상품명 스냅샷 (product 삭제 후에도 보존)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS sale (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    user_id         BIGINT UNSIGNED,
    product_id      BIGINT,
    product_name    VARCHAR(100)    NOT NULL,
    product_type    VARCHAR(20)     NOT NULL
                        CHECK (product_type IN ('MEMBERSHIP','GROUP','PT','LOCKER','ITEM')),
    amount          INT             NOT NULL,
    payment_method  VARCHAR(20)     NOT NULL DEFAULT 'CARD'
                        CHECK (payment_method IN ('CARD','CASH','TRANSFER')),
    sale_date       DATE            NOT NULL,
    memo            VARCHAR(255),
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_sale_date (sale_date),
    KEY idx_sale_user (user_id),
    CONSTRAINT fk_sale_user    FOREIGN KEY (user_id)    REFERENCES users    (user_id) ON DELETE SET NULL,
    CONSTRAINT fk_sale_product FOREIGN KEY (product_id) REFERENCES product  (id)      ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 테이블 관계 요약

```
gym (지점)
└─ admin_user (gym_id) — 지점별 관리자 계정

gym_setting (단일 행, id=1 고정)

users (role=MEMBER)  ←→  user_profiles
    ├─ member_freeze (user_id)
    ├─ membership (user_id)  ──▶ product
    ├─ attendance (user_id)
    ├─ class_attendee (user_id)  ──▶ class_session ──▶ users(TRAINER, trainer_id)
    ├─ consult (user_id)
    └─ message_recipient (user_id)  ──▶ message

sale
    ├─ users (user_id)
    └─ product (product_id)
```


> 마이그레이션 이력은 `docs/db.md` 를 참고.

---

## CRM 전용 테이블 DDL (crm_* prefix)

> 앱 테이블에 FK 없이 논리적 참조만 사용.
> `member_id VARCHAR(50)` = `users.user_id` 참조.
> `gym_id BIGINT UNSIGNED` = `gym.id` 참조.
> 모든 CRM 테이블 PK = `CHAR(36)` UUID (애플리케이션에서 `UUID.randomUUID().toString()` 생성).

### 실행 순서

1. `crm_users`
2. `crm_member_assignments`
3. `crm_member_tags`
4. `crm_member_notes`
5. `crm_membership_history`
6. `crm_pt_registration_type`
7. `crm_feedback_tickets`
8. `crm_ticket_settings`
9. `crm_ticket_purchases`
10. `crm_ticket_inventory`
11. `crm_feedback_requests`
12. `crm_messages`
13. `crm_cs_tickets`
14. `crm_sales`
15. `crm_sales_targets`
16. `crm_re_registration`
17. `crm_announcements`
18. `crm_daily_stats`

### DDL

```sql
-- ─────────────────────────────────────────────────────────────
-- crm_users (CRM 계정 — admin_user 대체 / JWT 인증용)
-- role: super_admin (플랫폼 전체), gym_admin (헬스장 관리자), trainer (트레이너)
-- app_user_id: 앱 users.user_id 연결 (트레이너 계정 연동 시 선택적 사용)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_users (
    id            CHAR(36)        NOT NULL,
    gym_id        BIGINT UNSIGNED NOT NULL,
    app_user_id   VARCHAR(50)     NULL                        COMMENT '앱 users.user_id 연결 (트레이너용)',
    name          VARCHAR(50)     NOT NULL,
    email         VARCHAR(100)    NULL,
    username      VARCHAR(50)     NOT NULL,
    password_hash TEXT            NOT NULL,
    role          ENUM('super_admin','gym_admin','trainer')  NOT NULL,
    is_active     TINYINT(1)      NOT NULL DEFAULT 1,
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_crm_gym_username (gym_id, username),
    KEY idx_crm_users_gym (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- admin_user 데이터 이관 (최초 1회)
INSERT IGNORE INTO crm_users (id, gym_id, name, email, username, password_hash, role, is_active, created_at)
SELECT
    UUID(),
    a.gym_id,
    a.name,
    CONCAT(a.username, '@', g.branch_code, '.crm.local'),
    a.username,
    a.password,
    CASE a.role
        WHEN 'SUPER_ADMIN' THEN 'super_admin'
        WHEN 'ADMIN'       THEN 'gym_admin'
        WHEN 'TRAINER'     THEN 'trainer'
        ELSE 'gym_admin'
    END,
    a.is_active,
    a.created_at
FROM admin_user a
JOIN gym g ON a.gym_id = g.id;

-- ─────────────────────────────────────────────────────────────
-- Sector 2 — 회원 관리 보조 테이블
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_member_assignments (
    id          CHAR(36)        NOT NULL,
    member_id   VARCHAR(50)     NOT NULL                     COMMENT 'users.user_id',
    trainer_id  CHAR(36)        NOT NULL                     COMMENT 'crm_users.id',
    gym_id      BIGINT UNSIGNED NOT NULL,
    assigned_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cma_member_gym (member_id, gym_id),
    KEY idx_cma_trainer (trainer_id),
    KEY idx_cma_gym     (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crm_member_tags (
    id         CHAR(36)        NOT NULL,
    member_id  VARCHAR(50)     NOT NULL,
    gym_id     BIGINT UNSIGNED NOT NULL,
    tag        VARCHAR(30)     NOT NULL,
    color      CHAR(7)         NULL                          COMMENT 'HEX 색상 e.g. #FF5733',
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cmt_member (member_id),
    KEY idx_cmt_gym    (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crm_member_notes (
    id         CHAR(36)        NOT NULL,
    member_id  VARCHAR(50)     NOT NULL,
    gym_id     BIGINT UNSIGNED NOT NULL,
    author_id  CHAR(36)        NULL                          COMMENT 'crm_users.id',
    content    TEXT            NOT NULL,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cmn_member (member_id),
    KEY idx_cmn_gym    (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 3 — 이용권 변경 이력
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_membership_history (
    id           CHAR(36)        NOT NULL,
    member_id    VARCHAR(50)     NOT NULL,
    gym_id       BIGINT UNSIGNED NOT NULL,
    action       ENUM('pause','extend','change','cancel')   NOT NULL,
    reason       TEXT            NULL,
    processed_by CHAR(36)        NULL                       COMMENT 'crm_users.id',
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cmh_member (member_id),
    KEY idx_cmh_gym    (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 4 — PT 등록 유형 (신규/재등록/소개)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_pt_registration_type (
    id            CHAR(36)        NOT NULL,
    member_id     VARCHAR(50)     NOT NULL,
    gym_id        BIGINT UNSIGNED NOT NULL,
    pt_package_id BIGINT UNSIGNED NULL                      COMMENT 'user_subscriptions.id 논리참조',
    reg_type      ENUM('new','re','referral')               NOT NULL,
    referrer_id   VARCHAR(50)     NULL                      COMMENT '소개자 users.user_id',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cprt_member (member_id),
    KEY idx_cprt_gym    (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 7 — 피드백 티켓
-- month_year: 'YYYY-MM' 형식
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_feedback_tickets (
    id          CHAR(36)        NOT NULL,
    member_id   VARCHAR(50)     NOT NULL,
    gym_id      BIGINT UNSIGNED NOT NULL,
    trainer_id  CHAR(36)        NULL                        COMMENT 'crm_users.id',
    ticket_type ENUM('free','paid')                         NOT NULL DEFAULT 'free',
    status      ENUM('issued','used','expired','pending')   NOT NULL DEFAULT 'issued',
    issued_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at     DATETIME        NULL,
    expires_at  DATETIME        NULL,
    month_year  CHAR(7)         NOT NULL                    COMMENT 'YYYY-MM',
    PRIMARY KEY (id),
    KEY idx_cft_member     (member_id),
    KEY idx_cft_gym        (gym_id),
    KEY idx_cft_trainer    (trainer_id),
    KEY idx_cft_month_year (month_year),
    KEY idx_cft_status     (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 8 — 티켓 발행 설정 (헬스장별 1행)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_ticket_settings (
    id                      CHAR(36)        NOT NULL,
    gym_id                  BIGINT UNSIGNED NOT NULL,
    free_tickets_per_member INT             NOT NULL DEFAULT 2,
    max_tickets_per_month   INT             NULL,
    is_beta                 TINYINT(1)      NOT NULL DEFAULT 1,
    updated_at              DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by              CHAR(36)        NULL                               COMMENT 'crm_users.id',
    PRIMARY KEY (id),
    UNIQUE KEY uq_cts_gym (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 기본 설정 삽입 (gym 테이블의 각 지점에 대해)
INSERT IGNORE INTO crm_ticket_settings (id, gym_id)
SELECT UUID(), id FROM gym;

-- ─────────────────────────────────────────────────────────────
-- Sector 9 — 티켓 구매 및 재고
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_ticket_purchases (
    id           CHAR(36)        NOT NULL,
    gym_id       BIGINT UNSIGNED NOT NULL,
    quantity     INT             NOT NULL,
    unit_price   DECIMAL(10,2)   NOT NULL,
    total_price  DECIMAL(10,2)   NOT NULL,
    purchased_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    purchased_by CHAR(36)        NULL                        COMMENT 'crm_users.id',
    PRIMARY KEY (id),
    KEY idx_ctp_gym (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crm_ticket_inventory (
    id         CHAR(36)        NOT NULL,
    gym_id     BIGINT UNSIGNED NOT NULL,
    total_qty  INT             NOT NULL DEFAULT 0,
    used_qty   INT             NOT NULL DEFAULT 0,
    updated_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cti_gym (gym_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

INSERT IGNORE INTO crm_ticket_inventory (id, gym_id)
SELECT UUID(), id FROM gym;

-- ─────────────────────────────────────────────────────────────
-- Sector 10 — 피드백 요청 (회원 앱 → CRM 처리)
-- attachments: JSON 배열 [{url, type: 'image'|'video'}]
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_feedback_requests (
    id           CHAR(36)        NOT NULL,
    ticket_id    CHAR(36)        NULL                        COMMENT 'crm_feedback_tickets.id',
    member_id    VARCHAR(50)     NOT NULL,
    gym_id       BIGINT UNSIGNED NOT NULL,
    trainer_id   CHAR(36)        NULL                        COMMENT 'crm_users.id',
    content      TEXT            NOT NULL,
    attachments  JSON            NULL,
    status       ENUM('pending','in_progress','completed','held') NOT NULL DEFAULT 'pending',
    response     TEXT            NULL,
    responded_at DATETIME        NULL,
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cfr_member  (member_id),
    KEY idx_cfr_gym     (gym_id),
    KEY idx_cfr_trainer (trainer_id),
    KEY idx_cfr_status  (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 11 — CRM 내부 쪽지
-- sender_id / receiver_id: member는 users.user_id(VARCHAR50), crm는 crm_users.id(CHAR36)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_messages (
    id            CHAR(36)        NOT NULL,
    gym_id        BIGINT UNSIGNED NOT NULL,
    sender_type   ENUM('member','trainer','admin')          NOT NULL,
    sender_id     VARCHAR(100)    NOT NULL,
    receiver_type ENUM('member','trainer','admin')          NOT NULL,
    receiver_id   VARCHAR(100)    NOT NULL,
    content       TEXT            NOT NULL,
    is_read       TINYINT(1)      NOT NULL DEFAULT 0,
    is_notice     TINYINT(1)      NOT NULL DEFAULT 0,
    parent_id     CHAR(36)        NULL                      COMMENT '답장 대상 crm_messages.id',
    created_at    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cm_gym      (gym_id),
    KEY idx_cm_receiver (receiver_id),
    KEY idx_cm_parent   (parent_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 12 — CS 티켓
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_cs_tickets (
    id           CHAR(36)        NOT NULL,
    gym_id       BIGINT UNSIGNED NOT NULL,
    member_id    VARCHAR(50)     NOT NULL,
    category     ENUM('app_error','membership','pt','feedback','payment','other') NOT NULL,
    title        VARCHAR(200)    NOT NULL,
    content      TEXT            NOT NULL,
    status       ENUM('received','checking','processing','answered','closed') NOT NULL DEFAULT 'received',
    assigned_to  CHAR(36)        NULL                       COMMENT 'crm_users.id',
    response     TEXT            NULL,
    responded_at DATETIME        NULL,
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cct_gym    (gym_id),
    KEY idx_cct_member (member_id),
    KEY idx_cct_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 14 — CRM 매출 (이용권·PT·피드백 티켓 통합)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_sales (
    id          CHAR(36)        NOT NULL,
    gym_id      BIGINT UNSIGNED NOT NULL,
    member_id   VARCHAR(50)     NOT NULL,
    sales_type  ENUM('membership','pt','feedback_ticket')   NOT NULL,
    reg_type    ENUM('new','re','referral')                 NULL,
    trainer_id  CHAR(36)        NULL                        COMMENT 'crm_users.id',
    amount      DECIMAL(12,2)   NOT NULL,
    sale_date   DATE            NOT NULL,
    note        TEXT            NULL,
    created_at  DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_cs_gym       (gym_id),
    KEY idx_cs_member    (member_id),
    KEY idx_cs_sale_date (sale_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS crm_sales_targets (
    gym_id     BIGINT UNSIGNED NOT NULL,
    month_year CHAR(7)         NOT NULL                     COMMENT 'YYYY-MM',
    target     DECIMAL(12,2)   NOT NULL,
    PRIMARY KEY (gym_id, month_year)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 15 — 재등록 관리 (이탈 위험 회원 자동 분류)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_re_registration (
    id           CHAR(36)        NOT NULL,
    member_id    VARCHAR(50)     NOT NULL,
    gym_id       BIGINT UNSIGNED NOT NULL,
    reason       ENUM('membership_expiry','pt_low','low_routine','low_app_usage','feedback_history') NOT NULL,
    status       ENUM('pending','in_progress','success','failed','hold') NOT NULL DEFAULT 'pending',
    assigned_to  CHAR(36)        NULL                       COMMENT 'crm_users.id',
    memo         TEXT            NULL,
    scheduled_at DATETIME        NULL,
    resolved_at  DATETIME        NULL,
    created_at   DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_crr_member (member_id),
    KEY idx_crr_gym    (gym_id),
    KEY idx_crr_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 16 — 공지/알림 발송
-- target_ids: JSON 배열 ["user_id1", "user_id2", ...]
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_announcements (
    id         CHAR(36)        NOT NULL,
    gym_id     BIGINT UNSIGNED NOT NULL,
    author_id  CHAR(36)        NULL                         COMMENT 'crm_users.id',
    target     ENUM('all_members','all_trainers','specific','all') NOT NULL,
    target_ids JSON            NULL,
    title      VARCHAR(200)    NOT NULL,
    content    TEXT            NOT NULL,
    send_push  TINYINT(1)      NOT NULL DEFAULT 0,
    sent_at    DATETIME        NULL,
    created_at DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY idx_ca_gym    (gym_id),
    KEY idx_ca_author (author_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ─────────────────────────────────────────────────────────────
-- Sector 17 — 일별 통계 집계 (배치로 매일 02:00 갱신)
-- ─────────────────────────────────────────────────────────────
CREATE TABLE IF NOT EXISTS crm_daily_stats (
    gym_id            BIGINT UNSIGNED NOT NULL,
    stat_date         DATE            NOT NULL,
    total_members     INT             NOT NULL DEFAULT 0,
    active_members    INT             NOT NULL DEFAULT 0,
    dormant_members   INT             NOT NULL DEFAULT 0,
    routine_completed INT             NOT NULL DEFAULT 0,
    attendance_rate   DECIMAL(5,2)    NOT NULL DEFAULT 0.00,
    feedback_issued   INT             NOT NULL DEFAULT 0,
    feedback_used     INT             NOT NULL DEFAULT 0,
    PRIMARY KEY (gym_id, stat_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## CRM 테이블 관계 요약

```
gym (지점)
├─ admin_user (gym_id)        ← 기존 (유지)
└─ crm_users (gym_id)         ← 신규 JWT 인증용

crm_users (CRM 계정)
├─ crm_member_assignments (trainer_id)
├─ crm_member_notes (author_id)
├─ crm_membership_history (processed_by)
├─ crm_feedback_tickets (trainer_id)
├─ crm_ticket_settings (updated_by)
├─ crm_ticket_purchases (purchased_by)
├─ crm_feedback_requests (trainer_id)
├─ crm_cs_tickets (assigned_to)
├─ crm_sales (trainer_id)
├─ crm_re_registration (assigned_to)
└─ crm_announcements (author_id)

users.user_id (앱 회원) ← 논리 참조 (FK 없음)
├─ crm_member_assignments (member_id)
├─ crm_member_tags (member_id)
├─ crm_member_notes (member_id)
├─ crm_membership_history (member_id)
├─ crm_pt_registration_type (member_id)
├─ crm_feedback_tickets (member_id)
├─ crm_feedback_requests (member_id)
├─ crm_cs_tickets (member_id)
├─ crm_sales (member_id)
└─ crm_re_registration (member_id)
```

---

## MyBatis 매퍼 쿼리

각 도메인별 Mapper 인터페이스 메서드와 대응하는 SQL.

---

### MemberMapper.xml

```xml
<mapper namespace="com.linkfit.admin.mapper.MemberMapper">

    <resultMap id="memberResultMap" type="Member">
        <id     property="id"         column="id"/>
        <result property="name"       column="name"/>
        <result property="email"      column="email"/>
        <result property="phone"      column="phone"/>
        <result property="gender"     column="gender"/>
        <result property="birthDate"  column="birth_date"/>
        <result property="memberType" column="member_type"/>
        <result property="status"     column="status"/>
        <result property="joinDate"   column="join_date"/>
    </resultMap>

    <!-- 목록: users + user_profiles JOIN, role=MEMBER -->
    <select id="findAll" resultMap="memberResultMap">
        SELECT u.user_id AS id, u.email,
               up.name, up.contact AS phone, up.gender, up.birth_date, up.member_type,
               CASE WHEN u.is_active = 1 THEN 'ACTIVE' ELSE 'SUSPENDED' END AS status,
               DATE(u.created_at) AS join_date
        FROM users u
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE u.role = 'MEMBER'
        <if test="keyword != null and keyword != ''">
            AND (up.name LIKE CONCAT('%', #{keyword}, '%')
              OR up.contact LIKE CONCAT('%', #{keyword}, '%'))
        </if>
        <if test="status != null and status != ''">
            AND u.is_active = #{status == 'ACTIVE' ? 1 : 0}
        </if>
        ORDER BY u.user_id DESC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <!-- 등록: users 먼저 insert → LAST_INSERT_ID()로 user_profiles insert -->
    <insert id="insertUser" useGeneratedKeys="true" keyProperty="id" keyColumn="user_id">
        INSERT INTO users (email, role, is_active, created_at)
        VALUES (#{email}, 'MEMBER', 1, NOW())
    </insert>

    <insert id="insertProfile">
        INSERT INTO user_profiles (user_id, name, contact, gender, birth_date, member_type)
        VALUES (#{id}, #{name}, #{phone}, #{gender}, #{birthDate}, #{memberType})
    </insert>

    <!-- 상태 변경: is_active = 1(ACTIVE) / 0(SUSPENDED) -->
    <update id="updateStatus">
        UPDATE users SET is_active = #{isActive} WHERE user_id = #{id}
    </update>

    <!-- 유증 등록 -->
    <insert id="insertFreeze">
        INSERT INTO member_freeze (user_id, freeze_start, freeze_end, reason)
        VALUES (#{memberId}, #{freezeStart}, #{freezeEnd}, #{reason})
    </insert>

    <!-- 유증 목록 -->
    <select id="findFreezeByMemberId" resultType="map">
        SELECT id, freeze_start, freeze_end, reason, created_at
        FROM member_freeze
        WHERE user_id = #{memberId}
        ORDER BY freeze_start DESC
    </select>

    <!-- 회원권 목록 -->
    <select id="findMembershipsByMemberId" resultType="map">
        SELECT ms.id, ms.type, ms.start_date, ms.end_date, ms.price, ms.memo,
               p.name AS product_name
        FROM membership ms
        LEFT JOIN product p ON ms.product_id = p.id
        WHERE ms.user_id = #{memberId}
        ORDER BY ms.start_date DESC
    </select>

    <!-- 회원권 등록 -->
    <insert id="insertMembership" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO membership (user_id, product_id, type, start_date, end_date, price, memo)
        VALUES (#{memberId}, #{productId}, #{type}, #{startDate}, #{endDate}, #{price}, #{memo})
    </insert>

</mapper>
```

---

### StaffMapper.xml

```xml
<mapper namespace="com.linkfit.admin.mapper.StaffMapper">

    <sql id="staffSelect">
        SELECT u.user_id AS id,
               up.name, up.contact AS phone,
               'TRAINER' AS role,
               CASE WHEN u.is_active = 1 THEN 'ACTIVE' ELSE 'INACTIVE' END AS status,
               DATE(u.created_at) AS hire_date
        FROM users u
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE u.role = 'TRAINER'
    </sql>

    <select id="findAll" resultMap="staffResultMap">
        <include refid="staffSelect"/>
        ORDER BY u.user_id DESC
        LIMIT #{size} OFFSET #{offset}
    </select>

    <!-- 등록: allowMultiQueries=true 필요 -->
    <insert id="insert" useGeneratedKeys="true" keyProperty="id" keyColumn="user_id">
        INSERT INTO users (email, role, is_active, created_at) VALUES (#{email}, 'TRAINER', 1, NOW());
        INSERT INTO user_profiles (user_id, name, contact) VALUES (LAST_INSERT_ID(), #{name}, #{phone})
    </insert>

</mapper>
```

---

### AttendanceMapper.xml

```xml
<mapper namespace="com.linkfit.admin.mapper.AttendanceMapper">

    <!-- 출석 목록: users + user_profiles JOIN -->
    <select id="findAll" resultMap="attendanceResultMap">
        SELECT a.id, a.user_id, up.name AS member_name, up.gender,
               a.type, a.attend_date, a.check_in_time
        FROM attendance a
        JOIN users u ON a.user_id = u.user_id
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE a.attend_date = #{date}
        ORDER BY a.check_in_time DESC
    </select>

    <!-- 체크인 -->
    <insert id="checkIn" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO attendance (user_id, type, attend_date, check_in_time)
        VALUES (#{memberId}, #{type}, #{attendDate}, #{checkInTime})
    </insert>

    <!-- 유증 회원 목록 -->
    <select id="findFrozen" resultType="map">
        SELECT u.user_id AS member_id, up.name, up.gender,
               f.freeze_start, f.freeze_end, f.reason
        FROM member_freeze f
        JOIN users u ON f.user_id = u.user_id
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE #{date} BETWEEN f.freeze_start AND f.freeze_end
        ORDER BY up.name ASC
    </select>

    <!-- 출석 통계: gender 값은 '남자'/'여자' -->
    <select id="countStats" resultType="map">
        SELECT
            COUNT(*) AS total,
            SUM(CASE WHEN up.gender = '남자' THEN 1 ELSE 0 END) AS male,
            SUM(CASE WHEN up.gender = '여자' THEN 1 ELSE 0 END) AS female
        FROM attendance a
        JOIN users u ON a.user_id = u.user_id
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE a.attend_date = #{date}
    </select>

</mapper>
```

---

### DashboardMapper.xml

```xml
<mapper namespace="com.linkfit.admin.mapper.DashboardMapper">

    <!-- 회원 현황: users + user_profiles JOIN -->
    <select id="memberStats" resultType="map">
        SELECT
            SUM(CASE WHEN u.is_active = 1 THEN 1 ELSE 0 END) AS active,
            SUM(CASE WHEN u.is_active = 0 THEN 1 ELSE 0 END) AS suspended,
            SUM(CASE WHEN u.is_active = 1 AND up.gender = '남자' THEN 1 ELSE 0 END) AS maleActive,
            SUM(CASE WHEN u.is_active = 1 AND up.gender = '여자' THEN 1 ELSE 0 END) AS femaleActive
        FROM users u
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE u.role = 'MEMBER'
    </select>

    <!-- 신규 회원 수 -->
    <select id="memberJoinStats" resultType="map">
        SELECT COUNT(*) AS newJoin
        FROM users u
        WHERE u.role = 'MEMBER'
        AND DATE(u.created_at) = #{date}
    </select>

    <!-- 매출 요약 -->
    <select id="revenueStats" resultType="map">
        SELECT
            SUM(CASE WHEN product_type = 'MEMBERSHIP' THEN amount ELSE 0 END) AS membership,
            SUM(CASE WHEN product_type = 'GROUP'      THEN amount ELSE 0 END) AS groupClass,
            SUM(CASE WHEN product_type = 'PT'         THEN amount ELSE 0 END) AS pt,
            SUM(CASE WHEN product_type = 'LOCKER'     THEN amount ELSE 0 END) AS locker,
            SUM(CASE WHEN product_type = 'ITEM'       THEN amount ELSE 0 END) AS items,
            SUM(amount) AS total
        FROM sale
        WHERE sale_date = #{date}
    </select>

    <!-- 매출 상세: sale + users + user_profiles JOIN -->
    <select id="revenueDetail" resultType="map">
        SELECT s.id, s.amount, s.payment_method, s.sale_date, s.memo,
               s.product_name, up.name AS member_name
        FROM sale s
        LEFT JOIN users u ON s.user_id = u.user_id
        LEFT JOIN user_profiles up ON u.user_id = up.user_id
        WHERE s.product_type = #{category}
        AND s.sale_date = #{date}
        ORDER BY s.id DESC
    </select>

</mapper>
```

---

### SaleMapper.xml

```xml
<mapper namespace="com.linkfit.admin.mapper.SaleMapper">

    <insert id="insert" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO sale (user_id, product_id, product_name, product_type, amount, payment_method, sale_date, memo)
        VALUES (#{userId}, #{productId}, #{productName}, #{productType}, #{amount}, #{paymentMethod}, #{saleDate}, #{memo})
    </insert>

</mapper>
```

---

### GymSettingMapper.xml

```xml
<mapper namespace="com.linkfit.admin.mapper.GymSettingMapper">

    <select id="find" resultMap="gymSettingResultMap">
        SELECT * FROM gym_setting WHERE id = 1
    </select>

    <!-- id=1 고정, INSERT ... ON DUPLICATE KEY UPDATE -->
    <insert id="upsert">
        INSERT INTO gym_setting (id, gym_name, gym_phone, gym_address, is_open,
            mon_open, mon_close, mon_closed, tue_open, tue_close, tue_closed,
            wed_open, wed_close, wed_closed, thu_open, thu_close, thu_closed,
            fri_open, fri_close, fri_closed, sat_open, sat_close, sat_closed,
            sun_open, sun_close, sun_closed, notice)
        VALUES (1, #{gymName}, #{gymPhone}, #{gymAddress}, #{isOpen},
            #{monOpen}, #{monClose}, #{monClosed}, #{tueOpen}, #{tueClose}, #{tueClosed},
            #{wedOpen}, #{wedClose}, #{wedClosed}, #{thuOpen}, #{thuClose}, #{thuClosed},
            #{friOpen}, #{friClose}, #{friClosed}, #{satOpen}, #{satClose}, #{satClosed},
            #{sunOpen}, #{sunClose}, #{sunClosed}, #{notice})
        ON DUPLICATE KEY UPDATE
            gym_name=#{gymName}, gym_phone=#{gymPhone}, gym_address=#{gymAddress}, is_open=#{isOpen},
            mon_open=#{monOpen}, mon_close=#{monClose}, mon_closed=#{monClosed},
            tue_open=#{tueOpen}, tue_close=#{tueClose}, tue_closed=#{tueClosed},
            wed_open=#{wedOpen}, wed_close=#{wedClose}, wed_closed=#{wedClosed},
            thu_open=#{thuOpen}, thu_close=#{thuClose}, thu_closed=#{thuClosed},
            fri_open=#{friOpen}, fri_close=#{friClose}, fri_closed=#{friClosed},
            sat_open=#{satOpen}, sat_close=#{satClose}, sat_closed=#{satClosed},
            sun_open=#{sunOpen}, sun_close=#{sunClose}, sun_closed=#{sunClosed},
            notice=#{notice}
    </insert>

    <update id="updateOpenStatus">
        UPDATE gym_setting SET is_open = #{isOpen} WHERE id = 1
    </update>

</mapper>
```
