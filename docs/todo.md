# LINK_Fit Admin — TODO

> 앱(linkfit) 연동 헬스장 운영용 관리자 페이지 작업 목록

---

## 🔴 Critical (보안 / 필수)

- [x] **인증 로직 교체** — `SecurityConfig` + DB 기반 `UserDetailsService` (BCrypt 해싱 적용)
- [x] **Spring Security 도입** — `spring-boot-starter-security` 추가, `SecurityConfig` 작성 (세션/CSRF 보호)
- [x] **인가(Authorization) 처리** — `SecurityFilterChain`으로 `/login`, 정적 리소스 외 모든 경로 인증 필요
- [x] **비밀번호 암호화** — `BCryptPasswordEncoder` 빈 등록 및 적용

---

## 🟠 High (기능 구현)

### 1. 헬스장 이용정보 관리
- [x] 헬스장 설정 페이지 (`/settings`) — 기본 정보, 실시간 오픈여부 토글
- [x] 운영시간 설정 (요일별 오픈·마감 시간) — `gym_setting` DB 연동
- [x] 휴일 설정 — 공휴일·임시 휴관일 날짜 등록·삭제 (`gym_holiday` DB 연동, 연도별 조회)
- [x] 공지사항 배너 등록·삭제·활성화 — 이미지 업로드 방식 (`gym_banner` DB + 파일 저장, 앱 표시는 앱개발팀 연동)

### 2. 직원(트레이너) 관리
- [x] 트레이너 목록 조회 (`/staff`) — 이름, 연락처, 역할 표시
- [x] 트레이너 계정 생성·수정·비활성화 (모달 UI + REST API)
- [x] 역할(Role) 구분 (슈퍼 관리자 / 일반 관리자 / 트레이너)
- [x] 출근 여부 현황판 — 오늘 출근/미출근 트레이너 목록 실시간 표시 (`staff_attendance` DB + 현황판 UI)
- [x] 출근 기록 조회 — 날짜별 출퇴근 이력 (기간 필터, 트레이너별 조회, 근무시간 계산)

### 3. 회원 관리
- [x] 회원 목록 페이지 (`/members`) — 검색·필터 구현
- [x] 회원 상세 조회 / 등록 / 수정 / 삭제 (모달 UI + REST API)
- [x] 회원권 상태 관리 (활성 / 정지) — 목록 필터 + PATCH `/api/members/{id}/status`
- [x] 회원 검색 및 필터링 — 이름/전화번호 검색, 상태 필터
- [x] 구독 등급 관리 (BASIC / LIGHT_FIT / REGULAR_FIT / INTENSIVE_FIT) — 목록 배지 + tier 필터 + 수동 조정 UI (`user_profiles.tier` 연동)
- [x] OT/PT 유형 부여 (관리자 전용) — `memberType` 관리 UI (일반 / OT / PT) + PATCH API
- [x] 이용권 부여·회수·만료일 설정 (`membership` 테이블 연동, 이용권 모달 UI)
- [x] 회원 탈퇴 처리 (소프트 탈퇴: `users.deleted_at` 세팅, WITHDRAWN 상태 배지)

### 4. 이용권(티켓) 관리 _(신규 — DB 테이블 이미 존재)_
> `member_tickets` (잔량), `ticket_logs` (이력), `ticket_purchases` (구매 이력) 활용
- [ ] 회원별 티켓 잔량 조회 — `member_tickets` (ONE_POINT / FEEDBACK / PHOTO / VIDEO)
- [ ] 티켓 지급·차감 처리 — `ticket_logs` INSERT (action_type: CHARGE / USE / GIFT)
- [ ] 티켓 구매 이력 조회 — `ticket_purchases`
- [ ] 원포인트 카드 발급 현황 — `onepoint_cards` (ISSUABLE / COMPLETED / CANCELLED)
- [ ] 트레이너 월별 발급 쿼터 조회 — `trainer_onepoint_quota.issuable_remaining`

### 5. 수업 관리
> `class_session` / `class_attendee` 테이블은 DB에 레거시로 존재 — 어드민 전용으로 유지 사용 가능  
> 트레이너 수업 일정은 `trainer_schedules` 테이블이 실사용 중
- [x] 그룹 수업 / 개인 레슨 / OT 일정 관리 (`/classes`) — 유형 탭 필터
- [x] 수업 등록 / 취소 (모달 UI + API)
- [ ] 수업 일정 캘린더 뷰 — `trainer_schedules` 기반 전체 PT·OT·원포인트 일정
- [ ] 수업 신청자 목록 조회 — `GET /api/classes/{id}/attendees` UI 미구현
- [ ] 원포인트 레슨 신청서 조회 및 승인·거절 — `onepoint_requests` 테이블 사용
- [ ] OT 배정 — 신규 회원 → 트레이너 매칭 (`trainer_members` + `user_profiles.trainer_id`)
- [ ] 수업 수정 UI (등록/취소만 구현, 수정 미구현)

### 6. 출석 관리
- [x] 출석 체크 기능 (`/attendance`) — 페이지 + 체크인 모달 + POST API
- [x] 일별 출석 현황 조회 (날짜 필터 + 유형별 집계)
- [ ] 주별 / 월별 출석 현황 — UI 기간 탭 미구현
- [ ] 회원별 출석 현황 조회 (이번 달 출석률)
- [ ] 루틴 실행 이력 조회 (날짜별 완료·미완료)
- [ ] 장기 미출석 회원 알림 목록 (예: 7일 이상 미출석)
- [ ] 유증(정지) 전용 UI — `POST /api/members/{id}/freeze` API만 존재

### 7. 상담 관리
- [x] 신규 상담 / 기존 회원 상담 등록 및 조회 (`/consults`) — 모달 + API
- [x] 상담 이력 관리 — 목록 조회, 유형 필터, 삭제

### 8. 매출 및 상품 관리
- [x] 매출 대시보드 — 일간·주간·월간 집계 + 날짜 네비게이션
- [x] 매출 펼치기(expand) — 카테고리별 상세 내역
- [x] 상품 목록 관리 (`/products`) — 등록·수정·삭제
- [ ] 구독권 판매 현황 — 등급별 가입자 수, 신규·해지 추이
- [ ] 티켓 판매 현황 — 원포인트·피드백 판매량
- [ ] 결제 내역 조회·환불 처리
- [ ] 매출 리포트 엑셀 내보내기

### 9. 메시지(공지·이벤트)
> ⚠️ `message` / `message_recipient` 테이블은 DB에서 **미사용(레거시)**  
> 실제 앱 쪽지는 `message_conversation` + `chat_message` 사용 (category: '공지'/'이벤트')
- [ ] 메시지 페이지 리팩토링 — `message_conversation`(category='공지'/'이벤트') 기반으로 전환
- [ ] 전체 회원 공지 발송 — `message_conversation` + `chat_message` INSERT (모든 회원 대상)
- [ ] 트레이너 전용 공지 발송
- [ ] 특정 회원 그룹 발송 (tier별, 담당 트레이너별)
- [ ] 이벤트 공지 등록 (category='이벤트')
- [ ] 발송 이력 조회 — `message_conversation` + `chat_message` 기반

---

## 🟡 Medium (대시보드 개선)

- [x] **날짜 네비게이션 동작 구현** — `<` `>` 버튼 클릭 시 날짜 변경 및 API 재호출
- [x] **기간 탭 데이터 연동** — 일간 / 주간 / 월간 탭 전환 시 API 파라미터 변경 및 재호출
- [x] **서브 탭 데이터 연동** — 회원 통계, 수업 통계, 출석 통계 서브 탭 동작
- [x] **실시간 데이터 연동** — 대시보드 전체 수치를 AJAX API 호출로 교체

---

## 🔵 New (프론트엔드 개편 대응 — 코드 연동)

> DB 스키마는 `docs/database.md` 기준으로 이미 구성되어 있음. **DB 변경 없이** 어드민 코드 연동만 필요.

### ⚠️ Mapper 호환성 수정 _(완료)_
> `users.user_id VARCHAR(50)` 대응 및 도메인 타입 수정 완료.
- [x] `MemberMapper.xml` — `user_id` 타입을 `VARCHAR(50)`으로 수정 (JOIN, WHERE, INSERT 전체)
- [x] `StaffMapper.xml` — 동일
- [x] `AttendanceMapper.xml` — 동일
- [x] `ConsultMapper.xml` — 동일
- [x] `SaleMapper.xml` — 동일
- [x] `Member` 도메인 클래스 — `id` 필드 타입 `Long` → `String` 변경
- [x] `MemberApiController` 등 id 파라미터 타입 수정

### DB 스키마 확인 (이미 존재 — 코드 연동 필요)
- [x] 등급 컬럼 — `user_profiles.tier ENUM('BASIC','LIGHT_FIT','REGULAR_FIT','INTENSIVE_FIT')`
- [x] 회원권 유형 — `user_profiles.member_type ENUM('PT','OT')` (null = 일반)
- [x] `user_daily_habits.daily_note TEXT`
- [x] `user_medical_history`: `adult_disease_detail`, `joint_detail`, `other_detail` 컬럼 존재
- [x] `user_exercise_info`: `plan_frequency`, `exercise_note` (구 복잡 필드 이미 제거됨)
- [x] 티켓 테이블: `member_tickets`, `ticket_logs`, `ticket_purchases` 이미 존재

### 4-1. 회원 등급(Tier) 관리 _(완료)_
> 등급은 `users.grade`가 아닌 `user_profiles.tier`에 저장됨
- [x] `Member` 도메인 클래스에 `tier` 필드 추가 (`String`)
- [x] `MemberMapper.xml` — SELECT 쿼리에 `up.tier` 컬럼 포함
- [x] 회원 목록 페이지 — `tier` 배지 컬럼 표시 (BASIC / LIGHT_FIT / REGULAR_FIT / INTENSIVE_FIT)
- [x] 회원 목록 페이지 — `tier` 필터 드롭다운 추가 (`?tier=` 파라미터)
- [x] 회원 상세 모달 — tier 표시 및 수동 조정 셀렉트 박스 (관리자 전용)
- [x] `PATCH /api/members/{id}/tier` 엔드포인트 구현 → `UPDATE user_profiles SET tier`

### 4-2. OT/PT 유형 부여 (관리자 전용) _(완료)_
> `user_profiles.member_type ENUM('PT','OT')` — null이면 일반 회원
- [x] 회원 상세 모달에 `memberType` 관리 UI 추가 (일반 / OT / PT 셀렉트)
- [x] `PATCH /api/members/{id}/member-type` 엔드포인트 구현 → `UPDATE user_profiles SET member_type`
- [x] `MemberMapper.xml` — `updateMemberType` 쿼리 추가

### 4-3. 회원 프로필 조회 화면 업데이트
- [ ] 회원 상세 모달 — 섹션 재정렬 (기본정보 → 방문경로 → 운동목적 → 운동계획 → 일상/식단 → 특이사항)
- [ ] 회원 상세 모달 — `daily_note`, `exercise_note`, `adult_disease_detail`, `joint_detail` 필드 표시
- [ ] 의료 이력 표시: 성인병/관절 그룹명 + 각 상세 텍스트 표시
- [ ] PT 잔여 횟수 표시 (`user_profiles.pt_sessions_left`)

---

## 🟢 Low (개선 사항)

### 백엔드
- [x] **데이터베이스 연동 — MyBatis + MariaDB**
  - [x] `build.gradle` 의존성 추가 (`mybatis-spring-boot-starter:3.0.3`, `mariadb-java-client`)
  - [x] `application-dev.yml` DataSource 및 MyBatis 설정
  - [x] 전 도메인 Mapper 인터페이스 + XML 작성 (Member, Staff, Class, Attendance, Consult, Product, Message, Sale, Dashboard, GymSetting)
  - [x] DB 연동 완료 — Mock → MyBatis 서비스 교체, Spring Boot 4.x MyBatisConfig 수동 설정
- [x] **로그아웃 처리** — Spring Security logout 설정 (세션 무효화, JSESSIONID 쿠키 삭제)
- [x] **예외 처리** — `GlobalExceptionHandler`(@ControllerAdvice) + 404/500 에러 페이지
- [ ] **로깅** — Logback 설정 (접근 로그, 오류 로그)

### 프론트엔드
- [x] **반응형(Responsive) 지원** — 모바일 사이드바 오버레이 토글
- [x] **설정 페이지** — 헬스장 정보, 실시간 오픈여부, 요일별 운영시간, 공지사항 (`gym_setting` DB 연동)
- [x] **로딩 상태 표시** — 대시보드 스켈레톤 UI (`dashboard.css` shimmer 애니메이션)
- [x] **Favicon 및 메타 태그** 설정

### 인프라 / 배포
- [x] **환경 변수 분리** — `application-dev.yml` / `application-prod.yml`, prod는 환경변수 사용
- [x] **포트 설정** — `17579` 포트 고정
- [ ] **테스트 코드 작성** — `src/test` 비어 있음

---

## 📡 API 명세

> Thymeleaf SSR 기반이므로 페이지 렌더링은 Controller → Model → Template 방식.
> 동적 갱신이 필요한 항목은 아래 REST 엔드포인트를 AJAX로 호출.
> `period` 공통 파라미터: `daily` / `weekly` / `monthly`

### 화면별 필요 API

#### 대시보드 (`/dashboard`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 회원 통계 | GET | `/api/dashboard/members?date=&period=&type=` |
| 상담 통계 | GET | `/api/dashboard/consults?date=&period=` |
| 수업 통계 | GET | `/api/dashboard/classes?date=&period=&type=` |
| 매출 통계 | GET | `/api/dashboard/revenue?date=&period=` |
| 매출 항목 상세 | GET | `/api/dashboard/revenue/{category}?date=&period=` |
| 출석 통계 | GET | `/api/dashboard/attendance?date=&period=&type=` |

#### 회원 관리 (`/members`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 회원 목록 조회 | GET | `/api/members?page=&size=&status=&tier=&keyword=` |
| 회원 상세 조회 | GET | `/api/members/{id}` |
| 회원 등록 | POST | `/api/members` |
| 회원 수정 | PUT | `/api/members/{id}` |
| 회원 삭제 | DELETE | `/api/members/{id}` |
| 회원 상태 변경 | PATCH | `/api/members/{id}/status` |
| 등급 변경 (관리자) | PATCH | `/api/members/{id}/tier` |
| 유형 변경 (OT/PT) | PATCH | `/api/members/{id}/member-type` |
| 유증(정지) 처리 | POST | `/api/members/{id}/freeze` |
| 회원권 목록 조회 | GET | `/api/members/{id}/memberships` |
| 회원권 등록 | POST | `/api/members/{id}/memberships` |

#### 직원 관리 (`/staff`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 직원 목록 조회 | GET | `/api/staff?page=&size=&role=` |
| 직원 상세 조회 | GET | `/api/staff/{id}` |
| 직원 등록 | POST | `/api/staff` |
| 직원 수정 | PUT | `/api/staff/{id}` |
| 직원 삭제 | DELETE | `/api/staff/{id}` |
| 직원 역할 변경 | PATCH | `/api/staff/{id}/role` |
| 출근 현황 조회 | GET | `/api/staff/attendance` |

#### 수업 관리 (`/classes`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 수업 목록 조회 | GET | `/api/classes?type=&date=&page=&size=` |
| 수업 상세 조회 | GET | `/api/classes/{id}` |
| 수업 등록 | POST | `/api/classes` |
| 수업 수정 | PUT | `/api/classes/{id}` |
| 수업 취소 | DELETE | `/api/classes/{id}` |
| 수업 신청자 목록 | GET | `/api/classes/{id}/attendees` |
| 수업 신청 | POST | `/api/classes/{id}/attendees` |
| 수업 신청 취소 | DELETE | `/api/classes/{id}/attendees/{memberId}` |
| 원포인트 신청 목록 | GET | `/api/classes/onepoint/requests` |
| 원포인트 신청 처리 | PATCH | `/api/classes/onepoint/requests/{id}/status` |

#### 출석 관리 (`/attendance`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 출석 현황 조회 | GET | `/api/attendance?date=&period=` |
| 출석 체크 | POST | `/api/attendance` |
| 출석 취소 | DELETE | `/api/attendance/{id}` |
| 유증 회원 목록 | GET | `/api/attendance/freeze?date=` |
| 장기 미출석 회원 조회 | GET | `/api/attendance/inactive?days=` |

#### 상담 관리 (`/consults`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 상담 목록 조회 | GET | `/api/consults?page=&size=&type=` |
| 상담 상세 조회 | GET | `/api/consults/{id}` |
| 신규 상담 등록 | POST | `/api/consults` |
| 상담 수정 | PUT | `/api/consults/{id}` |
| 상담 삭제 | DELETE | `/api/consults/{id}` |

#### 매출 관리 (`/revenue`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 매출 요약 조회 | GET | `/api/revenue/summary?date=&period=` |
| 등록/재등록 매출 상세 | GET | `/api/revenue/memberships?date=&period=` |
| 그룹 수업 매출 상세 | GET | `/api/revenue/group-classes?date=&period=` |
| 개인 레슨 매출 상세 | GET | `/api/revenue/pt?date=&period=` |
| 락커 매출 상세 | GET | `/api/revenue/lockers?date=&period=` |
| 공동 물품 매출 상세 | GET | `/api/revenue/items?date=&period=` |

#### 상품 관리 (`/products`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 상품 목록 조회 | GET | `/api/products?type=&page=&size=` |
| 상품 상세 조회 | GET | `/api/products/{id}` |
| 상품 등록 | POST | `/api/products` |
| 상품 수정 | PUT | `/api/products/{id}` |
| 상품 삭제 | DELETE | `/api/products/{id}` |

#### 메시지 (`/messages`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 메시지 목록 조회 | GET | `/api/messages?page=&size=` |
| 메시지 발송 | POST | `/api/messages` |
| 메시지 상세 조회 | GET | `/api/messages/{id}` |
| 메시지 삭제 | DELETE | `/api/messages/{id}` |

#### 설정 (`/settings`)
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 헬스장 설정 조회 | GET | `/api/settings/gym` |
| 헬스장 설정 저장 | PUT | `/api/settings/gym` |
| 실시간 오픈여부 변경 | PATCH | `/api/settings/gym/open` |

#### 인증
| 목적 | Method | 엔드포인트 |
|---|---|---|
| 로그인 페이지 | GET | `/login` |
| 로그인 처리 | POST | `/login` |
| 로그아웃 | POST | `/logout` |

---

## 📌 참고 사항

| 항목 | 현황 |
|---|---|
| 서버 포트 | `17579` |
| 어드민 계정 | `admin` / `admin1234` (DB 기반 UserDetailsService, BCrypt 해싱) |
| Spring Boot | `4.0.4` |
| Java | `21` |
| 빌드 도구 | Gradle `8.14` |
| DB | MariaDB 10.11.14 + MyBatis 3.0.3 |
| 인증 | Spring Security 7.x (Form Login, CSRF 보호) |
| 앱 백엔드 | `localhost:17577` |
| 설정 파일 | `application.yml` / `application-dev.yml` / `application-prod.yml` |

### DB 호환성 주의사항

| 항목 | 실제 DB | 어드민 현재 코드 | 상태 |
|------|---------|----------------|------|
| `users.user_id` 타입 | `VARCHAR(50)` (loginId) | `VARCHAR(50)` | ✅ 수정 완료 |
| `users.id` | `BIGINT` AUTO_INCREMENT (내부용) | 미사용 | — |
| 등급 컬럼 | `user_profiles.tier` | `user_profiles.tier` | ✅ 수정 완료 |
| 회원권 유형 | `user_profiles.member_type` | 동일 | ✅ |
| 메시지 시스템 | `message_conversation` + `chat_message` | `message` + `message_recipient` (레거시) | **재설계 필요** |
| 티켓 테이블 | `member_tickets`, `ticket_logs`, `ticket_purchases` | 미연동 | 연동 가능 |
| `class_session` | 레거시(미사용) | 어드민 전용 유지 | 어드민 독립 사용 허용 |
