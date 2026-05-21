# LINK_Fit Admin — TODO

> 프로젝트 현황 분석 기반 작업 목록 (2025)

---

## 🔴 Critical (보안 / 필수)

- [x] **인증 로직 교체** — `SecurityConfig` + `InMemoryUserDetailsManager`로 교체 (BCrypt 해싱 적용)
- [x] **Spring Security 도입** — `spring-boot-starter-security` 추가, `SecurityConfig` 작성 (세션/CSRF 보호)
- [x] **인가(Authorization) 처리** — `SecurityFilterChain`으로 `/login`, 정적 리소스 외 모든 경로 인증 필요
- [x] **비밀번호 암호화** — `BCryptPasswordEncoder` 빈 등록 및 InMemory 계정에 적용

---

## 🟠 High (기능 구현)

### 회원 관리
- [x] 회원 목록 페이지 (`/members`) 구현
- [x] 회원 상세 조회 / 등록 / 수정 / 삭제 기능 (모달 UI + REST API)
- [x] 회원권 상태 관리 (유효 / 만기 / 정지) — 목록 필터 + PATCH `/api/members/{id}/status`
- [x] 회원 검색 및 필터링 — 이름/전화번호 검색, 상태 필터

### 직원 관리
- [x] 직원 목록 페이지 (`/staff`) 구현
- [x] 직원 등록 / 수정 / 삭제 (모달 UI + REST API)
- [x] 역할(Role) 구분 (슈퍼 관리자 / 일반 관리자 / 트레이너)

### 수업 관리
- [x] 그룹 수업 / 개인 레슨 / OT 일정 관리 — `/classes` 페이지, 유형 탭 필터
- [ ] 수업 신청자 목록 조회 — API 엔드포인트만 존재 (`GET /api/classes/{id}/attendees`), UI 미구현
- [x] 수업 등록 / 취소 (모달 UI + API) — 수정 UI는 미구현

### 출석 관리
- [x] 출석 체크 기능 (`/attendance`) — 페이지 + 체크인 모달 + POST API
- [x] 일별 출석 현황 조회 (날짜 필터 + 유형별 집계)
- [ ] 주별 / 월별 출석 현황 조회 — API 파라미터만 준비됨, UI 기간 탭 미구현
- [ ] 유증(정지) 처리 기능 — `POST /api/members/{id}/freeze` API만, 전용 UI 미구현

### 상담 관리
- [x] 신규 상담 / 기존 회원 상담 등록 및 조회 — `/consults` 페이지 + 모달 + API
- [x] 상담 이력 관리 — 목록 조회, 유형 필터, 삭제

### 매출 관리
- [x] 등록/재등록, 그룹 수업, 개인 레슨, 락커, 공동 물품 매출 상세 조회 — `/revenue` 페이지
- [x] 매출 펼치기(expand) 기능 구현 — 클릭 시 슬라이드 애니메이션 + API 상세 조회
- [x] 기간별 매출 집계 (일간 / 주간 / 월간) — 탭 전환 + 날짜 네비게이션 (`<` `>`)

### 상품 관리
- [x] 회원권 상품 등록 / 수정 / 삭제 — `/products` 페이지, 모달 UI + REST API
- [x] 수업 상품 관리 — 유형 필터(GROUP, PT)로 통합 관리
- [x] 락커 / 공동 물품 관리 — 유형 필터(LOCKER, ITEM)로 통합 관리

---

## 🟡 Medium (대시보드 개선)

- [x] **날짜 네비게이션 동작 구현** — `<` `>` 버튼 클릭 시 날짜 변경 및 API 재호출
- [x] **기간 탭 데이터 연동** — 일간 / 주간 / 월간 탭 전환 시 API 파라미터 변경 및 재호출
- [x] **서브 탭 데이터 연동** — 회원 통계(전체/회원권/그룹수업/개인레슨), 수업 통계, 출석 통계 서브 탭 동작
- [x] **실시간 데이터 연동** — 대시보드 전체 수치를 AJAX API 호출로 교체 (Mock 데이터 반환)
- [x] **메시지 기능** — `/messages` 페이지 구현 (목록 조회, 발송 모달, 삭제)

---

## 🔵 New (프론트엔드 개편 대응 — 어드민 패널)

> 앱 프론트엔드 개편 스펙 기반. 앱 백엔드 변경과 병행 필요.

### DB 스키마 변경 (앱 백엔드와 협의 후 실행)
- [ ] `users` 테이블에 `grade` 컬럼 추가 (`BASIC`|`MEMBERSHIP`|`PREMIUM`|`VIP`, default `BASIC`)
- [ ] `user_daily_habits` 테이블에 `daily_note` 컬럼 추가
- [ ] `user_medical_history` 테이블에 `adult_disease_detail`, `joint_detail`, `other_detail` 컬럼 추가
- [ ] `user_exercise_info` 테이블에 `exercise_note` 컬럼 추가

### 4-1. 회원 등급 관리
- [ ] `Member` 도메인 클래스에 `grade` 필드 추가
- [ ] `MemberMapper.xml` — `SELECT` 쿼리에 `u.grade` 컬럼 포함
- [ ] 회원 목록 페이지 — `grade` 배지 컬럼 표시 (BASIC / MEMBERSHIP / PREMIUM / VIP)
- [ ] 회원 목록 페이지 — `grade` 필터 드롭다운 추가
- [ ] 회원 상세 모달 — grade 표시 및 수동 조정 UI (관리자 전용)
- [ ] `PATCH /api/members/{id}/grade` 엔드포인트 구현

### 4-2. OT/PT 유형 부여 (관리자 전용)
- [ ] 회원 상세 페이지에 `memberType` 관리 UI 추가 (일반 / OT / PT 선택)
- [ ] `PATCH /api/members/{id}/member-type` 엔드포인트 구현
- [ ] `MemberMapper.xml` — `UPDATE user_profiles SET member_type` 쿼리 추가

### 4-3. 회원 프로필 조회 화면 업데이트
- [ ] 회원 상세 모달 — 섹션 순서 재정렬 (기본정보 → 방문경로 → 운동목적 → 운동계획 → 일상/식단 → 특이사항 → 기타)
- [ ] 회원 상세 모달 — 제거 항목 삭제 (운동종목, 운동기간, PT여부, PT만족도, 불만족사유)
- [ ] 회원 상세 모달 — 신규 필드 표시 (daily_note, exercise_note, adult_disease_detail, joint_detail)
- [ ] 의료 이력 표시: 성인병/관절 그룹 + 각 상세 텍스트 표시

---

## 🟢 Low (개선 사항)

### 백엔드
- [x] **데이터베이스 연동 준비 — MyBatis + MariaDB**
  - [x] `build.gradle` 의존성 추가 (`mybatis-spring-boot-starter:3.0.3`, `mariadb-java-client`)
  - [x] `application-dev.properties` DataSource 및 MyBatis 설정 (DB 접속 정보 입력 필요)
  - [x] `src/main/resources/mapper/` 디렉토리 및 XML Mapper 파일 구성 (MemberMapper.xml, StaffMapper.xml)
  - [x] `MemberMapper`, `StaffMapper` 인터페이스 작성, Mock 서비스 구현체 완성
  - [x] 나머지 도메인 Mapper 인터페이스 + XML 작성 (Class, Attendance, Consult, Product, Message, Sale, Dashboard, GymSetting)
  - [x] DB 연동 완료 — Mock → MyBatis 서비스 교체 (전 도메인), Spring Boot 4.x MyBatisConfig 수동 설정
- [x] **로그아웃 처리** — Spring Security logout 설정 (세션 무효화, JSESSIONID 쿠키 삭제)
- [x] **예외 처리** — `GlobalExceptionHandler`(@ControllerAdvice) + 404/500 에러 페이지
- [ ] **로깅** — 접근 로그, 오류 로그 설정 (`application-dev.properties`에 레벨만 설정, Logback 설정 미구현)

### 프론트엔드
- [x] **반응형(Responsive) 지원** — 모바일 사이드바 오버레이 토글, 그리드 레이아웃 단일 컬럼 전환
- [x] **설정 페이지** — 헬스장 정보, 실시간 오픈여부, 요일별 운영시간, 공지사항 설정 (`/settings`, `gym_setting` DB 연동)
- [x] **로딩 상태 표시** — 대시보드 스켈레톤 UI 추가 (`dashboard.css`에 shimmer 애니메이션)
- [x] **Favicon 및 메타 태그** 설정 — `favicon.svg` 생성, 모든 페이지에 적용

### 인프라 / 배포
- [x] **환경 변수 분리** — `application-dev.properties` / `application-prod.properties` 분리, prod는 환경변수(`${DB_URL}` 등) 사용
- [x] **포트 설정** — `17579` 포트 고정 (application.properties, README 반영 완료)
- [ ] **테스트 코드 작성** — `src/test` 디렉토리 존재하나 테스트 없음

---

## 📡 프론트엔드 API 명세

> Thymeleaf SSR 기반이므로 페이지 렌더링은 Controller → Model → Template 방식.
> 날짜/탭 전환 등 동적 갱신이 필요한 항목은 아래 REST 엔드포인트를 AJAX로 호출.
> `period` 공통 파라미터: `daily` / `weekly` / `monthly`

---

### 🖥️ 화면별 필요 API

#### 1. 대시보드 (`/dashboard`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 회원 통계 (전체 / 회원권 / 그룹수업 / 개인레슨) | GET | `/api/dashboard/members?date=&period=&type=` |
| 상담 통계 | GET | `/api/dashboard/consults?date=&period=` |
| 수업 통계 (그룹 / 개인레슨 / OT) | GET | `/api/dashboard/classes?date=&period=&type=` |
| 매출 통계 | GET | `/api/dashboard/revenue?date=&period=` |
| 매출 항목 상세 (펼치기) | GET | `/api/dashboard/revenue/{category}?date=&period=` |
| 출석 통계 (전체 / 회원권 / 그룹수업) | GET | `/api/dashboard/attendance?date=&period=&type=` |

---

#### 2. 회원 관리 (`/members`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 회원 목록 조회 (검색 / 상태 필터) | GET | `/api/members?page=&size=&status=&keyword=` |
| 회원 상세 조회 | GET | `/api/members/{id}` |
| 회원 등록 | POST | `/api/members` |
| 회원 수정 | PUT | `/api/members/{id}` |
| 회원 삭제 | DELETE | `/api/members/{id}` |
| 회원 상태 변경 (유효 / 만기 / 정지) | PATCH | `/api/members/{id}/status` |
| 유증(정지) 처리 | POST | `/api/members/{id}/freeze` |
| 회원권 목록 조회 | GET | `/api/members/{id}/memberships` |
| 회원권 등록 | POST | `/api/members/{id}/memberships` |

---

#### 3. 직원 관리 (`/staff`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 직원 목록 조회 | GET | `/api/staff?page=&size=&role=` |
| 직원 상세 조회 | GET | `/api/staff/{id}` |
| 직원 등록 | POST | `/api/staff` |
| 직원 수정 | PUT | `/api/staff/{id}` |
| 직원 삭제 | DELETE | `/api/staff/{id}` |
| 직원 역할 변경 | PATCH | `/api/staff/{id}/role` |

---

#### 4. 수업 관리 (`/classes`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 수업 목록 조회 (그룹 / 개인레슨 / OT) | GET | `/api/classes?type=&date=&page=&size=` |
| 수업 상세 조회 | GET | `/api/classes/{id}` |
| 수업 등록 | POST | `/api/classes` |
| 수업 수정 | PUT | `/api/classes/{id}` |
| 수업 취소 | DELETE | `/api/classes/{id}` |
| 수업 신청자 목록 | GET | `/api/classes/{id}/attendees` |
| 수업 신청 | POST | `/api/classes/{id}/attendees` |
| 수업 신청 취소 | DELETE | `/api/classes/{id}/attendees/{memberId}` |

---

#### 5. 출석 관리 (`/attendance`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 출석 현황 조회 | GET | `/api/attendance?date=&period=` |
| 출석 체크 | POST | `/api/attendance` |
| 출석 취소 | DELETE | `/api/attendance/{id}` |
| 유증 회원 목록 | GET | `/api/attendance/freeze?date=` |

---

#### 6. 상담 관리 (`/consults`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 상담 목록 조회 | GET | `/api/consults?page=&size=&type=` |
| 상담 상세 조회 | GET | `/api/consults/{id}` |
| 신규 상담 등록 | POST | `/api/consults` |
| 기존 회원 상담 등록 | POST | `/api/consults/existing` |
| 상담 수정 | PUT | `/api/consults/{id}` |
| 상담 삭제 | DELETE | `/api/consults/{id}` |

---

#### 7. 매출 관리 (`/revenue`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 매출 요약 조회 | GET | `/api/revenue/summary?date=&period=` |
| 등록/재등록 매출 상세 | GET | `/api/revenue/memberships?date=&period=` |
| 그룹 수업 매출 상세 | GET | `/api/revenue/group-classes?date=&period=` |
| 개인 레슨 매출 상세 | GET | `/api/revenue/pt?date=&period=` |
| 락커 매출 상세 | GET | `/api/revenue/lockers?date=&period=` |
| 공동 물품 매출 상세 | GET | `/api/revenue/items?date=&period=` |

---

#### 8. 상품 관리 (`/products`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 상품 목록 조회 | GET | `/api/products?type=&page=&size=` |
| 상품 상세 조회 | GET | `/api/products/{id}` |
| 상품 등록 | POST | `/api/products` |
| 상품 수정 | PUT | `/api/products/{id}` |
| 상품 삭제 | DELETE | `/api/products/{id}` |

---

#### 9. 인증

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 로그인 페이지 | GET | `/login` |
| 로그인 처리 | POST | `/login` |
| 로그아웃 | POST | `/logout` |

---

#### 10. 메시지 (`/messages`)

| 목적 | Method | 엔드포인트 |
|---|---|---|
| 메시지 목록 조회 | GET | `/api/messages?page=&size=` |
| 메시지 발송 | POST | `/api/messages` |
| 메시지 상세 조회 | GET | `/api/messages/{id}` |
| 메시지 삭제 | DELETE | `/api/messages/{id}` |

---

### 📋 전체 엔드포인트 표

| Method | URL | 설명 | 화면 |
|---|---|---|---|
| GET | `/login` | 로그인 페이지 | 로그인 |
| POST | `/login` | 로그인 처리 | 로그인 |
| POST | `/logout` | 로그아웃 | 공통 |
| GET | `/dashboard` | 대시보드 페이지 | 대시보드 |
| GET | `/api/dashboard/members` | 회원 통계 | 대시보드 |
| GET | `/api/dashboard/consults` | 상담 통계 | 대시보드 |
| GET | `/api/dashboard/classes` | 수업 통계 | 대시보드 |
| GET | `/api/dashboard/revenue` | 매출 통계 | 대시보드 |
| GET | `/api/dashboard/revenue/{category}` | 매출 항목 상세 | 대시보드 |
| GET | `/api/dashboard/attendance` | 출석 통계 | 대시보드 |
| GET | `/members` | 회원 목록 페이지 | 회원 |
| GET | `/api/members` | 회원 목록 조회 | 회원 |
| GET | `/api/members/{id}` | 회원 상세 | 회원 |
| POST | `/api/members` | 회원 등록 | 회원 |
| PUT | `/api/members/{id}` | 회원 수정 | 회원 |
| DELETE | `/api/members/{id}` | 회원 삭제 | 회원 |
| PATCH | `/api/members/{id}/status` | 회원 상태 변경 | 회원 |
| POST | `/api/members/{id}/freeze` | 유증 처리 | 회원 |
| GET | `/api/members/{id}/memberships` | 회원권 목록 | 회원 |
| POST | `/api/members/{id}/memberships` | 회원권 등록 | 회원 |
| GET | `/staff` | 직원 목록 페이지 | 직원 |
| GET | `/api/staff` | 직원 목록 조회 | 직원 |
| GET | `/api/staff/{id}` | 직원 상세 | 직원 |
| POST | `/api/staff` | 직원 등록 | 직원 |
| PUT | `/api/staff/{id}` | 직원 수정 | 직원 |
| DELETE | `/api/staff/{id}` | 직원 삭제 | 직원 |
| PATCH | `/api/staff/{id}/role` | 직원 역할 변경 | 직원 |
| GET | `/classes` | 수업 목록 페이지 | 수업 |
| GET | `/api/classes` | 수업 목록 조회 | 수업 |
| GET | `/api/classes/{id}` | 수업 상세 | 수업 |
| POST | `/api/classes` | 수업 등록 | 수업 |
| PUT | `/api/classes/{id}` | 수업 수정 | 수업 |
| DELETE | `/api/classes/{id}` | 수업 취소 | 수업 |
| GET | `/api/classes/{id}/attendees` | 수업 신청자 목록 | 수업 |
| POST | `/api/classes/{id}/attendees` | 수업 신청 | 수업 |
| DELETE | `/api/classes/{id}/attendees/{memberId}` | 수업 신청 취소 | 수업 |
| GET | `/attendance` | 출석 현황 페이지 | 출석 |
| GET | `/api/attendance` | 출석 현황 조회 | 출석 |
| POST | `/api/attendance` | 출석 체크 | 출석 |
| DELETE | `/api/attendance/{id}` | 출석 취소 | 출석 |
| GET | `/api/attendance/freeze` | 유증 회원 목록 | 출석 |
| GET | `/consults` | 상담 목록 페이지 | 상담 |
| GET | `/api/consults` | 상담 목록 조회 | 상담 |
| GET | `/api/consults/{id}` | 상담 상세 | 상담 |
| POST | `/api/consults` | 신규 상담 등록 | 상담 |
| POST | `/api/consults/existing` | 기존 회원 상담 등록 | 상담 |
| PUT | `/api/consults/{id}` | 상담 수정 | 상담 |
| DELETE | `/api/consults/{id}` | 상담 삭제 | 상담 |
| GET | `/revenue` | 매출 현황 페이지 | 매출 |
| GET | `/api/revenue/summary` | 매출 요약 | 매출 |
| GET | `/api/revenue/memberships` | 등록/재등록 매출 상세 | 매출 |
| GET | `/api/revenue/group-classes` | 그룹 수업 매출 상세 | 매출 |
| GET | `/api/revenue/pt` | 개인 레슨 매출 상세 | 매출 |
| GET | `/api/revenue/lockers` | 락커 매출 상세 | 매출 |
| GET | `/api/revenue/items` | 공동 물품 매출 상세 | 매출 |
| GET | `/products` | 상품 목록 페이지 | 상품 |
| GET | `/api/products` | 상품 목록 조회 | 상품 |
| GET | `/api/products/{id}` | 상품 상세 | 상품 |
| POST | `/api/products` | 상품 등록 | 상품 |
| PUT | `/api/products/{id}` | 상품 수정 | 상품 |
| DELETE | `/api/products/{id}` | 상품 삭제 | 상품 |
| GET | `/messages` | 메시지 페이지 | 메시지 |
| GET | `/api/messages` | 메시지 목록 조회 | 메시지 |
| POST | `/api/messages` | 메시지 발송 | 메시지 |
| GET | `/api/messages/{id}` | 메시지 상세 | 메시지 |
| DELETE | `/api/messages/{id}` | 메시지 삭제 | 메시지 |

---

## 📌 참고 사항

| 항목 | 현황 |
|---|---|
| 서버 포트 | `17579` |
| 어드민 계정 | `admin` / `admin1234` (DB 기반 UserDetailsService, BCrypt 해싱) |
| Spring Boot | `4.0.4` |
| Java | `21` |
| 빌드 도구 | Gradle `8.14` |
| DB | MariaDB 10.11.14 + MyBatis 3.0.3 (전 도메인 DB 연동 완료) |
| 인증 | Spring Security 7.x (Form Login, CSRF 보호) |
