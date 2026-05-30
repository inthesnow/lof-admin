# LinkFit Database Schema

> DB: `linkfit` (MariaDB)  
> 기본 키 타입: `users.user_id VARCHAR(50)` = loginId (PK로 사용)  
> `users.id BIGINT` = numeric PK (외부 노출 없음, 내부 참조용)

---

## 목차

1. [사용자 (User)](#1-사용자-user)
2. [트레이너 (Trainer)](#2-트레이너-trainer)
3. [멤버십 · 구독 · 티켓](#3-멤버십--구독--티켓)
4. [원포인트 카드](#4-원포인트-카드)
5. [운동 마스터](#5-운동-마스터)
6. [운동 기록](#6-운동-기록)
7. [식단 기록](#7-식단-기록)
8. [피드백](#8-피드백)
9. [루틴](#9-루틴)
10. [커뮤니티 게시판](#10-커뮤니티-게시판)
11. [쪽지 (채팅)](#11-쪽지-채팅)
12. [알림](#12-알림)
13. [헬스장 · 출석 · 상담](#13-헬스장--출석--상담)
14. [기타 · 관리자](#14-기타--관리자)

---

## 1. 사용자 (User)

### `users` — 계정 기본 정보

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | VARCHAR(50) PK | loginId (JWT subject, 모든 FK에서 참조) |
| `id` | BIGINT UNI AUTO_INCREMENT | numeric PK (내부 참조용) |
| `email` | VARCHAR(255) UNI | 이메일 |
| `role` | ENUM('MEMBER','TRAINER','ADMIN') | 권한 |
| `nickname` | VARCHAR(50) | 닉네임 |
| `is_active` | TINYINT(1) | 활성 여부 (기본 1) |
| `deleted_at` | DATETIME | soft delete 시각 |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `user_auth` — 인증 정보

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `provider` | ENUM('email','google','naver','kakao') | 로그인 방식 |
| `provider_uid` | VARCHAR(255) | 소셜 UID |
| `password_hash` | VARCHAR(255) | bcrypt 해시 (email 전용) |
| `created_at` | DATETIME | |

### `user_profiles` — 프로필 상세 (회원 + 트레이너 공용)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | VARCHAR(50) PK FK→users | |
| `name` | VARCHAR(50) | 실명 |
| `birth_date` | DATE | |
| `gender` | ENUM('남자','여자') | |
| `contact` | VARCHAR(20) | 연락처 |
| `profile_email` | VARCHAR(100) | 공개용 이메일 |
| `photo_url` | VARCHAR(512) | 프로필 사진 URL |
| `tier` | ENUM('BASIC','LIGHT_FIT','REGULAR_FIT','INTENSIVE_FIT') | 구독 등급 (기본 BASIC) |
| `trainer_id` | VARCHAR(50) FK→users | 담당 트레이너 |
| `member_type` | ENUM('PT','OT') | 회원권 종류 |
| `pt_sessions_left` | INT | PT 잔여 횟수 |
| `address` | VARCHAR(255) | |
| `occupation` | VARCHAR(100) | |
| `referral_name` | VARCHAR(50) | 추천인 |
| `naver_keyword` | VARCHAR(100) | 유입 키워드 |
| `education` | VARCHAR(255) | 트레이너 학력 |
| `career` | LONGTEXT (JSON) | 트레이너 경력 |
| `qualifications` | LONGTEXT (JSON) | 트레이너 자격증 |
| `awards` | LONGTEXT (JSON) | 트레이너 수상 |
| `name_en` | VARCHAR(100) | 영문 이름 (트레이너) |

> **Tier 매핑**: API 응답에서는 단축 코드 사용  
> `BASIC` → `BASIC` / `LIGHT_FIT` → `LIGHT` / `REGULAR_FIT` → `REGULAR` / `INTENSIVE_FIT` → `INTENSIVE`

### `user_daily_habits` — 생활 습관

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | VARCHAR(50) PK FK→users | |
| `wake_up` | VARCHAR(20) | 기상 시간 |
| `bedtime` | VARCHAR(20) | 취침 시간 |
| `drinking_freq` | VARCHAR(50) | 음주 빈도 |
| `water_intake` | VARCHAR(50) | 수분 섭취 |
| `meal_pattern` | ENUM('규칙','불규칙','기타') | |
| `meal_pattern_other` | VARCHAR(100) | 기타 상세 |
| `daily_note` | TEXT | 메모 |

### `user_exercise_info` — 운동 정보

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | VARCHAR(50) PK FK→users | |
| `plan_frequency` | VARCHAR(20) | 주 운동 횟수 |
| `exercise_note` | TEXT | 운동 특이사항 |

### `user_exercise_purposes` — 운동 목적 (다중)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `purpose` | VARCHAR(50) | 목적 (체중감량, 근력강화 등) |

### `user_medical_history` — 병력

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `diagnosis` | VARCHAR(50) | 진단명 |
| `other_detail` | VARCHAR(255) | 기타 상세 |
| `adult_disease_detail` | VARCHAR(255) | 성인병 상세 |
| `joint_detail` | VARCHAR(255) | 관절 상세 |
| `leg_arm_detail` | VARCHAR(255) | 사지 상세 |

### `user_medical_conditions` — 현재 건강 상태 (다중)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `condition_name` | VARCHAR(100) | 상태명 |

### `user_visit_routes` — 유입 경로 (다중)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `route` | VARCHAR(50) | 경로명 |

### `user_favorite_exercises` — 즐겨찾기 운동

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `exercise_id` | INT UNSIGNED FK→exercise | |
| `created_at` | DATETIME | |

### `user_token_balance` — 토큰 잔액

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | VARCHAR(50) PK FK→users | |
| `balance` | INT | 토큰 잔액 (기본 0) |
| `updated_at` | DATETIME | |

---

## 2. 트레이너 (Trainer)

### `trainer_members` — 트레이너-회원 배정

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `member_id` | VARCHAR(50) FK→users | |
| `member_type` | ENUM('PT','ONE_POINT') | 배정 유형 |
| `assigned_at` | DATETIME | 배정 시각 |

### `trainer_member_memo` — 트레이너의 회원 메모

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `member_id` | VARCHAR(50) FK→users | |
| `content` | TEXT | 메모 내용 |
| `updated_at` | DATETIME | |

### `trainer_schedules` — 트레이너 수업 일정

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `member_id` | VARCHAR(50) FK→users | NULL 가능 (미배정 슬롯) |
| `type` | ENUM('PT','ONE_POINT') | 수업 유형 |
| `schedule_date` | DATE | |
| `start_time` | TIME | |
| `end_time` | TIME | |
| `memo` | VARCHAR(500) | |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `trainer_schedule_settings` — 트레이너 스케줄 설정

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `trainer_id` | VARCHAR(50) PK FK→users | |
| `start_hour` | TINYINT | 업무 시작 시 (기본 0) |
| `end_hour` | TINYINT | 업무 종료 시 (기본 24) |
| `slot_minutes` | TINYINT | 슬롯 단위 분 (기본 60) |
| `updated_at` | DATETIME | |

### `trainer_reviews` — 트레이너 리뷰

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `review_id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `member_id` | VARCHAR(50) FK→users | 작성자 |
| `rating` | DECIMAL(2,1) | 평점 (1.0~5.0) |
| `content` | TEXT | 리뷰 내용 |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `trainer_review_likes` — 리뷰 좋아요

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `like_id` | BIGINT PK | |
| `review_id` | BIGINT UNSIGNED FK→trainer_reviews | |
| `user_id` | VARCHAR(50) FK→users | |
| `created_at` | DATETIME | |

### `trainer_onepoint_quota` — 트레이너 원포인트 월별 발급 쿼터

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `trainer_id` | VARCHAR(50) PK FK→users | |
| `issuable_remaining` | INT | 잔여 발급 가능 횟수 (매월 1일 5로 초기화) |
| `updated_at` | DATETIME | |

### `trainer_token` — 트레이너 토큰 잔액

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `trainer_id` | VARCHAR(50) PK FK→users | |
| `balance` | INT | 토큰 잔액 |

### `trainer_comment` — 트레이너 운동 코멘트

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `record_id` | BIGINT UNSIGNED FK→exercise_records | |
| `exercise_id` | INT UNSIGNED FK→exercise | |
| `content` | TEXT | |
| `on_site_recommended` | TINYINT(1) | 현장 추천 여부 |
| `created_at` | DATETIME | |

---

## 3. 멤버십 · 구독 · 티켓

### `membership` — 헬스장 멤버십 계약

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `product_id` | BIGINT UNSIGNED FK→product | |
| `type` | VARCHAR(30) | 멤버십 종류 |
| `start_date` | DATE | |
| `end_date` | DATE | |
| `price` | INT | 계약 금액 |
| `memo` | TEXT | |
| `created_at` | DATETIME | |

### `member_freeze` — 멤버십 정지

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `freeze_start` | DATE | |
| `freeze_end` | DATE | |
| `reason` | VARCHAR(255) | |
| `created_at` | DATETIME | |

### `subscription_plans` — 구독 플랜 마스터

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `plan_code` | VARCHAR(20) UNI | 'LIGHT' / 'REGULAR' / 'INTENSIVE' |
| `name` | VARCHAR(50) | 플랜명 |
| `price` | INT | 월 요금 (원) |
| `session_count` | INT | PT 세션 수 |
| `description` | VARCHAR(200) | |
| `feedback_monthly_count` | INT | 월 피드백 티켓 수 |
| `onepoint_monthly_count` | INT | 월 원포인트 티켓 수 |
| `photo_monthly_count` | INT | 월 사진 티켓 수 |
| `video_monthly_count` | INT | 월 영상 티켓 수 |
| `created_at` | DATETIME | |

### `user_subscriptions` — 회원 구독 현황

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `user_id` | VARCHAR(50) PK FK→users | |
| `plan` | VARCHAR(20) | plan_code (기본 'free') |
| `plan_name` | VARCHAR(50) | |
| `status` | VARCHAR(20) | 'active' / 'expired' / 'cancelled' |
| `start_date` | DATE | |
| `end_date` | DATE | |
| `auto_renew` | TINYINT(1) | 자동 갱신 여부 |
| `sessions_left` | INT | 잔여 세션 수 |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `member_tickets` — 회원 티켓 잔량

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `ticket_type` | ENUM('ONE_POINT','FEEDBACK','PHOTO','VIDEO') | |
| `remaining` | INT | 잔여 횟수 (-1 = 무제한) |
| `updated_at` | DATETIME | |

> 유저당 티켓 종류별 1행씩 존재. 없으면 0으로 간주.

### `ticket_logs` — 티켓 변동 이력

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `ticket_type` | ENUM('ONE_POINT','FEEDBACK','PHOTO','VIDEO') | |
| `action_type` | ENUM('USE','CHARGE','EXPIRE','GIFT') | |
| `description` | VARCHAR(100) | 설명 |
| `created_at` | DATETIME | |

### `ticket_purchases` — 티켓 단품 구매 이력

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `product_id` | VARCHAR(30) | 'FEEDBACK_TICKET' / 'ONEPOINT_CARD' |
| `quantity` | INT | 구매 수량 |
| `price_krw` | INT | 결제 금액 (원) |
| `receipt_id` | VARCHAR(50) UNI | 영수증 ID |
| `payment_method` | VARCHAR(20) | 'CARD' 등 |
| `payment_token` | VARCHAR(255) | PG 결제 토큰 (mock 시 'MOCK-…') |
| `description` | VARCHAR(100) | |
| `created_at` | DATETIME | |

---

## 4. 원포인트 카드

### `onepoint_cards` — 원포인트 카드 발급 내역

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | 발급 트레이너 |
| `member_id` | VARCHAR(50) FK→users | 수령 회원 |
| `status` | ENUM('ISSUABLE','COMPLETED','CANCELLED') | 카드 상태 |
| `issued_at` | DATETIME | 발급 시각 |
| `completed_at` | DATETIME | 사용 완료 시각 |

### `onepoint_requests` — 원포인트 레슨 신청

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | |
| `member_id` | VARCHAR(50) FK→users | 신청 회원 |
| `trainer_id` | VARCHAR(50) FK→users | 담당 트레이너 |
| `conversation_id` | BIGINT | 연결 대화방 ID (옵션) |
| `preferred_dates` | LONGTEXT (JSON 배열) | 희망 날짜 목록 |
| `body_parts` | LONGTEXT (JSON 배열) | 신체 부위 목록 |
| `has_pain` | TINYINT(1) | 통증 여부 |
| `notes` | TEXT | 요청 메모 |
| `status` | ENUM('PENDING','ACCEPTED','CANCELLED','COMPLETED') | 상태 |
| `scheduled_date` | DATE | 확정 날짜 (트레이너 수락 시) |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

> CANCELLED 시 회원의 ONE_POINT 티켓 +1 환불

---

## 5. 운동 마스터

### `exercise` — 운동 종목 마스터

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | INT UNSIGNED PK | |
| `name_ko` | VARCHAR(100) UNI | 한국어 이름 |
| `name_en` | VARCHAR(100) UNI | 영문 이름 |
| `primary_body_part_id` | SMALLINT UNSIGNED FK→body_part | 주 부위 |
| `description` | TEXT | |
| `difficulty` | ENUM('beginner','intermediate','advanced') | |
| `movement_pattern` | ENUM('push','pull','squat','hinge','carry','rotation','isometric','other') | |
| `is_unilateral` | TINYINT(1) | 단측 운동 여부 |
| `is_compound` | TINYINT(1) | 복합 운동 여부 |
| `video_url` | VARCHAR(500) | |
| `thumbnail_url` | VARCHAR(500) | |
| `image_url` | VARCHAR(500) | |
| `met_value` | DECIMAL(4,2) | 칼로리 계산용 MET |
| `default_weight_kg` | DECIMAL(8,2) | 기본 권장 중량 |
| `is_active` | TINYINT(1) | |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `body_part` — 신체 부위 (계층 구조)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | SMALLINT UNSIGNED PK | |
| `parent_id` | SMALLINT UNSIGNED FK→body_part | 상위 부위 |
| `name_ko` | VARCHAR(50) UNI | |
| `name_en` | VARCHAR(50) UNI | |
| `sort_order` | TINYINT UNSIGNED | |
| `is_active` | TINYINT(1) | |

### `muscle` — 근육 마스터

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | SMALLINT UNSIGNED PK | |
| `body_part_id` | SMALLINT UNSIGNED FK→body_part | |
| `name_ko` | VARCHAR(50) UNI | |
| `name_en` | VARCHAR(100) UNI | |
| `description` | TEXT | |
| `is_active` | TINYINT(1) | |

### `exercise_muscle` — 운동-근육 관계

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `exercise_id` | INT UNSIGNED PK FK→exercise | |
| `muscle_id` | SMALLINT UNSIGNED PK FK→muscle | |
| `role` | ENUM('primary','secondary','stabilizer') | 역할 |
| `activation_pct` | TINYINT UNSIGNED | 활성화 비율 (%) |

### `equipment` — 장비 마스터

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | SMALLINT UNSIGNED PK | |
| `name_ko` | VARCHAR(50) UNI | |
| `name_en` | VARCHAR(50) UNI | |

### `exercise_equipment` — 운동-장비 관계

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `exercise_id` | INT UNSIGNED PK FK→exercise | |
| `equipment_id` | SMALLINT UNSIGNED PK FK→equipment | |
| `is_required` | TINYINT(1) | 필수 장비 여부 |

### `functional_category` — 기능적 운동 카테고리 (Push/Pull/하체 등)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | SMALLINT UNSIGNED PK | |
| `name_ko` | VARCHAR(50) UNI | |
| `name_en` | VARCHAR(50) UNI | |

### `exercise_functional_category` — 운동-기능카테고리 관계

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `exercise_id` | INT UNSIGNED PK FK→exercise | |
| `category_id` | SMALLINT UNSIGNED PK FK→functional_category | |

### `tag` — 자유 태그 (초보자 추천, 홈트 등)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | SMALLINT UNSIGNED PK | |
| `name_ko` | VARCHAR(50) UNI | |
| `name_en` | VARCHAR(50) UNI | |
| `color_hex` | CHAR(7) | 태그 색상 |
| `is_active` | TINYINT(1) | |

### `exercise_tag` — 운동-태그 관계

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `exercise_id` | INT UNSIGNED PK FK→exercise | |
| `tag_id` | SMALLINT UNSIGNED PK FK→tag | |

### `exercise_variation` — 운동 변형·파생 관계

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | INT UNSIGNED PK | |
| `parent_exercise_id` | INT UNSIGNED FK→exercise | |
| `child_exercise_id` | INT UNSIGNED FK→exercise | |
| `variation_note` | VARCHAR(255) | |

---

## 6. 운동 기록

### `exercise_records` — 운동 기록 헤더

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `record_date` | DATE | 기록 날짜 |
| `record_time` | TIME | 시작 시각 |
| `duration_minutes` | INT UNSIGNED | 운동 시간 (분) |
| `workout_name` | VARCHAR(100) | 워크아웃 이름 |
| `memo` | VARCHAR(500) | 메모 |
| `rating` | TINYINT | 운동 만족도 (1~5) |
| `mood` | TINYINT | 운동 전 컨디션 (1~5) |
| `session_memo` | TEXT | 세션 메모 |
| `completed` | TINYINT(1) | 완료 여부 |
| `feedback_status` | VARCHAR(20) | 피드백 요청 상태 |
| `stamp_type` | VARCHAR(20) | 트레이너 스탬프 종류 |
| `recommended_routine_id` | BIGINT UNSIGNED FK→routines | 추천 루틴 |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `exercise_record_items` — 운동 기록 종목

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `record_id` | BIGINT UNSIGNED FK→exercise_records | |
| `exercise_id` | INT UNSIGNED FK→exercise | |
| `memo` | VARCHAR(500) | 종목 메모 |

### `exercise_record_sets` — 운동 기록 세트

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `record_item_id` | BIGINT UNSIGNED FK→exercise_record_items | |
| `set_number` | INT | 세트 번호 |
| `weight` | DECIMAL(6,2) | 중량 (kg) |
| `reps` | INT | 반복 수 |
| `set_type` | VARCHAR(20) | 세트 종류 (일반/드롭/슈퍼 등) |
| `grip_type` | VARCHAR(20) | 그립 종류 (노멀/언더/해머 등) |
| `rpe` | DECIMAL(3,1) | RPE (1.0~10.0) |
| `rest_seconds` | INT | 휴식 시간 (초) |

### `personal_records` — 개인 기록 (PR)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `exercise_id` | INT UNSIGNED FK→exercise | |
| `one_rep_max` | DECIMAL(7,2) | 1RM (kg) |
| `max_weight` | DECIMAL(6,2) | 최고 중량 (kg) |
| `achieved_at` | DATE | 달성 날짜 |

### `exercise_stamp` — 트레이너 스탬프

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `record_id` | BIGINT UNSIGNED FK→exercise_records | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `stamp_type` | ENUM('GREAT','DONE','COMPLETE') | |
| `comment` | VARCHAR(200) | |
| `created_at` | DATETIME | |

### `exercise_feedback_surveys` — 운동 후 설문

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | |
| `record_id` | BIGINT UNI FK→exercise_records | |
| `condition` | VARCHAR(20) | 컨디션 |
| `pain` | VARCHAR(20) | 통증 여부 |
| `intensity` | VARCHAR(20) | 운동 강도 |
| `created_at` | DATETIME | |

---

## 7. 식단 기록

### `diet_records` — 식단 기록

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `record_date` | DATE | |
| `meal_type` | ENUM('아침','점심','저녁','간식') | |
| `content` | TEXT | 식단 내용 |
| `created_at` | DATETIME | |

---

## 8. 피드백

### `feedback_request` — 피드백 요청

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `member_id` | VARCHAR(50) FK→users | 요청 회원 |
| `trainer_id` | VARCHAR(50) FK→users | 담당 트레이너 |
| `record_id` | BIGINT UNSIGNED FK→exercise_records | 대상 기록 |
| `status` | ENUM('PENDING','COMPLETED') | |
| `requested_at` | DATETIME | |
| `responded_at` | DATETIME | |

### `feedback_request_exercise` — 피드백 요청 종목 (다중)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `request_id` | BIGINT UNSIGNED PK FK→feedback_request | |
| `exercise_id` | INT UNSIGNED PK FK→exercise | |

### `trainer_exercise_feedbacks` — 트레이너 피드백 작성

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | |
| `record_id` | BIGINT UNI FK→exercise_records | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `overall` | TEXT | 종합 피드백 |
| `is_sent` | TINYINT(1) | 발송 여부 |
| `sent_at` | DATETIME | 발송 시각 |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `trainer_exercise_feedback_items` — 피드백 종목별 내용

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | |
| `feedback_id` | BIGINT FK→trainer_exercise_feedbacks | |
| `item_id` | BIGINT FK→exercise_record_items | |
| `content` | TEXT | 종목별 피드백 내용 |

### `record_comment` — 기록 댓글 (회원-트레이너)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `record_type` | ENUM('DIET','EXERCISE') | |
| `record_id` | BIGINT UNSIGNED | 해당 기록 ID |
| `exercise_id` | INT UNSIGNED | 종목별 댓글 (운동 기록 전용) |
| `author_id` | VARCHAR(50) FK→users | 작성자 |
| `parent_id` | BIGINT UNSIGNED FK→record_comment | 답글 대상 |
| `content` | TEXT | 내용 |
| `created_at` | DATETIME | |

> API 응답 필드명: `author_id`→`userId`, `content`→`body`

---

## 9. 루틴

### `routines` — 루틴 (트레이너 작성)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `trainer_id` | VARCHAR(50) FK→users | 작성 트레이너 |
| `title` | VARCHAR(200) | |
| `comment` | TEXT | |
| `tags` | TEXT | |
| `target_type` | ENUM('individual','group','both') | 대상 유형 |
| `filter_member_types` | VARCHAR(30) | 회원권 필터 |
| `filter_genders` | VARCHAR(30) | 성별 필터 |
| `filter_age_groups` | VARCHAR(50) | 연령 필터 |
| `filter_exercise_purposes` | VARCHAR(150) | 운동 목적 필터 |
| `filter_medical_conditions` | VARCHAR(100) | 건강 상태 필터 |
| `created_at` | DATETIME | |
| `updated_at` | DATETIME | |

### `routine_exercises` — 루틴 구성 운동

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `routine_id` | BIGINT UNSIGNED FK→routines | |
| `exercise_id` | INT UNSIGNED FK→exercise | |
| `sort_order` | INT | 순서 |
| `sets` | INT | 세트 수 |
| `reps` | INT | 반복 수 |

### `routine_target_members` — 루틴 개인 배정

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `routine_id` | BIGINT UNSIGNED PK FK→routines | |
| `member_id` | VARCHAR(50) PK FK→users | |

---

## 10. 커뮤니티 게시판

### `posts` — 게시글

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | 작성자 |
| `category` | ENUM('운동기록','식단','질문','정보','자유') | |
| `body` | TEXT | 본문 |
| `likes` | INT | 좋아요 수 (캐시) |
| `created_at` | DATETIME | |

### `post_tags` — 게시글 태그

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `post_id` | BIGINT UNSIGNED FK→posts | |
| `tag` | VARCHAR(50) | 태그명 |

### `post_likes` — 게시글 좋아요

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `post_id` | BIGINT UNSIGNED PK FK→posts | |
| `user_id` | VARCHAR(50) PK FK→users | |

### `post_bookmarks` — 게시글 북마크

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `post_id` | BIGINT UNSIGNED PK FK→posts | |
| `user_id` | VARCHAR(50) PK FK→users | |
| `created_at` | DATETIME | |

### `post_comments` — 게시글 댓글

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `post_id` | BIGINT UNSIGNED FK→posts | |
| `user_id` | VARCHAR(50) FK→users | |
| `body` | TEXT | |
| `created_at` | DATETIME | |

---

## 11. 쪽지 (채팅)

> **실제 사용 테이블**: `message_conversation` + `chat_message`  
> `message`, `message_recipient` 테이블은 미사용 (공지/SMS 시스템 잔존)

### `message_conversation` — 대화방

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | INT PK | |
| `member_id` | VARCHAR(50) FK→users | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `category` | ENUM('쪽지','기록','공지','이벤트') | 대화방 분류 (기본 '쪽지') |
| `created_at` | DATETIME | |

### `chat_message` — 메시지

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | INT PK | |
| `conversation_id` | INT FK→message_conversation | |
| `sender_id` | VARCHAR(50) FK→users | |
| `content` | TEXT | |
| `record_id` | BIGINT | 연결 운동 기록 ID (옵션) |
| `is_read` | TINYINT(1) | |
| `created_at` | DATETIME | |

---

## 12. 알림

### `notifications` — 앱 알림

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT PK | |
| `user_id` | VARCHAR(50) FK→users | 수신자 |
| `type` | VARCHAR(50) | 알림 타입 (아래 목록 참조) |
| `title` | VARCHAR(100) | 알림 제목 |
| `message` | TEXT | 알림 내용 |
| `record_id` | BIGINT | 연결 데이터 ID (옵션) |
| `member_login_id` | VARCHAR(50) | 관련 회원 ID (옵션) |
| `member_name` | VARCHAR(100) | 관련 회원 이름 (옵션) |
| `is_read` | TINYINT(1) | 읽음 여부 |
| `created_at` | DATETIME | |

**알림 타입 목록**

| type | 설명 |
|------|------|
| `feedback_request` | 회원이 피드백 요청 |
| `trainer_feedback` | 트레이너 피드백 도착 |
| `onepoint_gift` | 원포인트 카드 선물 수령 |
| `onepoint_use_request` | 회원이 원포인트 카드 사용 |
| `onepoint_request` | 회원이 원포인트 레슨 신청 |
| `onepoint_status_change` | 원포인트 레슨 상태 변경 |
| `ticket_purchased` | 티켓 구매 완료 |

---

## 13. 헬스장 · 출석 · 상담

### `gym_setting` — 헬스장 설정 (단일 행)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | TINYINT UNSIGNED PK (기본 1) | |
| `gym_name` | VARCHAR(100) | |
| `gym_phone` | VARCHAR(20) | |
| `gym_address` | VARCHAR(255) | |
| `is_open` | TINYINT(1) | 영업 여부 |
| `mon_open` ~ `sun_close` | VARCHAR(5) | 요일별 영업 시간 |
| `mon_closed` ~ `sun_closed` | TINYINT(1) | 요일별 휴무 |
| `notice` | TEXT | 공지사항 |
| `updated_at` | DATETIME | |

### `attendance` — 출석

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `type` | VARCHAR(20) | 출석 유형 (기본 GENERAL) |
| `attend_date` | DATE | |
| `check_in_time` | TIME | |
| `created_at` | DATETIME | |

### `schedules` — 개인 일정 (회원/트레이너 공용)

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `schedule_id` | BIGINT PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `trainer_id` | VARCHAR(50) FK→users | |
| `date` | DATE | |
| `start_time` | VARCHAR(10) | |
| `end_time` | VARCHAR(10) | |
| `title` | VARCHAR(100) | |
| `memo` | TEXT | |
| `color` | VARCHAR(7) | HEX 색상 (기본 #58A6FF) |
| `created_at` | DATETIME | |

### `consult` — 상담 내역

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `type` | VARCHAR(20) | 상담 유형 (기본 NEW) |
| `name` | VARCHAR(50) | |
| `phone` | VARCHAR(20) | |
| `gender` | VARCHAR(10) | |
| `interest` | VARCHAR(100) | 관심사 |
| `content` | TEXT | 상담 내용 |
| `result` | VARCHAR(20) | 상담 결과 |
| `consult_date` | DATE | |
| `staff_id` | BIGINT UNSIGNED | 담당 직원 |
| `staff_name` | VARCHAR(50) | |
| `created_at` | DATETIME | |

### `terms` — 이용약관

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | INT UNSIGNED PK | |
| `version` | VARCHAR(20) | 버전 |
| `content` | TEXT | 약관 내용 |
| `updated_at` | DATE | |
| `created_at` | DATETIME | |

---

## 14. 기타 · 관리자

### `product` — 상품 마스터

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `name` | VARCHAR(100) | |
| `type` | VARCHAR(30) | 상품 유형 |
| `price` | INT | |
| `description` | TEXT | |
| `is_active` | TINYINT(1) | |
| `created_at` | DATETIME | |

### `sale` — 결제 내역

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `product_id` | BIGINT UNSIGNED FK→product | |
| `product_name` | VARCHAR(100) | |
| `product_type` | VARCHAR(30) | |
| `amount` | INT | |
| `payment_method` | VARCHAR(30) | |
| `sale_date` | DATE | |
| `memo` | TEXT | |
| `created_at` | DATETIME | |

### `admin_user` — 관리자 계정

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `username` | VARCHAR(50) UNI | |
| `password` | VARCHAR(255) | bcrypt |
| `name` | VARCHAR(50) | |
| `role` | VARCHAR(20) | |
| `is_active` | TINYINT(1) | |
| `created_at` | DATETIME | |

### `app_feedback` — 앱 피드백

| 컬럼 | 타입 | 설명 |
|------|------|------|
| `id` | BIGINT UNSIGNED PK | |
| `user_id` | VARCHAR(50) FK→users | |
| `content` | TEXT | |
| `created_at` | DATETIME | |

---

## 주요 관계 요약

```
users ──────────────────┬── user_auth (1:N by provider)
        │               ├── user_profiles (1:1)
        │               ├── user_daily_habits (1:1)
        │               ├── user_exercise_info (1:1)
        │               ├── user_exercise_purposes (1:N)
        │               ├── user_medical_history (1:N)
        │               └── user_token_balance (1:1)
        │
users ──┬── trainer_members ──── (trainer_id + member_id)
        │
users ──┬── member_tickets (1:N, type별 1행)
        ├── ticket_logs (1:N)
        ├── ticket_purchases (1:N)
        │
users ──┬── user_profiles.trainer_id ──→ users (담당 트레이너)
        │
users ──┬── trainer_onepoint_quota (1:1, 트레이너 전용)
        ├── onepoint_cards (trainer_id + member_id)
        ├── onepoint_requests (member_id + trainer_id)
        │
exercise_records ─┬── exercise_record_items ─── exercise_record_sets
                  ├── feedback_request
                  ├── trainer_exercise_feedbacks ─── trainer_exercise_feedback_items
                  ├── record_comment
                  └── exercise_stamp

message_conversation ─── chat_message (쪽지 시스템)

routines ─┬── routine_exercises
          └── routine_target_members
```

---

## 미사용 / 레거시 테이블

아래 테이블은 DB에 존재하나 현재 앱 코드에서 사용되지 않습니다.

| 테이블 | 비고 |
|--------|------|
| `message` | 공지/SMS용 잔존, chat_message로 대체 |
| `message_recipient` | 동일 |
| `class_session` | PT 수업 세션 관리 (미구현) |
| `class_attendee` | 동일 |
| `chat_message` (legacy join) | MessageMapper는 chat_message 사용 |
| `exercise_record_sets_backup` | 마이그레이션 백업용 |
