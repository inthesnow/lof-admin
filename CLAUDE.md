# LINK_Fit Admin — CLAUDE.md

헬스장 관리자 웹 어드민 프로젝트. LINK_Fit 앱의 백오피스로, 회원/직원/수업/출석/상담/매출/상품/메시지를 통합 관리한다.

---

## 기술 스택

| 항목 | 내용 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.4 |
| Security | Spring Security 7.x |
| Template | Thymeleaf (SSR) |
| Persistence | MyBatis + MariaDB (현재 Mock 서비스 사용 중) |
| Build | Gradle 8.14 |
| Port | **18080** (README의 8080은 오기) |
| 임시 계정 | `admin` / `admin123` |

---

## 프로젝트 구조

```
src/main/java/com/linkfit/admin/
├── LinkFitAdminApplication.java
├── common/
│   └── ApiResponse.java              ← 공통 REST 응답 래퍼 record
├── config/
│   └── SecurityConfig.java           ← Spring Security 설정 (form login, CSRF, 인가)
├── controller/
│   ├── LoginController.java          ← GET /login, GET /dashboard (페이지 반환만)
│   ├── PageController.java           ← 나머지 메뉴 페이지 GetMapping 모음
│   └── api/                          ← @RestController (JSON 반환)
│       ├── DashboardApiController.java
│       ├── MemberApiController.java
│       ├── StaffApiController.java
│       ├── ClassApiController.java
│       ├── AttendanceApiController.java
│       ├── ConsultApiController.java
│       ├── ProductApiController.java
│       ├── MessageApiController.java
│       └── RevenueApiController.java  ← 아직 미생성 (todo)
├── domain/                           ← VO/DTO (getter/setter 방식)
│   ├── Member.java
│   ├── Staff.java
│   ├── ClassSession.java
│   ├── Attendance.java
│   ├── Consult.java
│   ├── Product.java
│   └── Message.java
├── service/                          ← 인터페이스
│   ├── MemberService.java
│   ├── StaffService.java
│   ├── ClassService.java
│   ├── AttendanceService.java
│   ├── ConsultService.java
│   ├── ProductService.java
│   └── MessageService.java
├── service/mock/                     ← 인메모리 더미 구현체 (현재 사용 중)
│   ├── MockMemberService.java        ← DB 연동 시 MyBatisMemberService로 교체 예정
│   └── ...
├── mapper/                           ← MyBatis @Mapper 인터페이스 (DB 연동 후 활성화)
│   ├── MemberMapper.java
│   └── StaffMapper.java
└── exception/
    └── GlobalExceptionHandler.java   ← @ControllerAdvice, 404/500 처리

src/main/resources/
├── application.properties            ← server.port=18080
├── application-dev.properties        ← 로컬 DB 접속 정보 (직접 입력 필요)
├── application-prod.properties       ← 환경변수 ${DB_URL} 방식
├── mapper/
│   ├── MemberMapper.xml              ← MyBatis SQL (골격 작성됨)
│   └── StaffMapper.xml
├── static/
│   ├── favicon.svg
│   └── css/
│       ├── common.css                ← 사이드바/레이아웃/테이블/모달 공통 스타일
│       ├── dashboard.css             ← 대시보드 전용 + 스켈레톤 UI
│       └── login.css                 ← 로그인 전용
└── templates/
    ├── fragments/
    │   └── sidebar.html              ← Thymeleaf 재사용 사이드바 (th:replace로 삽입)
    ├── login.html
    ├── dashboard.html
    ├── members.html
    ├── staff.html
    ├── classes.html
    ├── attendance.html
    ├── consults.html
    ├── revenue.html
    ├── products.html
    ├── messages.html
    └── error/
        ├── 404.html
        └── 500.html
```

---

## 아키텍처 패턴

### 서비스 계층 교체 전략
DB 연동 전까지 Mock 서비스가 `@Service`로 빈 등록되어 동작한다.
DB 연동 시 Mock 클래스를 제거하고 MyBatis 구현체를 작성한 뒤 같은 인터페이스를 구현하면 된다.

```java
// 현재: Mock 서비스가 빈으로 등록됨
@Service
public class MockMemberService implements MemberService { ... }

// DB 연동 후: Mock 제거하고 교체
@Service
public class MyBatisMemberService implements MemberService {
    private final MemberMapper memberMapper;
    ...
}
```

### REST API 응답 형식
모든 API는 `ApiResponse<T>` record를 사용한다.

```java
// 성공
ApiResponse.ok(data)       // { success: true, message: "ok", data: ... }
ApiResponse.ok()           // data: null

// 실패
ApiResponse.error("메시지") // { success: false, message: "...", data: null }
```

### 사이드바 fragment 사용법
모든 페이지는 `fragments/sidebar.html`을 `th:replace`로 삽입한다.
`activePage` 파라미터로 현재 메뉴를 하이라이트한다.

```html
<aside th:replace="~{fragments/sidebar :: sidebar('members')}"></aside>
```

activePage 값: `dashboard` / `members` / `staff` / `classes` / `attendance` / `consults` / `revenue` / `products` / `messages`

### 프론트엔드 데이터 흐름
페이지 렌더링은 Controller → Thymeleaf SSR.
동적 데이터(날짜 변경, 탭 전환, 테이블 갱신 등)는 각 페이지의 인라인 JS에서 `fetch()`로 REST API를 호출한다.

---

## 개발 방향성

### 1단계 (완료) — 골격 구축
- Spring Security 인증/인가 설정
- 전체 페이지 UI (8개 메뉴 HTML/CSS)
- Mock 서비스 기반 REST API 동작 확인
- 대시보드 동적 기능 (날짜 네비게이션, 탭, expand)

### 2단계 (진행 예정) — DB 연동
1. `application-dev.properties`에 MariaDB 접속 정보 입력
2. `application.properties`에 `spring.profiles.active=dev` 추가
3. DB 스키마 생성 (member, staff, class_session, attendance, consult, product, message 테이블)
4. 나머지 Mapper 인터페이스 + XML 작성 (Class, Attendance, Consult, Product, Message)
5. Mock 서비스를 MyBatis 서비스로 순차 교체
6. Spring Security `UserDetailsService` 구현 → DB 계정 조회로 교체

### 3단계 (추후) — 기능 고도화
- 수업 신청자 목록 UI
- 유증(정지) 전용 UI
- 출석 주별/월별 집계 UI
- 수업 수정 기능
- 설정 페이지
- 테스트 코드 작성

---

## 개발 진행 현황

### ✅ 완료

**보안**
- Spring Security 7.x 설정 (`SecurityConfig`)
- BCryptPasswordEncoder 적용, InMemoryUserDetailsManager로 임시 계정 운용
- 모든 경로 인증 필요, `/login` · 정적 리소스만 허용
- 로그아웃 세션 무효화, JSESSIONID 쿠키 삭제

**백엔드**
- `ApiResponse<T>` 공통 응답 포맷
- `GlobalExceptionHandler` — 404/500 에러 페이지 반환 또는 JSON 응답
- `PageController` — 8개 메뉴 페이지 GetMapping
- REST API Controller 8개 — 모든 엔드포인트 골격 완성
- 도메인 클래스 7개 (Member, Staff, ClassSession, Attendance, Consult, Product, Message)
- 서비스 인터페이스 7개 + Mock 구현체 7개 (더미 데이터로 즉시 동작)
- MyBatis 의존성 추가, MemberMapper/StaffMapper 인터페이스 + XML 골격

**프론트엔드**
- `common.css` — 사이드바, 레이아웃, 테이블, 배지, 모달, 페이지네이션, 스켈레톤 UI, 반응형
- `fragments/sidebar.html` — Thymeleaf 재사용 fragment, activePage로 활성 탭 표시
- 8개 메뉴 페이지 HTML (회원/직원/수업/출석/상담/매출/상품/메시지)
- 404/500 에러 페이지
- favicon.svg, 모든 페이지 적용
- 모바일 반응형 (사이드바 오버레이 토글)
- 스켈레톤 UI (대시보드 초기 로딩)
- 대시보드 날짜 네비게이션(`<` `>`), 기간 탭, 서브 탭, 매출 expand — 모두 API 연동

**인프라**
- `application-dev.properties` / `application-prod.properties` 환경 분리
- prod는 `${DB_URL}`, `${DB_USERNAME}`, `${DB_PASSWORD}` 환경변수 사용

### ⏳ 미완료

| 항목 | 비고 |
|---|---|
| DB 스키마 및 MyBatis 연동 | Mock → MyBatis 교체 필요 |
| Class/Attendance/Consult/Product/Message Mapper XML | 골격 미작성 |
| 수업 신청자 목록 UI | API 엔드포인트만 존재 |
| 수업 수정 UI | 등록/취소만 구현 |
| 유증(정지) 전용 UI | freeze API만 존재 |
| 출석 주별/월별 집계 UI | API 파라미터만 준비 |
| DB 기반 로그인 (`UserDetailsService`) | InMemory 임시 운용 중 |
| 설정 페이지 | 미구현 |
| 로깅 설정 (Logback) | 로그 레벨만 설정됨 |
| 테스트 코드 | `src/test` 비어 있음 |

---

## 실행 방법

```bash
# 빌드 (gradlew CRLF 이슈 시 아래 명령 먼저 실행)
sed -i 's/\r//' gradlew

# 빌드
./gradlew build -x test

# 실행
./gradlew bootRun

# 접속
http://localhost:18080
```

**Windows:**
```bat
gradlew.bat bootRun
```

---

## 디자인 시스템

LINK_Fit 앱과 동일한 GitHub 스타일 다크 테마.
`common.css`와 `dashboard.css`에 CSS 변수로 정의됨.

| 변수 | 값 | 용도 |
|---|---|---|
| `--bg` | `#0D1117` | 페이지 배경 |
| `--surface` | `#161B22` | 카드, 사이드바, 헤더 |
| `--surface-up` | `#21262D` | 입력 필드, 버튼 배경 |
| `--accent` | `#58A6FF` | 활성 탭, 링크, 포커스 |
| `--success` | `#3FB950` | 유효 상태, 매출 |
| `--error` | `#F85149` | 에러, 만기 상태, 삭제 |
| `--warning` | `#D29922` | 경고, 정지 상태 |
| `--border` | `#30363D` | 테두리 |
| `--text-primary` | `#F0F6FC` | 주요 텍스트 |
| `--text-secondary` | `#8B949E` | 보조 텍스트 |
| `--text-muted` | `#6E7681` | 비활성 텍스트 |

---

## 주요 URL 목록

| URL | 설명 |
|---|---|
| `GET /login` | 로그인 페이지 |
| `POST /login` | Spring Security 로그인 처리 |
| `POST /logout` | 로그아웃 (세션 무효화) |
| `GET /dashboard` | 대시보드 |
| `GET /members` | 회원 관리 |
| `GET /staff` | 직원 관리 |
| `GET /classes` | 수업 관리 |
| `GET /attendance` | 출석 관리 |
| `GET /consults` | 상담 관리 |
| `GET /revenue` | 매출 관리 |
| `GET /products` | 상품 관리 |
| `GET /messages` | 메시지 |
| `GET /api/**` | REST API (JSON 반환, 인증 필요) |
