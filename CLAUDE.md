# LINK_Fit Admin — CLAUDE.md

헬스장 관리자 웹 어드민 프로젝트. LINK_Fit 앱의 백오피스로, 회원/직원/수업/출석/상담/매출/상품/메시지/CRM/피드백 등을 통합 관리한다.

---

## 기술 스택

| 항목 | 내용 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.4 |
| Security | Spring Security 7.x (JWT, Stateless) |
| Template | Thymeleaf (SSR) |
| Persistence | MyBatis + MariaDB (DB 연동 완료) |
| Build | Gradle 8.14 |
| Port | **17579** |
| 계정 | `admin` / `admin1234` (DB 기반, BCrypt 해싱) |

---

## DB 연결 정보

| 항목 | 값 |
|---|---|
| DBMS | MariaDB 10.11.14 |
| Host | `localhost` |
| Port | `3306` |
| Database | `linkfit` |
| Username | `linkfit` |
| Password | `link_fit!` |
| Profile | `dev` (기본값, `application.yml`에 설정됨) |

> 연결 설정 파일: `src/main/resources/application-dev.yml`
> 실제 앱 서비스 DB(`linkfit`)를 공유하여 사용한다.
> DDL 상세는 `docs/sql.md` / `docs/database.md` 참고.

### 어드민 전용 테이블

| 테이블 | 설명 |
|---|---|
| `admin_user` | 어드민 로그인 계정 |
| `product` | 상품/이용권 |
| `membership` | 회원권 구매 이력 |
| `member_freeze` | 유증(정지) 기록 |
| `class_session` | 그룹/PT 수업 (어드민 독립 사용) |
| `class_attendee` | 수업 신청자 |
| `attendance` | 출석 기록 |
| `consult` | 상담 기록 |
| `sale` | 매출 내역 |
| `gym_setting` | 헬스장 운영 설정 |

### 앱 DB 테이블 (재사용)

| 테이블 | 어드민 도메인 매핑 |
|---|---|
| `users` (role=MEMBER) | `member` |
| `users` (role=TRAINER) | `staff` |
| `user_profiles` | member/staff 상세 정보 (tier, member_type, trainer_id 등) |
| `member_tickets` | 티켓 잔량 (ONE_POINT / FEEDBACK / PHOTO / VIDEO) |
| `ticket_logs` | 티켓 지급·차감 이력 |
| `ticket_purchases` | 티켓 구매 이력 |
| `trainer_schedules` | 트레이너 PT·OT 일정 |
| `message_conversation` + `chat_message` | 실제 앱 쪽지/공지 (category: '공지'/'이벤트') |

---

## 프로젝트 구조

```
src/main/java/com/linkfit/admin/
├── LinkFitAdminApplication.java
├── common/
│   └── ApiResponse.java              ← 공통 REST 응답 래퍼 record
├── config/
│   ├── SecurityConfig.java           ← JWT Stateless 보안 설정 (CSRF 비활성, CORS 설정)
│   └── MyBatisConfig.java            ← Spring Boot 4.x MyBatis 수동 설정
├── controller/
│   ├── LoginController.java          ← GET /login, GET /dashboard (페이지 반환)
│   ├── PageController.java           ← 나머지 메뉴 페이지 GetMapping 모음
│   └── api/                          ← @RestController (JSON 반환)
│       ├── AuthApiController.java    ← POST /api/auth/login, /api/auth/logout
│       ├── DashboardApiController.java
│       ├── MemberApiController.java
│       ├── StaffApiController.java
│       ├── ClassApiController.java
│       ├── AttendanceApiController.java
│       ├── ConsultApiController.java
│       ├── ProductApiController.java
│       ├── MessageApiController.java
│       ├── RevenueApiController.java
│       ├── MembershipApiController.java
│       ├── PtApiController.java
│       ├── ReRegistrationApiController.java
│       ├── SettingApiController.java
│       ├── StatsApiController.java
│       ├── FeedbackApiController.java
│       ├── CrmInboxApiController.java
│       ├── CrmSalesApiController.java
│       ├── CsTicketApiController.java
│       └── AnnouncementApiController.java
├── domain/                           ← VO/DTO (getter/setter 방식)
│   ├── AdminUser.java
│   ├── Member.java / Staff.java / ClassSession.java / ClassAttendee.java
│   ├── Attendance.java / Consult.java / Product.java / Message.java / Sale.java
│   ├── Membership.java / MemberFreeze.java / MemberTicket.java
│   ├── PtMember.java / ReRegistration.java / GymSetting.java / TicketSettings.java
│   ├── CrmUser.java / CrmAnnouncement.java / CrmCsTicket.java / CrmDailyStats.java
│   ├── CrmMemberNote.java / CrmMemberTag.java / CrmMembershipHistory.java / CrmMessage.java / CrmSale.java
│   └── FeedbackRequest.java / FeedbackTicket.java
├── service/                          ← 서비스 인터페이스
├── service/mybatis/                  ← MyBatis 구현체 (현재 사용 중)
│   ├── MyBatisMemberService.java
│   ├── MyBatisStaffService.java
│   └── ... (전 도메인 구현 완료)
├── service/mock/                     ← 더미 구현체 (현재 @Service 미등록, 비사용)
├── mapper/                           ← MyBatis @Mapper 인터페이스 (21개)
├── security/
│   ├── JwtUtil.java                  ← JWT 생성/검증 (JJWT)
│   ├── JwtCookieFilter.java          ← 쿠키에서 crm_token 추출 → SecurityContext 세팅
│   └── CrmUserDetails.java           ← UserDetails 구현체
├── service/
│   └── AdminUserDetailsService.java  ← DB 기반 UserDetailsService
├── scheduler/
│   └── DailyStatsScheduler.java      ← 일별 통계 자동 집계
└── exception/
    └── GlobalExceptionHandler.java   ← @ControllerAdvice, 404/500 처리

src/main/resources/
├── application.yml                   ← server.port=17579, profiles.active=dev
├── application-dev.yml               ← 로컬 DB 접속 정보, JWT 설정, CORS 설정
├── application-prod.yml              ← 환경변수 ${DB_URL} 방식
├── mapper/                           ← MyBatis XML (21개, 전 도메인 작성 완료)
├── static/css/
│   ├── common.css                    ← 사이드바/레이아웃/테이블/모달 공통 스타일
│   ├── dashboard.css                 ← 대시보드 전용 + 스켈레톤 UI
│   └── login.css                     ← 로그인 전용
└── templates/
    ├── fragments/sidebar.html        ← Thymeleaf 재사용 사이드바
    ├── login.html / dashboard.html
    ├── members.html / staff.html / classes.html / attendance.html
    ├── consults.html / revenue.html / products.html / messages.html
    ├── pt.html / settings.html / reregistration.html / feedback.html
    ├── inbox.html / cs.html / crm-sales.html / announcements.html
    └── error/404.html / error/500.html
```

---

## 아키텍처 패턴

### 인증 방식 — JWT (Stateless)
Spring Security 세션 없이 JWT 쿠키(`crm_token`) 기반으로 동작한다.
`JwtCookieFilter`가 모든 요청에서 쿠키를 추출해 `SecurityContextHolder`에 인증 정보를 세팅한다.
로그인/로그아웃은 `POST /api/auth/login`, `POST /api/auth/logout`으로 처리한다.

```java
// JWT 클레임 구조
sub: userId (admin_user.user_id)
branchCode, username, role, gymId
```

### REST API 응답 형식
모든 API는 `ApiResponse<T>` record를 사용한다.

```java
ApiResponse.ok(data)       // { success: true, message: "ok", data: ... }
ApiResponse.ok()           // data: null
ApiResponse.error("메시지") // { success: false, message: "...", data: null }
```

### 사이드바 fragment 사용법
모든 페이지는 `fragments/sidebar.html`을 `th:replace`로 삽입한다.

```html
<aside th:replace="~{fragments/sidebar :: sidebar('members')}"></aside>
```

activePage 값: `dashboard` / `members` / `staff` / `classes` / `attendance` / `consults` / `revenue` / `products` / `messages` / `pt` / `reregistration` / `feedback` / `inbox` / `cs` / `crm-sales` / `announcements` / `settings`

### 프론트엔드 데이터 흐름
페이지 렌더링은 Controller → Thymeleaf SSR.
동적 데이터(날짜 변경, 탭 전환, 테이블 갱신 등)는 각 페이지의 인라인 JS에서 `fetch()`로 REST API를 호출한다.
인증은 쿠키로 자동 전달되므로 `credentials: 'include'` 설정 필요.

---

## 개발 진행 현황

### ✅ 완료

**보안 / 인증**
- Spring Security 7.x + JWT Stateless 인증 (`JwtCookieFilter`, `JwtUtil`)
- DB 기반 `AdminUserDetailsService` (BCrypt 해싱)
- CORS 설정 (`application-dev.yml`의 `app.cors.allowed-origins`)
- 로그아웃 쿠키 삭제 처리

**백엔드**
- `ApiResponse<T>` 공통 응답 포맷
- `GlobalExceptionHandler` — 404/500 에러 페이지 반환 또는 JSON 응답
- REST API Controller 19개 — 전 도메인 엔드포인트 구현
- 도메인 클래스 27개 (기본 도메인 + CRM 계열 + Feedback 계열)
- 서비스 인터페이스 12개 + MyBatis 구현체 12개 (DB 연동 완료)
- MyBatis Mapper 인터페이스 + XML 21개 (전 도메인 작성 완료)
- `DailyStatsScheduler` — 일별 통계 자동 집계

**프론트엔드**
- 전체 페이지 HTML 20개
- `common.css` — 사이드바, 레이아웃, 테이블, 배지, 모달, 페이지네이션, 스켈레톤 UI, 반응형
- `fragments/sidebar.html` — Thymeleaf 재사용 fragment
- 모바일 반응형 (사이드바 오버레이 토글)
- 스켈레톤 UI (대시보드 초기 로딩)
- 대시보드 날짜 네비게이션, 기간 탭, 서브 탭, 매출 expand — API 연동 완료
- 설정 페이지 (`/settings`) — 헬스장 정보, 실시간 오픈여부 토글, 요일별 운영시간 (`gym_setting` DB 연동)
- 회원 등급(tier) 관리 — 목록 배지, 필터, 상세 모달 수정
- OT/PT 유형 부여 — `user_profiles.member_type` 연동
- 회원 → 트레이너 지정 (`PUT /api/members/{id}/trainer`)

**인프라**
- `application-dev.yml` / `application-prod.yml` 환경 분리
- prod는 `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` 환경변수 사용

### ⏳ 미완료

| 항목 | 비고 |
|---|---|
| 메시지 시스템 재설계 | `message_conversation` + `chat_message` 기반으로 전환 필요 (현재 레거시 테이블 사용) |
| 이용권(티켓) 관리 UI | `member_tickets`, `ticket_logs`, `ticket_purchases` 연동 필요 |
| 원포인트 신청 승인·거절 | `onepoint_requests` 테이블 연동 필요 |
| 수업 일정 캘린더 뷰 | `trainer_schedules` 기반 전체 일정 뷰 미구현 |
| 회원 상세 모달 추가 정보 | `daily_note`, `exercise_note`, 의료이력 상세, PT 잔여 횟수 표시 |
| 출석 주별/월별 UI | API 파라미터는 준비됨, 프론트 미구현 |
| 유증(정지) 전용 UI | `freeze` API만 존재 |
| 로깅 (Logback) | 로그 레벨만 설정됨 |
| 테스트 코드 | 일부만 작성 (`DailyStatsSchedulerTest`, `MyBatisReRegistrationServiceTest`) |

---

## 실행 방법

```bash
# 빌드
./gradlew build -x test

# 실행
./gradlew bootRun

# 접속
http://localhost:17579
```

**Windows:**
```bat
gradlew.bat bootRun
```

---

## 디자인 시스템

라이트 테마 + 사이드바 다크 네이비. `common.css`에 CSS 변수로 정의됨.

| 변수 | 값 | 용도 |
|---|---|---|
| `--bg` | `#F6F8FA` | 페이지 배경 |
| `--surface` | `#FFFFFF` | 카드, 헤더 |
| `--surface-up` | `#F0F2F5` | 입력 필드, 버튼 배경 |
| `--accent` | `#0969DA` | 활성 탭, 링크, 포커스 |
| `--success` | `#1A7F37` | 유효 상태, 매출 |
| `--error` | `#CF222E` | 에러, 만기 상태, 삭제 |
| `--warning` | `#9A6700` | 경고, 정지 상태 |
| `--border` | `#D0D7DE` | 테두리 |
| `--text-primary` | `#1F2328` | 주요 텍스트 |
| `--text-secondary` | `#636C76` | 보조 텍스트 |
| `--text-muted` | `#818B98` | 비활성 텍스트 |
| 사이드바 배경 | `#1C2333` | 다크 네이비 |

---

## 주요 URL 목록

### 페이지

| URL | 설명 |
|---|---|
| `GET /login` | 로그인 페이지 |
| `GET /dashboard` | 대시보드 |
| `GET /members` | 회원 관리 |
| `GET /staff` | 직원 관리 |
| `GET /classes` | 수업 관리 |
| `GET /attendance` | 출석 관리 |
| `GET /consults` | 상담 관리 |
| `GET /revenue` | 매출 관리 |
| `GET /products` | 상품 관리 |
| `GET /messages` | 메시지 |
| `GET /pt` | PT 관리 |
| `GET /reregistration` | 재등록 관리 |
| `GET /feedback` | 피드백 관리 |
| `GET /inbox` | 받은 메시지함 (CRM) |
| `GET /cs` | CS 티켓 |
| `GET /crm-sales` | CRM 영업 현황 |
| `GET /announcements` | 공지사항 관리 |
| `GET /settings` | 헬스장 설정 |

### REST API

| 경로 | 컨트롤러 | 주요 기능 |
|---|---|---|
| `POST /api/auth/login` | AuthApiController | JWT 로그인 |
| `POST /api/auth/logout` | AuthApiController | 쿠키 삭제 로그아웃 |
| `/api/dashboard/**` | DashboardApiController | 통계 (members/classes/attendance/revenue/consults/crm-summary) |
| `/api/members/**` | MemberApiController | 회원 CRUD, 상태·등급·유형 변경, 티켓 조회·충전, 메모·태그, 트레이너 지정 |
| `/api/staff/**` | StaffApiController | 직원 CRUD, 역할 변경, 대시보드, 담당 회원 |
| `/api/classes/**` | ClassApiController | 수업 CRUD, 신청자 관리 |
| `/api/attendance/**` | AttendanceApiController | 출석 체크, 현황 조회, 유증 목록, 추이 |
| `/api/consults/**` | ConsultApiController | 상담 CRUD |
| `/api/products/**` | ProductApiController | 상품 CRUD |
| `/api/messages/**` | MessageApiController | 메시지 CRUD |
| `/api/revenue/**` | RevenueApiController | 매출 요약, 카테고리별 상세 |
| `/api/memberships/**` | MembershipApiController | 회원권 이력, 만료 예정, 액션 처리 |
| `/api/pt/**` | PtApiController | PT 회원 목록, 티켓 조회·수정 |
| `/api/reregistration/**` | ReRegistrationApiController | 재등록 목록, 상태·메모·담당자 변경, 자동 분류 |
| `/api/settings/gym` | SettingApiController | 헬스장 설정 조회·수정, 오픈 여부 토글 |
| `/api/stats/**` | StatsApiController | 일별 통계 조회, 수동 집계 |
| `/api/feedback/**` | FeedbackApiController | 피드백 요청·티켓 관리, 설정 |
| `/api/inbox/**` | CrmInboxApiController | CRM 받은 메시지 CRUD, 읽음 처리 |
| `/api/crm-sales/**` | CrmSalesApiController | CRM 매출 목록, 요약, 목표 관리, 내보내기 |
| `/api/cs/tickets/**` | CsTicketApiController | CS 티켓 관리, 담당자·상태·응답 처리 |
| `/api/announcements/**` | AnnouncementApiController | 공지사항 CRUD, 발송 처리 |
