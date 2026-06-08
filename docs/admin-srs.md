# 링크온핏 CRM — Admin SRS (Software Requirements Specification)

> **문서 버전**: v1.0  
> **작성일**: 2026-06-08  
> **대상**: 백엔드·프론트엔드 개발자  
> **전제**: 기존 링크온핏 회원 앱과 동일한 DB를 공유하며, 앱 로직·스키마에 대한 영향을 최소화한다.

---

## 1. 프로젝트 개요

링크온핏 CRM은 헬스장 관리자·트레이너가 사용하는 **별도 웹 관리 시스템**이다.  
회원 앱과 DB를 공유하되, CRM 전용 테이블을 별도로 구성하여 앱 코드베이스를 건드리지 않는다.

### 1.1 목표
- 회원 정보·이용권·PT·루틴 이행 출석을 한 화면에서 관리
- 월 2회 무료 피드백 티켓 제공 및 수익화 구조 설계
- 재등록률·부가수익 향상을 위한 데이터 기반 운영

### 1.2 사용자 역할

| 역할 | 설명 |
|------|------|
| `super_admin` | 플랫폼 전체 관리 (링크온핏 운영팀) |
| `gym_admin` | 헬스장 단위 관리자 |
| `trainer` | 담당 회원 관리·피드백 처리 |

---

## 2. 아키텍처 원칙 — 앱 영향 최소화

### 2.1 DB 공유 전략

```
[기존 앱 DB]                    [CRM 전용 테이블]
  users                    →      crm_members (view or join)
  memberships              →      crm_membership_notes
  pt_sessions              →      crm_pt_notes
  routines / routine_logs  →      crm_feedback_tickets
  ...                      →      crm_cs_tickets
                                  crm_messages
                                  crm_re_registration
                                  crm_sales
                                  crm_announcements
                                  crm_ticket_settings
```

**규칙**
1. 기존 앱 테이블에 **컬럼 추가 금지** — 앱 마이그레이션 없이 독립 운영
2. 기존 테이블은 **READ ONLY** 원칙 — CRM에서 데이터를 직접 수정해야 할 경우, 앱이 이미 제공하는 API 엔드포인트를 통해 처리하거나, 별도 CRM 테이블에 오버라이드 값을 저장
3. CRM 테이블은 모두 `crm_` 접두사를 사용하여 앱 테이블과 명확히 구분
4. CRM 전용 API 서버를 분리(별도 서비스 또는 `/admin` prefix 라우트)하여 앱 라우터와 충돌 방지

### 2.2 인증 분리

- CRM 로그인은 기존 회원 앱 인증과 완전 분리
- `crm_users` 테이블에 `gym_admin`, `trainer` 계정 관리
- JWT 토큰 발급 시 `role`, `gym_id` 클레임 포함
- 앱 회원(users) 테이블에 CRM 계정 혼용 금지

### 2.3 배포 구조 (권장)

```
[회원 앱 서버]          [CRM 웹 서버]
  /api/v1/*      ←→      /admin/api/v1/*
  
  동일 DB 클러스터 사용
  Read Replica 활용 권장 (통계 쿼리 분리)
```

---

## 3. 공통 DB 스키마

### 3.1 CRM 계정

```sql
CREATE TABLE crm_users (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id        UUID NOT NULL REFERENCES gyms(id),
  name          VARCHAR(50) NOT NULL,
  email         VARCHAR(100) UNIQUE NOT NULL,
  password_hash TEXT NOT NULL,
  role          ENUM('super_admin','gym_admin','trainer') NOT NULL,
  is_active     BOOLEAN DEFAULT TRUE,
  created_at    TIMESTAMPTZ DEFAULT NOW()
);
```

### 3.2 CRM 공통 메모

```sql
CREATE TABLE crm_member_notes (
  id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id  UUID NOT NULL,           -- 앱 users.id 참조 (FK 없이 논리적 참조)
  gym_id     UUID NOT NULL,
  author_id  UUID REFERENCES crm_users(id),
  content    TEXT NOT NULL,
  created_at TIMESTAMPTZ DEFAULT NOW()
);
```

> **FK 없이 논리적 참조**: 앱 `users` 테이블에 FK constraint를 걸지 않는다. 앱 스키마 변경 시 마이그레이션 충돌을 방지하기 위함.

---

## 4. 섹터별 기능 요명세

---

### Sector 1 — 메인 대시보드

**목적**: CRM 접속 시 헬스장 운영 현황을 한눈에 파악

**표시 항목**

| 항목 | 데이터 소스 | 설명 |
|------|------------|------|
| 전체 회원 수 | `memberships` (앱) | 해당 gym 소속 활성 회원 |
| 활성 / 휴면 회원 | `memberships.status` | 상태별 분류 |
| 오늘 루틴 이행 회원 | `routine_logs` (앱) | 오늘 날짜 완료 건수 |
| 루틴 이행 출석률 | `routine_logs` / `memberships` | 이행 회원 ÷ 전체 활성 회원 |
| 피드백 대기 건수 | `crm_feedback_tickets` | status = 'pending' |
| 무료 티켓 사용률 | `crm_feedback_tickets` | 당월 used / issued |
| 이용권 만료 예정 | `memberships.end_date` | 30일 이내 만료 예정 |
| PT 잔여 부족 회원 | `pt_sessions.remaining` | 잔여 3회 이하 |
| 이번 달 매출 | `crm_sales` | 당월 합계 |
| 재등록 상담 대상 | `crm_re_registration` | status IN ('pending','in_progress') |

**API**
```
GET /admin/api/v1/dashboard?gym_id=:id
```

---

### Sector 2 — 회원 관리

**목적**: 회원 기본정보·상태·담당 트레이너 관리

**기능 요구사항**
- 회원 목록: 이름·연락처·가입일·이용권 구분·상태 표시
- 필터: 이용권 회원 / PT 회원 / 신규 / 기존 / 휴면
- 담당 트레이너 지정 (`crm_member_assignments`)
- 회원별 메모 기록 (`crm_member_notes`)
- 상담카드 확인 (앱 onboarding 데이터 read-only)
- 상태 태그 관리 (커스텀 태그, `crm_member_tags`)

**신규 CRM 테이블**

```sql
CREATE TABLE crm_member_assignments (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id   UUID NOT NULL,
  trainer_id  UUID REFERENCES crm_users(id),
  gym_id      UUID NOT NULL,
  assigned_at TIMESTAMPTZ DEFAULT NOW()
);

CREATE TABLE crm_member_tags (
  id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id UUID NOT NULL,
  gym_id    UUID NOT NULL,
  tag       VARCHAR(30) NOT NULL,
  color     VARCHAR(7)
);
```

**API**
```
GET    /admin/api/v1/members?gym_id=&filter=&page=&limit=
GET    /admin/api/v1/members/:id
PUT    /admin/api/v1/members/:id/trainer       -- 담당 트레이너 변경
POST   /admin/api/v1/members/:id/notes
GET    /admin/api/v1/members/:id/notes
POST   /admin/api/v1/members/:id/tags
DELETE /admin/api/v1/members/:id/tags/:tagId
```

---

### Sector 3 — 이용권 관리

**목적**: 회원권 기간·상태·재등록 시점 관리

**데이터 소스**: 앱 `memberships` 테이블 (read-only)  
**CRM 추가**: 휴회·연장·변경 이력은 `crm_membership_history`에 기록 후 앱 API 호출

**기능 요구사항**
- 이용권 목록: 회원명·시작일·종료일·잔여일·상태
- 만료 예정 필터: 7일 / 14일 / 30일
- 휴회·연장·변경 요청 기록 (CRM에서 승인 후 앱 API 통해 반영)
- 재등록 대상자 분류 → Sector 15 연동

```sql
CREATE TABLE crm_membership_history (
  id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id   UUID NOT NULL,
  gym_id      UUID NOT NULL,
  action      ENUM('pause','extend','change','cancel') NOT NULL,
  reason      TEXT,
  processed_by UUID REFERENCES crm_users(id),
  created_at  TIMESTAMPTZ DEFAULT NOW()
);
```

**API**
```
GET  /admin/api/v1/memberships?gym_id=&status=&expiring_in_days=
GET  /admin/api/v1/memberships/:memberId
POST /admin/api/v1/memberships/:memberId/actions   -- {action, reason}
```

---

### Sector 4 — PT 관리

**목적**: PT 수업권·잔여 횟수·트레이너별 현황 관리

**데이터 소스**: 앱 `pt_sessions`, `pt_packages` (read-only)

**기능 요구사항**
- PT 회원 목록: 잔여 횟수·담당 트레이너·진행 이력
- 잔여 횟수 부족 알림 (3회 이하)
- 트레이너별 PT 회원 현황
- 신규 / 재등록 / 소개 구분 (`crm_pt_registration_type`)
- PT 재등록 대상자 → Sector 15 연동

```sql
CREATE TABLE crm_pt_registration_type (
  id             UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id      UUID NOT NULL,
  gym_id         UUID NOT NULL,
  pt_package_id  UUID NOT NULL,      -- 앱 pt_packages.id 논리 참조
  reg_type       ENUM('new','re','referral') NOT NULL,
  referrer_id    UUID,               -- 소개 시 소개자 member_id
  created_at     TIMESTAMPTZ DEFAULT NOW()
);
```

**API**
```
GET /admin/api/v1/pt?gym_id=&trainer_id=&low_sessions=
GET /admin/api/v1/pt/:memberId
```

---

### Sector 5 — 루틴 관리

**목적**: 데일리 운동 루틴 등록·배정·수정

**데이터 소스**: 앱 `routines` 테이블 (read + CRM에서 write 가능한 유일한 영역)  
단, 앱 테이블에 컬럼 추가 없이 앱이 제공하는 루틴 CRUD API를 CRM이 호출한다.

**기능 요구사항**
- 루틴 목록: 부위별·목적별 분류 (가슴·등·하체·팔·복근·전신)
- 난이도 구분: 초보자·다이어트·근력증가
- 회원별 루틴 배정
- 트레이너별 루틴 등록 이력
- 루틴 수정·삭제 (앱 API 호출)
- 루틴 이행률 확인 → Sector 6 연동

**API** (앱 API 프록시 또는 직접 호출)
```
GET    /admin/api/v1/routines?gym_id=&category=&level=
POST   /admin/api/v1/routines                  -- 앱 루틴 API 호출
PUT    /admin/api/v1/routines/:id
DELETE /admin/api/v1/routines/:id
POST   /admin/api/v1/routines/:id/assign       -- {member_id}
GET    /admin/api/v1/routines/assignments?gym_id=
```

---

### Sector 6 — 루틴 이행 출석 관리

**목적**: 루틴 완료 = 출석으로 인정하는 CRM 차별화 기능

**데이터 소스**: 앱 `routine_logs` (read-only)

**기능 요구사항**
- 오늘 루틴 완료 회원 목록 (실시간)
- 루틴 미완료 회원 목록
- 회원별 출석률 (루틴 이행 완료일 기준)
- 주간·월간 이행률 통계
- 이행률 낮은 회원 (70% 미만) 자동 분류
- 트레이너별 담당 회원 이행률

**집계 로직**
```
출석률 = (루틴 완료 days / 회원권 경과 days) × 100
```

**API**
```
GET /admin/api/v1/attendance?gym_id=&date=&trainer_id=
GET /admin/api/v1/attendance/members/:memberId?period=weekly|monthly
GET /admin/api/v1/attendance/low-performers?gym_id=&threshold=70
```

---

### Sector 7 — 피드백 티켓 관리

**목적**: 월 2회 무료 피드백 티켓 지급·사용 현황 관리

```sql
CREATE TABLE crm_feedback_tickets (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id    UUID NOT NULL,
  gym_id       UUID NOT NULL,
  trainer_id   UUID REFERENCES crm_users(id),
  ticket_type  ENUM('free','paid') DEFAULT 'free',
  status       ENUM('issued','used','expired','pending') NOT NULL,
  issued_at    TIMESTAMPTZ DEFAULT NOW(),
  used_at      TIMESTAMPTZ,
  expires_at   TIMESTAMPTZ,
  month_year   VARCHAR(7) NOT NULL               -- 'YYYY-MM' 월별 관리
);
```

**기능 요구사항**
- 회원별 당월 티켓 지급·사용·만료 현황
- 자동 월 초 티켓 발급 (batch job)
- 담당 트레이너 답변 상태: `pending → in_progress → completed → held`
- 트레이너별 피드백 처리량

**API**
```
GET  /admin/api/v1/feedback-tickets?gym_id=&month=&status=
POST /admin/api/v1/feedback-tickets/issue           -- 수동 발급
PUT  /admin/api/v1/feedback-tickets/:id/status
GET  /admin/api/v1/feedback-tickets/trainer/:trainerId
```

---

### Sector 8 — 티켓 발행량 조절 (베타)

**목적**: 베타기간 동안 헬스장별 무료 티켓 발행량 제어

```sql
CREATE TABLE crm_ticket_settings (
  id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id              UUID NOT NULL UNIQUE,
  free_tickets_per_member INT DEFAULT 2,         -- 회원 1인당 월 기본 제공
  max_tickets_per_month   INT,                   -- 헬스장 전체 월 발행 한도
  is_beta             BOOLEAN DEFAULT TRUE,
  updated_at          TIMESTAMPTZ DEFAULT NOW(),
  updated_by          UUID REFERENCES crm_users(id)
);
```

**기능 요구사항**
- gym_admin이 자신의 헬스장 설정만 수정 가능
- super_admin은 전체 헬스장 설정 조회·수정 가능
- 발행 한도 초과 시 신규 티켓 발급 차단 + 알림
- 사용률·미사용률 집계

**API**
```
GET /admin/api/v1/ticket-settings/:gymId
PUT /admin/api/v1/ticket-settings/:gymId
GET /admin/api/v1/ticket-settings/:gymId/usage-summary
```

---

### Sector 9 — 티켓 구매 및 배포 (정식)

**목적**: 베타 이후 헬스장이 피드백 티켓을 구매하여 회원에게 배포

```sql
CREATE TABLE crm_ticket_purchases (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id       UUID NOT NULL,
  quantity     INT NOT NULL,
  unit_price   NUMERIC(10,2) NOT NULL,
  total_price  NUMERIC(10,2) NOT NULL,
  purchased_at TIMESTAMPTZ DEFAULT NOW(),
  purchased_by UUID REFERENCES crm_users(id)
);

CREATE TABLE crm_ticket_inventory (
  gym_id      UUID NOT NULL UNIQUE,
  total_qty   INT DEFAULT 0,
  used_qty    INT DEFAULT 0,
  updated_at  TIMESTAMPTZ DEFAULT NOW()
);
```

**기능 요구사항**
- 헬스장별 잔여 티켓 수량 관리
- 회원별 배포 이력 (`crm_feedback_tickets.ticket_type = 'paid'`)
- 무료 / 구매 티켓 구분 표시
- 소진율 기반 재구매 권고 알림

**API**
```
POST /admin/api/v1/ticket-purchases
GET  /admin/api/v1/ticket-purchases?gym_id=
GET  /admin/api/v1/ticket-inventory/:gymId
POST /admin/api/v1/ticket-inventory/:gymId/distribute   -- {member_id, qty}
```

---

### Sector 10 — 피드백 요청 관리

**목적**: 회원이 앱에서 보낸 피드백 요청을 CRM에서 처리

```sql
CREATE TABLE crm_feedback_requests (
  id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  ticket_id       UUID REFERENCES crm_feedback_tickets(id),
  member_id       UUID NOT NULL,
  gym_id          UUID NOT NULL,
  trainer_id      UUID REFERENCES crm_users(id),
  content         TEXT NOT NULL,
  attachments     JSONB,                          -- [{url, type: 'image'|'video'}]
  status          ENUM('pending','in_progress','completed','held') DEFAULT 'pending',
  response        TEXT,
  responded_at    TIMESTAMPTZ,
  created_at      TIMESTAMPTZ DEFAULT NOW()
);
```

**기능 요구사항**
- 피드백 요청 목록: 회원명·요청내용·첨부파일·상태·담당 트레이너
- 사진·영상 첨부 미리보기
- 트레이너 배정 및 답변 등록
- 답변 완료 시 회원 앱 알림 발송

**API**
```
GET  /admin/api/v1/feedback-requests?gym_id=&status=&trainer_id=
GET  /admin/api/v1/feedback-requests/:id
PUT  /admin/api/v1/feedback-requests/:id/assign      -- {trainer_id}
POST /admin/api/v1/feedback-requests/:id/respond     -- {response}
PUT  /admin/api/v1/feedback-requests/:id/status
```

---

### Sector 11 — 쪽지함

**목적**: 회원·트레이너·관리자 간 내부 소통 관리

```sql
CREATE TABLE crm_messages (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id       UUID NOT NULL,
  sender_type  ENUM('member','trainer','admin') NOT NULL,
  sender_id    UUID NOT NULL,
  receiver_type ENUM('member','trainer','admin') NOT NULL,
  receiver_id  UUID NOT NULL,
  content      TEXT NOT NULL,
  is_read      BOOLEAN DEFAULT FALSE,
  is_notice    BOOLEAN DEFAULT FALSE,         -- 공지성 쪽지
  parent_id    UUID REFERENCES crm_messages(id),
  created_at   TIMESTAMPTZ DEFAULT NOW()
);
```

**기능 요구사항**
- 받은·보낸 쪽지 목록
- 읽음·안읽음 상태 관리
- 일대일·공지 쪽지 구분
- 쪽지 검색 (키워드)
- 관리자 → 전체 회원 공지 발송

**API**
```
GET  /admin/api/v1/messages?gym_id=&inbox|outbox&page=
POST /admin/api/v1/messages
PUT  /admin/api/v1/messages/:id/read
GET  /admin/api/v1/messages/search?q=
```

---

### Sector 12 — CS / 문의 관리

**목적**: 회원 문의·앱 오류·결제 불편사항 처리

```sql
CREATE TABLE crm_cs_tickets (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id       UUID NOT NULL,
  member_id    UUID NOT NULL,
  category     ENUM('app_error','membership','pt','feedback','payment','other') NOT NULL,
  title        VARCHAR(200) NOT NULL,
  content      TEXT NOT NULL,
  status       ENUM('received','checking','processing','answered','closed') DEFAULT 'received',
  assigned_to  UUID REFERENCES crm_users(id),
  response     TEXT,
  responded_at TIMESTAMPTZ,
  created_at   TIMESTAMPTZ DEFAULT NOW()
);
```

**상태 흐름**: `received → checking → processing → answered → closed`

**API**
```
GET  /admin/api/v1/cs?gym_id=&status=&category=
GET  /admin/api/v1/cs/:id
PUT  /admin/api/v1/cs/:id/assign
POST /admin/api/v1/cs/:id/respond
PUT  /admin/api/v1/cs/:id/status
```

---

### Sector 13 — 트레이너 관리

**목적**: 트레이너 계정 및 업무 현황 관리

**기능 요구사항**
- 트레이너 계정 CRUD (crm_users where role='trainer')
- 담당 회원 목록 (`crm_member_assignments`)
- 트레이너별 오늘 피드백 대기·완료 건수
- 트레이너별 쪽지 미답변 건수
- 트레이너별 루틴 등록 수
- PT 재등록 상담 대상자 현황

**API**
```
GET    /admin/api/v1/trainers?gym_id=
POST   /admin/api/v1/trainers
PUT    /admin/api/v1/trainers/:id
DELETE /admin/api/v1/trainers/:id
GET    /admin/api/v1/trainers/:id/dashboard   -- 업무량 요약
GET    /admin/api/v1/trainers/:id/members
```

---

### Sector 14 — 매출 관리

**목적**: 이용권·PT·피드백 티켓 매출 통합 관리

```sql
CREATE TABLE crm_sales (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id       UUID NOT NULL,
  member_id    UUID NOT NULL,
  sales_type   ENUM('membership','pt','feedback_ticket') NOT NULL,
  reg_type     ENUM('new','re','referral'),
  trainer_id   UUID REFERENCES crm_users(id),
  amount       NUMERIC(12,2) NOT NULL,
  sale_date    DATE NOT NULL,
  note         TEXT,
  created_at   TIMESTAMPTZ DEFAULT NOW()
);
```

**기능 요구사항**
- 월별·유형별 매출 합계
- 신규·재등록·소개 구분
- 트레이너별 매출 현황
- 목표 대비 달성률 (`crm_sales_targets`)
- 부가수익(피드백 티켓) 별도 집계

```sql
CREATE TABLE crm_sales_targets (
  gym_id      UUID NOT NULL,
  month_year  VARCHAR(7) NOT NULL,    -- 'YYYY-MM'
  target      NUMERIC(12,2) NOT NULL,
  PRIMARY KEY (gym_id, month_year)
);
```

**API**
```
GET  /admin/api/v1/sales?gym_id=&month=&type=
GET  /admin/api/v1/sales/trainer/:trainerId?month=
GET  /admin/api/v1/sales/summary?gym_id=&month=
PUT  /admin/api/v1/sales/targets/:gymId
POST /admin/api/v1/sales
```

---

### Sector 15 — 재등록 관리

**목적**: 이탈 가능성이 높은 회원을 사전에 파악하여 재등록 상담 관리

```sql
CREATE TABLE crm_re_registration (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  member_id    UUID NOT NULL,
  gym_id       UUID NOT NULL,
  reason       ENUM('membership_expiry','pt_low','low_routine','low_app_usage','feedback_history') NOT NULL,
  status       ENUM('pending','in_progress','success','failed','hold') DEFAULT 'pending',
  assigned_to  UUID REFERENCES crm_users(id),
  memo         TEXT,
  scheduled_at TIMESTAMPTZ,
  resolved_at  TIMESTAMPTZ,
  created_at   TIMESTAMPTZ DEFAULT NOW()
);
```

**자동 분류 기준**

| 조건 | 분류 이유 |
|------|-----------|
| 이용권 만료 30일 이내 | `membership_expiry` |
| PT 잔여 3회 이하 | `pt_low` |
| 루틴 이행률 70% 미만 | `low_routine` |
| 앱 미접속 14일 이상 | `low_app_usage` |
| 피드백 요청 이력 있음 | `feedback_history` (긍정 신호) |

**API**
```
GET  /admin/api/v1/re-registration?gym_id=&status=&reason=
PUT  /admin/api/v1/re-registration/:id/status
POST /admin/api/v1/re-registration/:id/memo
POST /admin/api/v1/re-registration/auto-classify   -- 배치 실행용
```

---

### Sector 16 — 알림 / 공지 관리

**목적**: 회원·트레이너에게 CRM 내부 알림 및 앱 푸시 발송

```sql
CREATE TABLE crm_announcements (
  id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  gym_id       UUID NOT NULL,
  author_id    UUID REFERENCES crm_users(id),
  target       ENUM('all_members','all_trainers','specific','all') NOT NULL,
  target_ids   UUID[],                     -- 특정 대상일 경우
  title        VARCHAR(200) NOT NULL,
  content      TEXT NOT NULL,
  send_push    BOOLEAN DEFAULT FALSE,      -- 앱 푸시 알림 여부
  sent_at      TIMESTAMPTZ,
  created_at   TIMESTAMPTZ DEFAULT NOW()
);
```

**알림 유형**

| 유형 | 트리거 |
|------|--------|
| 이용권 만료 예정 | 배치: 7일·3일·1일 전 자동 발송 |
| 피드백 답변 완료 | Sector 10 응답 완료 시 자동 |
| 루틴 업데이트 | 루틴 배정 시 자동 |
| 재등록 안내 | Sector 15 상태 변경 시 수동 |
| 센터 공지 | 관리자 수동 작성 |

**API**
```
GET  /admin/api/v1/announcements?gym_id=
POST /admin/api/v1/announcements
POST /admin/api/v1/announcements/:id/send
GET  /admin/api/v1/announcements/templates     -- 자동 알림 템플릿
```

---

### Sector 17 — 통계 / 리포트

**목적**: 운영 데이터 분석 및 의사결정 지원

**기능 요구사항**
- 전체 회원 수·활성·휴면 추이 (월별)
- 루틴 이행률 추이
- 출석률 추이
- 피드백 티켓 사용률
- 트레이너별 피드백 처리량
- 이용권·PT 재등록률
- 월별 매출 (이용권·PT·티켓 구분)
- 재등록 성공률

**권장 구현**: 집계 전용 materialized view 또는 별도 analytics 테이블을 일별 배치로 갱신 (앱 메인 DB 부하 분산)

```sql
CREATE TABLE crm_daily_stats (
  gym_id            UUID NOT NULL,
  stat_date         DATE NOT NULL,
  total_members     INT,
  active_members    INT,
  dormant_members   INT,
  routine_completed INT,
  attendance_rate   NUMERIC(5,2),
  feedback_issued   INT,
  feedback_used     INT,
  PRIMARY KEY (gym_id, stat_date)
);
```

**API**
```
GET /admin/api/v1/stats/overview?gym_id=&from=&to=
GET /admin/api/v1/stats/attendance?gym_id=&period=
GET /admin/api/v1/stats/feedback?gym_id=&period=
GET /admin/api/v1/stats/sales?gym_id=&period=
GET /admin/api/v1/stats/re-registration?gym_id=&period=
```

---

## 5. 배치 작업 (Scheduled Jobs)

| 작업 | 주기 | 설명 |
|------|------|------|
| 피드백 티켓 월별 자동 발급 | 매월 1일 00:00 | `crm_ticket_settings` 설정 기준 발급 |
| 만료 티켓 처리 | 매일 00:00 | 당월 미사용 티켓 `expired` 처리 |
| 재등록 대상자 자동 분류 | 매일 06:00 | `crm_re_registration` auto-classify |
| 이용권 만료 알림 발송 | 매일 09:00 | 7일·3일·1일 전 대상자 푸시 |
| 일별 통계 집계 | 매일 02:00 | `crm_daily_stats` 갱신 |

---

## 6. 비기능 요구사항

| 항목 | 요구사항 |
|------|---------|
| 인증 | JWT, 토큰 만료 24h (refresh 7d) |
| 권한 | gym_admin은 자신의 gym_id 데이터만 접근 가능 |
| 응답속도 | 목록 API p95 < 500ms |
| DB 부하 | 통계 쿼리는 Read Replica 사용 |
| 로깅 | 모든 CRM write 작업 audit log 기록 |
| 앱 영향 | 기존 앱 테이블 스키마 변경 금지 |

---

## 7. 프론트엔드 구현 지침

- **프레임워크**: React + TypeScript (또는 팀 기존 스택)
- **라우팅**: `/admin/*` 경로, 앱 회원 경로와 분리
- **상태관리**: React Query (서버 상태) + Zustand (UI 상태)
- **UI 참고**: 첨부 구조도 이미지 기준 17개 섹터 좌측 사이드바 네비게이션
- **대시보드**: 카드형 KPI 위젯 + 경고 항목 인라인 표시
- **테이블**: 무한스크롤 또는 페이지네이션, 필터·검색 기본 제공
- **반응형**: 태블릿·데스크탑 (모바일 불필요)