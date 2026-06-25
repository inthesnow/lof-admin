# linkfit DB 전체 스키마 (users 제외)

> users 관련 테이블은 `userssql.md` 참조  
> 총 테이블 수: 107개 중 users 16개 제외한 91개

---

## 목차

1. [헬스장 (Gym)](#1-헬스장-gym)
2. [멤버십 / 상품 / 결제](#2-멤버십--상품--결제)
3. [운동 마스터 데이터](#3-운동-마스터-데이터)
4. [운동 기록](#4-운동-기록)
5. [루틴 / 스케줄 / 출석](#5-루틴--스케줄--출석)
6. [신체 / 식단 기록](#6-신체--식단-기록)
7. [커뮤니티 / 게시판](#7-커뮤니티--게시판)
8. [메시지 / 알림](#8-메시지--알림)
9. [트레이너](#9-트레이너)
10. [원포인트 / 피드백](#10-원포인트--피드백)
11. [클래스 / 상담 / 기타](#11-클래스--상담--기타)
12. [CRM 시스템](#12-crm-시스템)

---

## 1. 헬스장 (Gym)

### gym

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `branch_code` | char(4) | NO | - | 지점 코드 (영문2+숫자2, 예: LF01) UNIQUE |
| `name` | varchar(100) | NO | - | 헬스장 이름 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### gym_setting

헬스장 운영 설정 (지점별 1행, `branch_code`로 `gym` 테이블과 논리 연결)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | tinyint(3) unsigned | NO | - | PK (gym.id와 동일 값 사용) |
| `branch_code` | char(4) | YES | NULL | UNIQUE, gym.branch_code 논리 참조 |
| `gym_name` | varchar(100) | NO | `LINK_Fit` | 헬스장 이름 |
| `gym_phone` | varchar(20) | YES | NULL | 전화번호 |
| `gym_address` | varchar(255) | YES | NULL | 주소 |
| `is_open` | tinyint(1) | NO | 1 | 영업 여부 |
| `mon_open` ~ `sun_open` | varchar(5) | YES | `06:00` / `08:00` | 요일별 오픈 시간 |
| `mon_close` ~ `sun_close` | varchar(5) | YES | `22:00` / `20:00` | 요일별 마감 시간 |
| `mon_closed` ~ `sun_closed` | tinyint(1) | NO | 0 (일요일만 1) | 요일별 휴무 여부 |
| `notice` | text | YES | NULL | 공지사항 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

## 2. 멤버십 / 상품 / 결제

### members

> ⚠️ 레거시 테이블 — 현재는 `users` 테이블을 메인으로 사용

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | UNIQUE |
| `email` | varchar(100) | NO | - | UNIQUE |
| `name` | varchar(50) | NO | - | 이름 |
| `password` | varchar(255) | NO | - | 비밀번호 |
| `phone` | varchar(20) | YES | NULL | 전화번호 |
| `birth_date` | varchar(10) | YES | NULL | 생년월일 |
| `created_at` | datetime(6) | NO | - | 생성일 |

---

### membership (멤버십 등록 내역)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `product_id` | bigint(20) unsigned | YES | NULL | FK → product.id (SET NULL) |
| `type` | varchar(30) | NO | - | 멤버십 종류 |
| `start_date` | date | NO | - | 시작일 |
| `end_date` | date | NO | - | 종료일 |
| `price` | int(11) | NO | 0 | 금액 |
| `memo` | text | YES | NULL | 메모 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### member_tickets (유저 보유 티켓)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK (논리) → users.user_id |
| `ticket_type` | enum | NO | - | `ONE_POINT` / `FEEDBACK` / `PHOTO` / `VIDEO` |
| `remaining` | int(11) | NO | 0 | 잔여 티켓 수 |
| `updated_at` | datetime | YES | current_timestamp | 수정일 (자동 갱신) |

**UNIQUE**: `(user_id, ticket_type)`

---

### member_freeze (멤버십 일시정지)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `freeze_start` | date | NO | - | 정지 시작일 |
| `freeze_end` | date | NO | - | 정지 종료일 |
| `reason` | varchar(255) | YES | NULL | 사유 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### product (상품)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `name` | varchar(100) | NO | - | 상품명 |
| `type` | varchar(30) | NO | - | 상품 유형 |
| `price` | int(11) | NO | 0 | 가격 |
| `description` | text | YES | NULL | 설명 |
| `is_active` | tinyint(1) | NO | 1 | 판매 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### sale (판매 내역)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | YES | NULL | FK → users.user_id (SET NULL) |
| `product_id` | bigint(20) unsigned | YES | NULL | FK → product.id (SET NULL) |
| `product_name` | varchar(100) | YES | NULL | 상품명 스냅샷 |
| `product_type` | varchar(30) | YES | NULL | 상품 유형 스냅샷 |
| `amount` | int(11) | NO | 0 | 결제 금액 |
| `payment_method` | varchar(30) | NO | `CARD` | 결제 수단 |
| `sale_date` | date | NO | - | 판매일 |
| `memo` | text | YES | NULL | 메모 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### subscription_plans (구독 플랜 정의)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `plan_code` | varchar(20) | NO | - | 플랜 코드 (UNIQUE) |
| `name` | varchar(50) | NO | - | 플랜명 |
| `price` | int(11) | NO | - | 가격 |
| `session_count` | int(11) | YES | NULL | 세션 수 (NULL=무제한) |
| `description` | varchar(200) | YES | NULL | 설명 |
| `feedback_monthly_count` | int(11) | NO | 0 | 월 피드백 티켓 수 (-1=무제한) |
| `onepoint_monthly_count` | int(11) | NO | 0 | 월 원포인트 티켓 수 |
| `photo_monthly_count` | int(11) | NO | 0 | 월 사진 티켓 수 |
| `video_monthly_count` | int(11) | NO | 0 | 월 영상 티켓 수 |
| `created_at` | datetime | YES | current_timestamp | 생성일 |

---

### ticket_purchases (앱 내 티켓 구매)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | 구매자 |
| `product_id` | varchar(30) | NO | - | 스토어 상품 ID |
| `quantity` | int(11) | NO | 1 | 수량 |
| `price_krw` | int(11) | NO | - | 원화 금액 |
| `receipt_id` | varchar(50) | NO | - | 영수증 ID (UNIQUE) |
| `payment_method` | varchar(20) | NO | `CARD` | 결제 수단 |
| `payment_token` | varchar(255) | YES | NULL | 결제 토큰 |
| `description` | varchar(100) | NO | `` | 설명 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### ticket_logs (티켓 사용/충전 로그)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | 대상 유저 |
| `ticket_type` | enum | NO | - | `ONE_POINT` / `FEEDBACK` / `PHOTO` / `VIDEO` |
| `action_type` | enum | NO | - | `USE` / `CHARGE` / `EXPIRE` / `GIFT` |
| `description` | varchar(100) | NO | - | 내역 설명 |
| `created_at` | datetime | NO | current_timestamp | 발생일시 |

---

### iap_purchases (인앱 결제 - iOS/Android)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `product_id` | varchar(100) | NO | - | 스토어 상품 ID |
| `platform` | enum | NO | - | `ios` / `android` |
| `purchase_token` | varchar(1000) | NO | - | Android: purchase_token / iOS: transaction_id (UNIQUE 500자) |
| `transaction_id` | varchar(200) | YES | NULL | 스토어 트랜잭션 ID |
| `status` | enum | NO | `granted` | `granted` / `failed` / `refunded` |
| `expires_at` | timestamp | YES | NULL | 구독 만료일 (구독 상품만) |
| `created_at` | timestamp | NO | current_timestamp | 생성일 |

---

## 3. 운동 마스터 데이터

### exercises (운동 마스터 - 기존)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(10) unsigned | NO | AUTO_INCREMENT | PK |
| `name_en` | varchar(150) | NO | - | 영문명 |
| `name_ko` | varchar(150) | NO | - | 한국어명 |
| `body_part` | enum | NO | - | `가슴` / `등` / `어깨` / `팔` / `하체` / `복근` |
| `exercise_type` | enum | NO | - | `맨몸` / `소도구` / `기구` / `유산소` / `스트레칭` |
| `gender` | enum | NO | - | `남` / `녀` / `공통` |
| `file_male` | varchar(500) | YES | NULL | 남성 영상 파일 경로 |
| `file_female` | varchar(500) | YES | NULL | 여성 영상 파일 경로 |
| `thumbnail_url` | varchar(500) | YES | NULL | 썸네일 URL |
| `created_at` | timestamp | NO | current_timestamp | 생성일 |

---

### exercise (운동 마스터 - 신규 상세 버전)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(10) unsigned | NO | AUTO_INCREMENT | PK |
| `name_ko` | varchar(100) | NO | - | 한국어명 (UNIQUE) |
| `name_en` | varchar(100) | NO | - | 영문명 (UNIQUE) |
| `primary_body_part_id` | smallint(5) unsigned | NO | - | FK → body_part.id, 대표 신체 부위 |
| `description` | text | YES | NULL | 운동 설명 |
| `instructions` | text | YES | NULL | 수행 방법 (단계별) |
| `tips` | text | YES | NULL | 코치 팁 / 주의사항 |
| `caution` | text | YES | NULL | 주의 사항 |
| `record_tip` | text | YES | NULL | 기록 팁 |
| `difficulty` | enum | NO | `intermediate` | `beginner` / `intermediate` / `advanced` |
| `movement_pattern` | enum | NO | `other` | `push` / `pull` / `squat` / `hinge` / `carry` / `rotation` / `isometric` / `other` |
| `is_unilateral` | tinyint(1) | NO | 0 | 단측 운동 여부 |
| `is_compound` | tinyint(1) | NO | 1 | 복합관절 여부 |
| `video_url` | varchar(500) | YES | NULL | 시연 영상 URL |
| `thumbnail_url` | varchar(500) | YES | NULL | 썸네일 URL |
| `image_url` | varchar(500) | YES | NULL | 이미지 URL |
| `met_value` | decimal(4,2) | YES | NULL | MET 값 (칼로리 계산용) |
| `default_weight_kg` | decimal(8,2) | YES | NULL | 기본 추천 무게 (kg) |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### body_part (신체 부위 - 계층 구조)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | smallint(5) unsigned | NO | AUTO_INCREMENT | PK |
| `parent_id` | smallint(5) unsigned | YES | NULL | FK → body_part.id (SET NULL), 상위 부위 |
| `name_ko` | varchar(50) | NO | - | 한국어명 (UNIQUE) |
| `name_en` | varchar(50) | NO | - | 영문명 (UNIQUE) |
| `sort_order` | tinyint(3) unsigned | NO | 0 | 정렬 순서 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### muscle (근육 마스터)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | smallint(5) unsigned | NO | AUTO_INCREMENT | PK |
| `body_part_id` | smallint(5) unsigned | NO | - | FK → body_part.id |
| `name_ko` | varchar(50) | NO | - | 한국어명 (UNIQUE) |
| `name_en` | varchar(100) | NO | - | 영문명 (UNIQUE) |
| `description` | text | YES | NULL | 설명 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### functional_category (기능별 운동 카테고리)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | smallint(5) unsigned | NO | AUTO_INCREMENT | PK |
| `name_ko` | varchar(50) | NO | - | 한국어명 (UNIQUE) |
| `name_en` | varchar(50) | NO | - | 영문명 (UNIQUE) |
| `description` | varchar(255) | YES | NULL | 설명 |
| `sort_order` | tinyint(3) unsigned | NO | 0 | 정렬 순서 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### equipment (운동 장비 마스터)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | smallint(5) unsigned | NO | AUTO_INCREMENT | PK |
| `name_ko` | varchar(50) | NO | - | 한국어명 (UNIQUE) |
| `name_en` | varchar(50) | NO | - | 영문명 (UNIQUE) |
| `description` | varchar(255) | YES | NULL | 설명 |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### tag (자유 태그)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | smallint(5) unsigned | NO | AUTO_INCREMENT | PK |
| `name_ko` | varchar(50) | NO | - | 한국어명 (UNIQUE) |
| `name_en` | varchar(50) | NO | - | 영문명 (UNIQUE) |
| `color_hex` | char(7) | YES | NULL | UI 표시용 색상 (#RRGGBB) |
| `is_active` | tinyint(1) | NO | 1 | 활성 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### exercise_muscle (운동-근육 관계)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `exercise_id` | int(10) unsigned | NO | - | PK+FK → exercises.id (CASCADE) |
| `muscle_id` | smallint(5) unsigned | NO | - | PK+FK → muscle.id |
| `role` | enum | NO | `primary` | `primary` / `secondary` / `stabilizer` |
| `activation_pct` | tinyint(3) unsigned | YES | NULL | 활성화 비율 % (0~100) |

---

### exercise_equipment (운동-장비 관계)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `exercise_id` | int(10) unsigned | NO | - | PK+FK → exercises.id (CASCADE) |
| `equipment_id` | smallint(5) unsigned | NO | - | PK+FK → equipment.id |
| `is_required` | tinyint(1) | NO | 1 | 필수 장비 여부 |

---

### exercise_functional_category (운동-기능 카테고리 관계)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `exercise_id` | int(10) unsigned | NO | - | PK+FK → exercises.id (CASCADE) |
| `category_id` | smallint(5) unsigned | NO | - | PK+FK → functional_category.id |

---

### exercise_tag (운동-태그 관계)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `exercise_id` | int(10) unsigned | NO | - | PK+FK → exercises.id (CASCADE) |
| `tag_id` | smallint(5) unsigned | NO | - | PK+FK → tag.id (CASCADE) |

---

### exercise_variation (운동 변형/파생 관계)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(10) unsigned | NO | AUTO_INCREMENT | PK |
| `parent_exercise_id` | int(10) unsigned | NO | - | FK → exercises.id (CASCADE), 원본 운동 |
| `child_exercise_id` | int(10) unsigned | NO | - | FK → exercises.id (CASCADE), 변형 운동 |
| `variation_note` | varchar(255) | YES | NULL | 변형 포인트 설명 |

**UNIQUE**: `(parent_exercise_id, child_exercise_id)`

---

### exercise_video (운동 영상 - 성별별)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(11) | NO | AUTO_INCREMENT | PK |
| `exercise_id` | int(10) unsigned | NO | - | FK → exercises.id (CASCADE) |
| `gender` | enum | NO | - | `male` / `female` |
| `s3_key` | varchar(500) | NO | - | S3 키 |
| `cdn_url` | varchar(500) | NO | - | CDN URL |
| `created_at` | datetime | YES | current_timestamp | 생성일 |
| `updated_at` | datetime | YES | current_timestamp | 수정일 (자동 갱신) |

**UNIQUE**: `(exercise_id, gender)`

---

## 4. 운동 기록

### exercise_records (운동 세션 기록)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `record_date` | date | NO | - | 운동 날짜 |
| `record_time` | time | YES | NULL | 운동 시작 시각 |
| `duration_seconds` | int(10) unsigned | YES | NULL | 운동 시간 (초) |
| `duration_minutes` | int(10) unsigned | YES | NULL | 운동 시간 (분) |
| `workout_name` | varchar(100) | YES | NULL | 운동명 |
| `memo` | varchar(500) | YES | NULL | 메모 |
| `session_memo` | text | YES | NULL | 세션 메모 |
| `rating` | tinyint(4) | YES | NULL | 평점 (1~5) |
| `mood` | tinyint(4) | YES | NULL | 기분 (1~5) |
| `routine_rating` | tinyint(4) | YES | NULL | 루틴 평점 |
| `completed` | tinyint(1) | NO | 0 | 완료 여부 |
| `recommended_routine_id` | bigint(20) unsigned | YES | NULL | FK → routines.id (SET NULL) |
| `feedback_status` | varchar(20) | YES | NULL | `pending` / `completed` |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### exercise_record_items (세션 내 운동 종목)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `record_id` | bigint(20) unsigned | NO | - | FK → exercise_records.id (CASCADE) |
| `exercise_id` | int(10) unsigned | NO | - | FK → exercises.id (CASCADE) |
| `memo` | varchar(500) | YES | NULL | 메모 |

---

### exercise_record_sets (운동 세트 기록)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `record_item_id` | bigint(20) unsigned | NO | - | FK → exercise_record_items.id (CASCADE) |
| `set_number` | int(11) | NO | 1 | 세트 번호 |
| `weight` | decimal(6,2) | YES | NULL | 무게 (kg) |
| `reps` | int(11) | NO | 1 | 반복 횟수 |
| `set_type` | varchar(20) | NO | `일반` | 세트 유형 |
| `grip_type` | varchar(20) | NO | `노멀` | 그립 유형 |
| `rpe` | decimal(3,1) | YES | NULL | RPE (6.0~10.0) |
| `rest_seconds` | int(11) | YES | NULL | 휴식 시간 (초) |

---

### exercise_record_sets_backup (세트 기록 백업 - 구버전)

> 이전 스키마 구조의 백업 테이블

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `record_id` | bigint(20) unsigned | NO | - | FK → exercise_records.id (CASCADE) |
| `exercise_id` | int(10) unsigned | NO | - | FK → exercise.id |
| `sets` | int(11) | NO | 1 | 세트 수 |
| `reps` | int(11) | NO | 1 | 반복 횟수 |
| `weight` | decimal(6,2) | YES | NULL | 무게 (kg) |
| `memo` | varchar(500) | YES | NULL | 메모 |
| `completed` | tinyint(1) | NO | 0 | 완료 여부 |

---

### personal_records (개인 최고 기록)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id |
| `exercise_id` | int(10) unsigned | NO | - | FK → exercise.id |
| `one_rep_max` | decimal(7,2) | YES | NULL | 1RM |
| `max_weight` | decimal(6,2) | YES | NULL | 최고 무게 |
| `achieved_at` | date | YES | NULL | 달성일 |

**UNIQUE**: `(user_id, exercise_id)`

---

### exercise_stamp (트레이너 운동 기록 스탬프)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `record_id` | bigint(20) unsigned | NO | - | FK → exercise_records.id |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `stamp_type` | enum | NO | - | `GREAT` / `DONE` / `COMPLETE` |
| `comment` | varchar(200) | YES | NULL | 스탬프 코멘트 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

**UNIQUE**: `(record_id, trainer_id)`

---

## 5. 루틴 / 스케줄 / 출석

### routines (루틴)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE), 작성 트레이너 |
| `title` | varchar(200) | NO | - | 루틴 제목 |
| `comment` | text | YES | NULL | 설명 |
| `target_type` | enum | YES | NULL | `individual` / `group` / `both` |
| `filter_member_types` | varchar(30) | YES | NULL | 대상 멤버 유형 필터 |
| `filter_genders` | varchar(30) | YES | NULL | 성별 필터 |
| `filter_age_groups` | varchar(50) | YES | NULL | 연령대 필터 |
| `filter_exercise_purposes` | varchar(150) | YES | NULL | 운동 목적 필터 |
| `filter_medical_conditions` | varchar(100) | YES | NULL | 질환 필터 |
| `tags` | text (JSON) | YES | NULL | 태그 JSON 배열 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### routine_exercises (루틴 내 운동 구성)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `routine_id` | bigint(20) unsigned | NO | - | FK → routines.id (CASCADE) |
| `exercise_id` | int(10) unsigned | NO | - | FK → exercises.id (CASCADE) |
| `sort_order` | int(11) | NO | 0 | 순서 |
| `sets` | int(11) | NO | - | 세트 수 |
| `reps` | int(11) | NO | - | 반복 횟수 |

---

### routine_target_members (루틴 대상 회원)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `routine_id` | bigint(20) unsigned | NO | - | PK+FK → routines.id (CASCADE) |
| `member_id` | varchar(50) | NO | - | PK+FK → users.user_id (CASCADE) |

---

### schedules (일정)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `schedule_id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id |
| `trainer_id` | varchar(50) | YES | NULL | FK → users.user_id (CASCADE), 담당 트레이너 |
| `type` | varchar(20) | YES | NULL | 일정 유형 |
| `date` | date | NO | - | 날짜 |
| `start_time` | varchar(10) | YES | NULL | 시작 시간 |
| `end_time` | varchar(10) | YES | NULL | 종료 시간 |
| `title` | varchar(100) | YES | NULL | 제목 |
| `member_name` | varchar(100) | YES | NULL | 회원 이름 |
| `memo` | text | YES | NULL | 메모 |
| `color` | varchar(7) | YES | `#58A6FF` | 캘린더 표시 색상 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### trainer_schedules (트레이너 PT/원포인트 스케줄)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `member_id` | varchar(50) | YES | NULL | FK → users.user_id (SET NULL) |
| `type` | enum | NO | - | `PT` / `ONE_POINT` |
| `schedule_date` | date | NO | - | 날짜 |
| `start_time` | time | NO | - | 시작 시간 |
| `end_time` | time | NO | - | 종료 시간 |
| `memo` | varchar(500) | YES | NULL | 메모 |
| `created_at` | datetime | YES | current_timestamp | 생성일 |
| `updated_at` | datetime | YES | current_timestamp | 수정일 (자동 갱신) |

---

### trainer_schedule_settings (트레이너 스케줄 설정)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `trainer_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `start_hour` | tinyint(4) | NO | 0 | 업무 시작 시 |
| `end_hour` | tinyint(4) | NO | 24 | 업무 종료 시 |
| `slot_minutes` | tinyint(4) | NO | 60 | 슬롯 단위 (분) |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### class_session (그룹 클래스 세션)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | YES | NULL | FK → users.user_id (CASCADE) |
| `title` | varchar(100) | NO | - | 클래스명 |
| `type` | varchar(30) | NO | - | 클래스 유형 |
| `category` | varchar(30) | YES | NULL | 카테고리 |
| `trainer_name` | varchar(50) | YES | NULL | 트레이너 이름 스냅샷 |
| `class_date` | date | NO | - | 수업 날짜 |
| `start_time` | time | NO | - | 시작 시간 |
| `end_time` | time | NO | - | 종료 시간 |
| `capacity` | int(11) | NO | 20 | 최대 정원 |
| `enrolled` | int(11) | NO | 0 | 현재 수강 인원 |
| `status` | varchar(20) | NO | `OPEN` | 세션 상태 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### class_attendee (클래스 수강생)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `class_session_id` | bigint(20) unsigned | NO | - | FK → class_session.id (CASCADE) |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `registered_at` | datetime | NO | current_timestamp | 등록일 |
| `attended` | tinyint(1) | NO | 0 | 실제 출석 여부 |

**UNIQUE**: `(class_session_id, user_id)`

---

### attendance (일반 출석 체크)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `type` | varchar(20) | NO | `GENERAL` | 출석 유형 |
| `attend_date` | date | NO | - | 출석 날짜 |
| `check_in_time` | time | NO | - | 체크인 시각 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

## 6. 신체 / 식단 기록

### body_records (신체 일일 기록)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `date` | date | NO | - | 기록 날짜 |
| `morning_weight` | decimal(5,2) | YES | NULL | 아침 체중 (kg) |
| `morning_weight_time` | varchar(5) | YES | NULL | 아침 체중 측정 시간 |
| `evening_weight` | decimal(5,2) | YES | NULL | 저녁 체중 (kg) |
| `evening_weight_time` | varchar(5) | YES | NULL | 저녁 체중 측정 시간 |
| `water_intake` | decimal(4,2) | YES | NULL | 수분 섭취량 (L) |
| `sleep_start_time` | varchar(5) | YES | NULL | 취침 시각 |
| `sleep_hours` | int(11) | YES | NULL | 수면 시간 (시) |
| `sleep_minutes` | int(11) | YES | NULL | 수면 시간 (분) |
| `did_exercise` | tinyint(1) | YES | NULL | 운동 여부 |
| `exercise_types` | text | YES | NULL | 운동 종류 |
| `exercise_content` | text | YES | NULL | 운동 내용 |
| `exercise_minutes` | int(11) | YES | NULL | 운동 시간 (분) |
| `hunger_level` | int(11) | YES | NULL | 공복감 수준 |
| `condition` | varchar(20) | YES | NULL | 컨디션 |
| `had_bowel_movement` | tinyint(1) | YES | NULL | 배변 여부 |
| `note` | text | YES | NULL | 메모 |
| `created_at` | timestamp | YES | current_timestamp | 생성일 |
| `updated_at` | timestamp | YES | current_timestamp | 수정일 (자동 갱신) |

**UNIQUE**: `(user_id, date)`

---

### body_record_meals (식사 기록 - body_records 상세)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `body_record_id` | bigint(20) unsigned | NO | - | FK → body_records.id (CASCADE) |
| `meal_type` | varchar(10) | NO | - | 식사 종류 (예: 아침, 점심, 저녁, 간식) |
| `content` | text | YES | NULL | 식사 내용 |
| `time` | varchar(5) | YES | NULL | 식사 시간 |

---

### diet_records (식단 기록 - 단순 버전)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `record_date` | date | NO | - | 기록 날짜 |
| `meal_type` | enum | NO | - | `아침` / `점심` / `저녁` / `간식` |
| `content` | text | NO | - | 식사 내용 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

## 7. 커뮤니티 / 게시판

### posts (게시글)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `category` | enum | NO | - | `운동기록` / `식단` / `질문` / `정보` / `자유` |
| `body` | text | NO | - | 본문 |
| `likes` | int(11) | NO | 0 | 좋아요 수 (비정규화 카운터) |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### post_images (게시글 첨부 이미지)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `post_id` | bigint(20) unsigned | NO | - | FK → posts.id (CASCADE) |
| `image_url` | varchar(500) | NO | - | 이미지 URL |
| `s3_key` | varchar(500) | NO | - | S3 키 |
| `sort_order` | int(11) | NO | 0 | 정렬 순서 |
| `created_at` | timestamp | NO | current_timestamp | 생성일 |

---

### post_likes (게시글 좋아요)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `post_id` | bigint(20) unsigned | NO | - | PK+FK → posts.id (CASCADE) |
| `user_id` | varchar(50) | NO | - | PK |

---

### post_comments (게시글 댓글)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `post_id` | bigint(20) unsigned | NO | - | FK → posts.id (CASCADE) |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `body` | text | NO | - | 댓글 내용 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### post_bookmarks (게시글 북마크)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `post_id` | bigint(20) unsigned | NO | - | PK+FK → posts.id (CASCADE) |
| `user_id` | varchar(50) | NO | - | PK |
| `created_at` | datetime | YES | current_timestamp | 생성일 |

---

### post_shares (게시글 공유)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `post_id` | bigint(20) unsigned | NO | - | FK → posts.id |
| `user_id` | varchar(50) | NO | - | 공유한 유저 |
| `created_at` | datetime | YES | current_timestamp | 생성일 |

**UNIQUE**: `(post_id, user_id)`

---

### post_tags (게시글 태그)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `post_id` | bigint(20) unsigned | NO | - | FK → posts.id (CASCADE) |
| `tag` | varchar(50) | NO | - | 태그 텍스트 |

---

### record_comment (운동/식단 기록 댓글)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `author_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE), 작성자 |
| `record_type` | enum | NO | - | `DIET` / `EXERCISE` |
| `record_id` | bigint(20) unsigned | NO | - | 기록 ID (record_type에 따라 대상 테이블 다름) |
| `exercise_id` | int(10) unsigned | YES | NULL | 특정 운동 종목 (NULL이면 기록 전체 댓글) |
| `content` | text | NO | - | 댓글 내용 |
| `parent_id` | bigint(20) unsigned | YES | NULL | FK → record_comment.id (CASCADE), 대댓글 |
| `created_at` | datetime | YES | current_timestamp | 생성일 |

---

## 8. 메시지 / 알림

### message (공지/SMS 발송 메시지)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `title` | varchar(200) | NO | - | 제목 |
| `content` | text | NO | - | 내용 |
| `type` | varchar(20) | NO | `SMS` | 유형 (SMS 등) |
| `status` | varchar(20) | NO | `DRAFT` | 상태 (DRAFT / SENT 등) |
| `sent_at` | datetime | YES | NULL | 발송일시 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### message_recipient (메시지 수신자)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `message_id` | bigint(20) unsigned | NO | - | FK → message.id (CASCADE) |
| `user_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |

---

### message_conversation (트레이너-회원 채팅방)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(11) | NO | AUTO_INCREMENT | PK |
| `member_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `category` | enum | NO | `쪽지` | `쪽지` / `기록` / `공지` / `이벤트` / `원포인트` |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

**UNIQUE**: `(member_id, trainer_id, category)`

---

### chat_message (채팅 메시지)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(11) | NO | AUTO_INCREMENT | PK |
| `conversation_id` | int(11) | NO | - | FK → message_conversation.id (CASCADE) |
| `sender_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `content` | text | NO | - | 메시지 내용 |
| `record_id` | bigint(20) | YES | NULL | 연결된 운동 기록 ID |
| `is_read` | tinyint(1) | NO | 0 | 읽음 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### notifications (푸시/앱 알림)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | 수신자 loginId |
| `type` | varchar(50) | NO | - | 알림 유형 (예: `feedback_request`, `message`) |
| `title` | varchar(100) | YES | NULL | 알림 제목 |
| `message` | text | NO | - | 알림 내용 |
| `record_id` | bigint(20) | YES | NULL | 연결된 기록 ID |
| `member_name` | varchar(100) | YES | NULL | 관련 회원 이름 |
| `member_login_id` | varchar(50) | YES | NULL | 관련 회원 loginId |
| `is_read` | tinyint(1) | NO | 0 | 읽음 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

## 9. 트레이너

### trainer_members (트레이너-회원 배정)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `member_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `member_type` | enum | NO | `PT` | `PT` / `ONE_POINT` |
| `assigned_at` | datetime | NO | current_timestamp | 배정일 |

**UNIQUE**: `(trainer_id, member_id)`

---

### trainer_member_memo (트레이너의 회원 메모)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `member_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `content` | text | YES | NULL | 메모 내용 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

**UNIQUE**: `(trainer_id, member_id)`

---

### trainer_token (트레이너 토큰 잔액)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `trainer_id` | varchar(50) | NO | - | **PK**, FK → users.user_id (CASCADE) |
| `balance` | int(11) | NO | 0 | 토큰 잔액 |

---

### trainer_comment (트레이너 운동 코멘트)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `record_id` | bigint(20) unsigned | NO | - | 운동 기록 ID |
| `exercise_id` | int(10) unsigned | NO | - | 운동 종목 ID |
| `content` | text | NO | - | 코멘트 내용 |
| `on_site_recommended` | tinyint(1) | NO | 0 | 현장 추천 여부 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### trainer_reviews (트레이너 리뷰)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `review_id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | 리뷰 대상 트레이너 |
| `member_id` | varchar(50) | NO | - | 리뷰 작성 회원 |
| `rating` | decimal(2,1) | NO | - | 평점 |
| `content` | text | YES | NULL | 리뷰 내용 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

**UNIQUE**: `(trainer_id, member_id)`

---

### trainer_review_likes (트레이너 리뷰 좋아요)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `like_id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `review_id` | bigint(20) unsigned | NO | - | FK → trainer_reviews.review_id (CASCADE) |
| `user_id` | varchar(50) | NO | - | FK → users.user_id |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

**UNIQUE**: `(review_id, user_id)`

---

## 10. 원포인트 / 피드백

### onepoint_requests (원포인트 레슨 신청)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `member_id` | varchar(50) | NO | - | FK → users.user_id |
| `trainer_id` | varchar(50) | NO | - | FK → users.user_id |
| `conversation_id` | bigint(20) | YES | NULL | 연결된 대화방 ID |
| `preferred_dates` | JSON | YES | NULL | 희망 날짜 목록 |
| `body_parts` | JSON | YES | NULL | 관심 신체 부위 |
| `has_pain` | tinyint(1) | NO | 0 | 통증 여부 |
| `notes` / `note` | text | YES | NULL | 요청 메모 |
| `status` | enum | NO | `PENDING` | `PENDING` / `ACCEPTED` / `CANCELLED` / `COMPLETED` / `APPROVED` / `REJECTED` |
| `scheduled_date` | date | YES | NULL | 예정 날짜 |
| `selected_date` | date | YES | NULL | 확정 날짜 |
| `selected_time` | varchar(5) | YES | NULL | 확정 시간 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### onepoint_request_slots (원포인트 희망 시간 슬롯)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `request_id` | bigint(20) | NO | - | FK → onepoint_requests.id (CASCADE) |
| `slot_order` | int(11) | NO | - | 순서 |
| `preferred_date` | date | NO | - | 희망 날짜 |
| `preferred_time` | varchar(5) | NO | - | 희망 시간 |

---

### onepoint_cards (원포인트 카드)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `trainer_id` | varchar(50) | NO | - | 발급 트레이너 |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `status` | enum | NO | `ISSUABLE` | `ISSUABLE` / `COMPLETED` / `CANCELLED` |
| `issued_at` | datetime | NO | current_timestamp | 발급일 |
| `completed_at` | datetime | YES | NULL | 완료일 |

---

### trainer_onepoint_quota (트레이너 원포인트 발급 쿼터)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `trainer_id` | varchar(50) | NO | - | **PK** |
| `issuable_remaining` | int(11) | NO | 5 | 남은 발급 가능 수 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### feedback_request (운동 피드백 요청)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `member_id` | varchar(50) | NO | - | FK → users.user_id (CASCADE) |
| `trainer_id` | varchar(50) | YES | NULL | 담당 트레이너 (배정 전 NULL) |
| `record_id` | bigint(20) unsigned | NO | - | 피드백 대상 운동 기록 ID |
| `status` | enum | NO | `PENDING` | `PENDING` / `COMPLETED` |
| `requested_at` | datetime | NO | current_timestamp | 요청일 |
| `responded_at` | datetime | YES | NULL | 응답일 |

---

### feedback_request_exercise (피드백 요청 - 운동 종목 지정)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `request_id` | bigint(20) unsigned | NO | - | PK+FK → feedback_request.id (CASCADE) |
| `exercise_id` | int(10) unsigned | NO | - | PK+FK → exercises.id (CASCADE) |

---

### exercise_feedback_surveys (운동 후 자가 설문)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `record_id` | bigint(20) | NO | - | 운동 기록 ID (UNIQUE) |
| `condition` | varchar(20) | NO | - | 컨디션: 나쁨 / 보통 / 좋음 / 매우 좋음 |
| `pain` | varchar(20) | NO | - | 통증: 없음 / 경미함 / 있음 |
| `intensity` | varchar(20) | NO | - | 강도: 너무 쉬움 / 적절함 / 너무 어려움 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### trainer_exercise_feedbacks (트레이너 운동 피드백)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `record_id` | bigint(20) | NO | - | 운동 기록 ID (UNIQUE) |
| `trainer_id` | varchar(50) | NO | - | 작성 트레이너 |
| `overall` | text | YES | NULL | 전체 피드백 내용 |
| `is_sent` | tinyint(1) | NO | 0 | 전송 여부 |
| `sent_at` | datetime | YES | NULL | 전송일시 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### trainer_exercise_feedback_items (피드백 운동 종목별 상세)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) | NO | AUTO_INCREMENT | PK |
| `feedback_id` | bigint(20) | NO | - | FK → trainer_exercise_feedbacks.id (CASCADE) |
| `item_id` | bigint(20) | NO | - | exercise_record_items.id 참조 |
| `content` | text | YES | NULL | 종목별 피드백 내용 |

---

## 11. 클래스 / 상담 / 기타

### consult (상담 내역)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | YES | NULL | FK → users.user_id (SET NULL) |
| `type` | varchar(20) | NO | `NEW` | 상담 유형 (신규/재등록 등) |
| `name` | varchar(50) | NO | - | 상담자 이름 |
| `phone` | varchar(20) | YES | NULL | 연락처 |
| `gender` | varchar(10) | YES | NULL | 성별 |
| `interest` | varchar(100) | YES | NULL | 관심 분야 |
| `content` | text | YES | NULL | 상담 내용 |
| `result` | varchar(20) | YES | NULL | 상담 결과 |
| `consult_date` | date | NO | - | 상담 날짜 |
| `staff_id` | bigint(20) unsigned | YES | NULL | 담당 직원 ID |
| `staff_name` | varchar(50) | YES | NULL | 담당 직원 이름 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### app_feedback (앱 피드백)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | bigint(20) unsigned | NO | AUTO_INCREMENT | PK |
| `user_id` | varchar(50) | NO | - | FK → users.user_id |
| `content` | text | NO | - | 피드백 내용 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### terms (약관)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | int(10) unsigned | NO | AUTO_INCREMENT | PK |
| `version` | varchar(20) | NO | - | 버전 (예: 2026-06-17) |
| `updated_at` | date | NO | - | 업데이트 날짜 |
| `content` | text | NO | - | 약관 전문 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

## 12. CRM 시스템

> CRM 테이블은 모두 `gym_id`를 기준으로 멀티테넌시를 구현함  
> PK는 대부분 `char(36)` UUID 사용

### crm_announcements (공지사항)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `author_id` | char(36) | YES | NULL | crm_users.id 참조 |
| `target` | enum | NO | - | `all_members` / `all_trainers` / `specific` / `all` |
| `target_ids` | JSON | YES | NULL | 특정 대상 ID 목록 |
| `title` | varchar(200) | NO | - | 제목 |
| `content` | text | NO | - | 내용 |
| `send_push` | tinyint(1) | NO | 0 | 푸시 발송 여부 |
| `sent_at` | datetime | YES | NULL | 발송일시 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_cs_tickets (고객 문의 티켓)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `member_id` | varchar(50) | NO | - | 문의 회원 |
| `category` | enum | NO | - | `app_error` / `membership` / `pt` / `feedback` / `payment` / `other` |
| `title` | varchar(200) | NO | - | 제목 |
| `content` | text | NO | - | 내용 |
| `status` | enum | NO | `received` | `received` / `checking` / `processing` / `answered` / `closed` |
| `assigned_to` | char(36) | YES | NULL | 담당 crm_users.id |
| `response` | text | YES | NULL | 답변 |
| `responded_at` | datetime | YES | NULL | 답변일시 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_daily_stats (헬스장 일별 통계)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `gym_id` | bigint(20) unsigned | NO | - | PK |
| `stat_date` | date | NO | - | PK, 통계 날짜 |
| `total_members` | int(11) | NO | 0 | 총 회원 수 |
| `active_members` | int(11) | NO | 0 | 활성 회원 수 |
| `dormant_members` | int(11) | NO | 0 | 휴면 회원 수 |
| `routine_completed` | int(11) | NO | 0 | 루틴 완료 수 |
| `attendance_rate` | decimal(5,2) | NO | 0.00 | 출석률 |
| `feedback_issued` | int(11) | NO | 0 | 피드백 발급 수 |
| `feedback_used` | int(11) | NO | 0 | 피드백 사용 수 |

---

### crm_feedback_requests (CRM 피드백 요청)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `ticket_id` | char(36) | YES | NULL | crm_feedback_tickets.id 참조 |
| `member_id` | varchar(50) | NO | - | 요청 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `trainer_id` | char(36) | YES | NULL | 담당 crm_users.id |
| `content` | text | NO | - | 요청 내용 |
| `attachments` | JSON | YES | NULL | 첨부 파일 목록 |
| `status` | enum | NO | `pending` | `pending` / `in_progress` / `completed` / `held` |
| `response` | text | YES | NULL | 응답 내용 |
| `responded_at` | datetime | YES | NULL | 응답일시 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_feedback_tickets (CRM 피드백 티켓)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `trainer_id` | char(36) | YES | NULL | 담당 crm_users.id |
| `ticket_type` | enum | NO | `free` | `free` / `paid` |
| `status` | enum | NO | `issued` | `issued` / `used` / `expired` / `pending` |
| `month_year` | char(7) | NO | - | 발급 월 (YYYY-MM) |
| `issued_at` | datetime | NO | current_timestamp | 발급일 |
| `used_at` | datetime | YES | NULL | 사용일 |
| `expires_at` | datetime | YES | NULL | 만료일 |

---

### crm_member_assignments (트레이너-회원 배정 - CRM)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | users.user_id |
| `trainer_id` | char(36) | NO | - | crm_users.id |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `assigned_at` | datetime | NO | current_timestamp | 배정일 |

**UNIQUE**: `(member_id, gym_id)`

---

### crm_member_notes (회원 메모 - CRM)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `author_id` | char(36) | YES | NULL | 작성자 crm_users.id |
| `content` | text | NO | - | 메모 내용 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_member_tags (회원 태그 - CRM)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `tag` | varchar(30) | NO | - | 태그명 |
| `color` | char(7) | YES | NULL | HEX 색상 (예: #FF5733) |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_membership_history (멤버십 변경 이력 - CRM)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `action` | enum | NO | - | `pause` / `extend` / `change` / `cancel` |
| `reason` | text | YES | NULL | 사유 |
| `processed_by` | char(36) | YES | NULL | 처리자 crm_users.id |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_messages (CRM 내부 메시지)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `sender_type` | enum | NO | - | `member` / `trainer` / `admin` |
| `sender_id` | varchar(100) | NO | - | 발신자 ID |
| `receiver_type` | enum | NO | - | `member` / `trainer` / `admin` |
| `receiver_id` | varchar(100) | NO | - | 수신자 ID |
| `content` | text | NO | - | 내용 |
| `is_read` | tinyint(1) | NO | 0 | 읽음 여부 |
| `is_notice` | tinyint(1) | NO | 0 | 공지 여부 |
| `parent_id` | char(36) | YES | NULL | 답장 대상 메시지 ID |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_pt_registration_type (PT 등록 유형)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `pt_package_id` | bigint(20) unsigned | YES | NULL | user_subscriptions.id 논리 참조 |
| `reg_type` | enum | NO | - | `new` / `re` / `referral` |
| `referrer_id` | varchar(50) | YES | NULL | 소개자 users.user_id |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_re_registration (재등록 관리)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `member_id` | varchar(50) | NO | - | 대상 회원 |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `reason` | enum | NO | - | `membership_expiry` / `pt_low` / `low_routine` / `low_app_usage` / `feedback_history` |
| `status` | enum | NO | `pending` | `pending` / `in_progress` / `success` / `failed` / `hold` |
| `assigned_to` | char(36) | YES | NULL | 담당 crm_users.id |
| `memo` | text | YES | NULL | 메모 |
| `scheduled_at` | datetime | YES | NULL | 예정 일시 |
| `resolved_at` | datetime | YES | NULL | 해결 일시 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_sales (매출 내역 - CRM)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `member_id` | varchar(50) | NO | - | 결제 회원 |
| `trainer_id` | char(36) | YES | NULL | 담당 crm_users.id |
| `sales_type` | enum | NO | - | `membership` / `pt` / `feedback_ticket` |
| `reg_type` | enum | YES | NULL | `new` / `re` / `referral` |
| `amount` | decimal(12,2) | NO | - | 금액 |
| `sale_date` | date | NO | - | 판매일 |
| `note` | text | YES | NULL | 메모 |
| `created_at` | datetime | NO | current_timestamp | 생성일 |

---

### crm_sales_targets (월 매출 목표)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `gym_id` | bigint(20) unsigned | NO | - | PK |
| `month_year` | char(7) | NO | - | PK, 목표 월 (YYYY-MM) |
| `target` | decimal(12,2) | NO | - | 목표 금액 |

---

### crm_ticket_inventory (CRM 피드백 티켓 재고)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID (UNIQUE) |
| `total_qty` | int(11) | NO | 0 | 전체 재고 수 |
| `used_qty` | int(11) | NO | 0 | 사용된 수량 |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

### crm_ticket_purchases (CRM 피드백 티켓 구매)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID |
| `quantity` | int(11) | NO | - | 구매 수량 |
| `unit_price` | decimal(10,2) | NO | - | 단가 |
| `total_price` | decimal(10,2) | NO | - | 총액 |
| `purchased_by` | char(36) | YES | NULL | 구매자 crm_users.id |
| `purchased_at` | datetime | NO | current_timestamp | 구매일 |

---

### crm_ticket_settings (CRM 티켓 설정)

| 컬럼 | 타입 | NULL | 기본값 | 설명 |
|------|------|------|--------|------|
| `id` | char(36) | NO | - | PK (UUID) |
| `gym_id` | bigint(20) unsigned | NO | - | 헬스장 ID (UNIQUE) |
| `free_tickets_per_member` | int(11) | NO | 2 | 회원당 무료 티켓 수 |
| `max_tickets_per_month` | int(11) | YES | NULL | 월 최대 티켓 수 |
| `is_beta` | tinyint(1) | NO | 1 | 베타 여부 |
| `updated_by` | char(36) | YES | NULL | 수정자 crm_users.id |
| `updated_at` | datetime | NO | current_timestamp | 수정일 (자동 갱신) |

---

## 전체 테이블 관계 요약

```
gym
├── gym_setting              1:1  헬스장 운영 설정
├── admin_user               1:N  관리자 계정
├── crm_users                1:N  CRM 유저 (gym_admin, trainer)
├── user_gym [논리]          N:M  앱 유저 소속 헬스장
└── crm_daily_stats          1:N  일별 통계

exercises / exercise (운동 마스터)
├── exercise_muscle          N:M  운동-근육 (body_part → muscle)
├── exercise_equipment       N:M  운동-장비
├── exercise_functional_category N:M 운동-기능 카테고리
├── exercise_tag             N:M  운동-태그
├── exercise_variation       N:M  변형 운동 관계
└── exercise_video           1:2  성별별 시연 영상

users.user_id (앱 유저 기준 FK)
├── exercise_records         1:N  운동 세션
│   └── exercise_record_items  1:N  운동 종목
│       └── exercise_record_sets 1:N 세트 기록
├── body_records             1:N  신체 일일 기록
│   └── body_record_meals    1:N  식사 기록
├── diet_records             1:N  식단 기록 (단순)
├── personal_records         1:N  개인 최고 기록
├── routines (trainer_id)    1:N  트레이너 작성 루틴
│   ├── routine_exercises    1:N  루틴 구성 운동
│   └── routine_target_members N:M 대상 회원
├── schedules                1:N  일정
├── trainer_schedules        1:N  PT/원포인트 일정
├── attendance               1:N  출석
├── membership               1:N  멤버십 내역
├── sale                     1:N  판매 내역
├── trainer_members          N:M  트레이너-회원 배정
├── message_conversation     N:M  채팅방 (member+trainer)
│   └── chat_message         1:N  채팅 메시지
├── posts                    1:N  게시글
│   ├── post_images / post_likes / post_comments
│   ├── post_bookmarks / post_shares / post_tags
└── feedback_request         1:N  피드백 요청
    └── trainer_exercise_feedbacks 1:1 트레이너 피드백
        └── trainer_exercise_feedback_items 1:N 종목별 피드백
```
