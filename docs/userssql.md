# Users 관련 테이블 스키마

총 16개 테이블 (앱 유저 8개, CRM/관리자 2개, 부가정보 6개)

---

## 1. users (앱 유저 계정 - 핵심)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | 내부 순번 (UNIQUE) |
| `user_id` | varchar(50) | NO | - | **PK**, 로그인 식별자 |
| `email` | varchar(255) | YES | NULL | 이메일 (UNIQUE) |
| `nickname` | varchar(50) | YES | NULL | 닉네임 |
| `role` | enum | NO | `MEMBER` | `MEMBER` / `TRAINER` / `ADMIN` |
| `grade` | varchar(20) | NO | `BASIC` | `BASIC` / `MEMBERSHIP` / `PREMIUM` / `VIP` |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `deleted_at` | datetime | YES | NULL | 소프트 삭제 시각 |
| `created_at` | datetime | NO | current_timestamp | 가입일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

## 2. user_auth (소셜/비밀번호 인증)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `provider` | varchar(20) | NO | - | 인증 제공자 (예: kakao, google, local) |
| `provider_uid` | varchar(255) | YES | NULL | 소셜 고유 ID |
| `password_hash` | varchar(255) | YES | NULL | 비밀번호 해시 (local 로그인용) |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

**UNIQUE**: `(provider, provider_uid)`

---

## 3. user_profiles (프로필 상세)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `user_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `name` | varchar(50) | NO | - | 실명 |
| `name_en` | varchar(100) | YES | NULL | 영문 이름 |
| `birth_date` | date | YES | NULL | 생년월일 |
| `gender` | enum | YES | NULL | `남자` / `여자` |
| `contact` | varchar(20) | YES | NULL | 연락처 |
| `address` | varchar(255) | YES | NULL | 주소 |
| `occupation` | varchar(100) | YES | NULL | 직업 |
| `profile_email` | varchar(100) | YES | NULL | 프로필용 이메일 (users.email과 별개) |
| `profile_image_url` | varchar(512) | YES | NULL | 프로필 이미지 URL |
| `profile_image_key` | varchar(256) | YES | NULL | 프로필 이미지 S3 키 |
| `photo_url` | varchar(512) | YES | NULL | 사진 URL (트레이너용) |
| `height` | varchar(10) | YES | NULL | 키 (cm) |
| `weight` | varchar(10) | YES | NULL | 체중 (kg) |
| `tier` | enum | NO | `BASIC` | `BASIC` / `LIGHT_FIT` / `REGULAR_FIT` / `INTENSIVE_FIT` |
| `member_type` | enum | YES | `PT` | `PT` / `OT` |
| `pt_sessions_left` | int(11) | NO | 0 | 잔여 PT 횟수 |
| `trainer_id` | varchar(50) | YES | NULL | FK → users.user_id (SET NULL), 담당 트레이너 |
| `referral_name` | varchar(50) | YES | NULL | 소개자 이름 |
| `naver_keyword` | varchar(100) | YES | NULL | 네이버 유입 키워드 |
| `education` | varchar(255) | YES | NULL | 학력 (트레이너용) |
| `career` | longtext (JSON) | YES | NULL | 경력 JSON (트레이너용) |
| `qualifications` | longtext (JSON) | YES | NULL | 자격증 JSON (트레이너용) |
| `awards` | longtext (JSON) | YES | NULL | 수상 JSON (트레이너용) |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `status_updated_at` | timestamp | YES | NULL | 상태 변경 시각 |

---

## 4. user_subscriptions (구독/플랜)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `user_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `plan` | varchar(20) | NO | `free` | 플랜 코드 |
| `plan_name` | varchar(50) | NO | `무료` | 플랜 표시명 |
| `status` | varchar(20) | NO | `active` | 구독 상태 |
| `start_date` | date | YES | NULL | 구독 시작일 |
| `end_date` | date | YES | NULL | 구독 종료일 |
| `auto_renew` | tinyint(1) | NO | 0 | 자동 갱신 여부 |
| `sessions_left` | int(11) | YES | NULL | 잔여 세션 수 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

## 5. user_token_balance (토큰 잔액)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `user_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `balance` | int(11) | NO | 0 | 보유 토큰 수량 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

## 6. user_gym (유저-헬스장 연결)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | 논리 FK → users.user_id |
| `gym_id` | bigint(20) unsigned | NO | - | 논리 FK → gym.id |
| `joined_at` | datetime | NO | current_timestamp | 등록일 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |

**UNIQUE**: `(user_id, gym_id)`  
※ 물리적 FK 없음 (논리 참조)

---

## 7. user_terms_agreements (약관 동의)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `terms_agreed` | tinyint(1) | NO | 0 | 이용약관 동의 여부 |
| `privacy_agreed` | tinyint(1) | NO | 0 | 개인정보 동의 여부 |
| `terms_version` | varchar(20) | NO | - | 약관 버전 (예: 2026-06-17) |
| `terms_agreed_at` | timestamp | NO | current_timestamp | 동의 일시 |
| `created_at` | timestamp | NO | current_timestamp | 생성일 |

---

## 8. user_daily_habits (생활 습관)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `user_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `wake_up` | varchar(20) | YES | NULL | 기상 시간 |
| `bedtime` | varchar(20) | YES | NULL | 취침 시간 |
| `drinking_freq` | varchar(50) | YES | NULL | 음주 빈도 |
| `water_intake` | varchar(50) | YES | NULL | 하루 수분 섭취량 |
| `meal_pattern` | enum | YES | NULL | `규칙` / `불규칙` / `기타` |
| `meal_pattern_other` | varchar(100) | YES | NULL | 식사 패턴 기타 내용 |
| `daily_note` | text | YES | NULL | 생활 관련 메모 |

---

## 9. user_exercise_info (운동 정보)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `user_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `plan_frequency` | varchar(20) | YES | NULL | 주간 운동 계획 횟수 |
| `hard_point` | text | YES | NULL | 어려운 점 / 불편 부위 |
| `exercise_note` | text | YES | NULL | 운동 관련 메모 |
| `trainer_note` | text | YES | NULL | 트레이너 메모 |

---

## 10. user_exercise_purposes (운동 목적 - 다중값)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `purpose` | varchar(50) | NO | - | 운동 목적 (예: 다이어트, 근력 향상 등) |

---

## 11. user_favorite_exercises (즐겨찾기 운동)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `exercise_id` | int(10) unsigned | NO | - | FK → exercises.id (CASCADE) |
| `created_at` | datetime | NO | current_timestamp | 등록일 |

**UNIQUE**: `(user_id, exercise_id)`

---

## 12. user_medical_history (병력)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `diagnosis` | varchar(50) | NO | - | 진단명/병력 구분 |
| `other_detail` | varchar(255) | YES | NULL | 기타 상세 |
| `adult_disease_detail` | varchar(255) | YES | NULL | 성인병 상세 |
| `joint_detail` | varchar(255) | YES | NULL | 관절 상세 |
| `leg_detail` | varchar(255) | YES | NULL | 다리 상세 |
| `arm_detail` | varchar(255) | YES | NULL | 팔 상세 |
| `leg_arm_detail` | varchar(255) | YES | NULL | 팔다리 통합 상세 (구버전) |

---

## 13. user_medical_conditions (현재 질환 - 다중값)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `condition_name` | varchar(100) | NO | - | 질환명 |

---

## 14. user_visit_routes (유입 경로 - 다중값)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `route` | varchar(50) | NO | - | 유입 경로 (예: 인스타그램, 지인 소개 등) |

---

## 15. crm_users (CRM 시스템 유저)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | **PK** (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | FK → gym.id |
| `app_user_id` | varchar(50) | YES | NULL | 앱 users.user_id 연결 (트레이너용) |
| `name` | varchar(50) | NO | - | 이름 |
| `email` | varchar(100) | YES | NULL | 이메일 |
| `username` | varchar(50) | NO | - | 로그인 아이디 |
| `password_hash` | text | NO | - | 비밀번호 해시 |
| `role` | enum | NO | - | `super_admin` / `gym_admin` / `trainer` |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

**UNIQUE**: `(gym_id, username)`

---

## 16. admin_user (관리자 계정)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `gym_id` | bigint(20) unsigned | NO | - | FK → gym.id |
| `username` | varchar(50) | NO | - | 로그인 아이디 |
| `password` | varchar(255) | NO | - | 비밀번호 |
| `name` | varchar(50) | NO | - | 이름 |
| `role` | varchar(20) | NO | `ADMIN` | 역할 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

**UNIQUE**: `(gym_id, username)`

---

## 테이블 관계 요약

```
users (user_id PK)
├── user_auth              1:N  소셜/비밀번호 인증 (provider별)
├── user_profiles          1:1  프로필 상세 + 트레이너 전용 필드
├── user_subscriptions     1:1  구독 플랜
├── user_token_balance     1:1  토큰 잔액
├── user_gym               N:M  소속 헬스장 (gym과 연결)
├── user_terms_agreements  1:N  약관 동의 이력
├── user_daily_habits      1:1  생활 습관
├── user_exercise_info     1:1  운동 정보/트레이너 메모
├── user_exercise_purposes 1:N  운동 목적 (다중 선택)
├── user_favorite_exercises 1:N 즐겨찾기 운동 (exercises 테이블 참조)
├── user_medical_history   1:N  병력 (진단별 행)
├── user_medical_conditions 1:N 현재 질환 (다중 선택)
└── user_visit_routes      1:N  유입 경로 (다중 선택)

crm_users  - CRM 전용 계정 (gym_admin, trainer), app_user_id로 users 논리 연결
admin_user - 헬스장 관리자 계정 (gym.id FK)
```
