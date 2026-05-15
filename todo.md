# LINK_Fit Admin — TODO

> 프로젝트 현황 분석 기반 작업 목록 (2025)

---

## 🔴 Critical (보안 / 필수)

- [ ] **인증 로직 교체** — `LoginController.java`의 하드코딩된 계정(`admin` / `admin123`)을 실제 인증 시스템으로 교체 (DB 조회, bcrypt 해싱 등)
- [ ] **Spring Security 도입** — 현재 Security 의존성 없음. `spring-boot-starter-security` 추가 및 세션/CSRF 보호 설정
- [ ] **인가(Authorization) 처리** — `/dashboard` 등 보호된 경로에 인증 없이 직접 접근 가능한 문제 해결
- [ ] **비밀번호 암호화** — 비밀번호 저장 및 검증 시 BCryptPasswordEncoder 적용

---

## 🟠 High (기능 구현)

### 회원 관리
- [ ] 회원 목록 페이지 (`/members`) 구현
- [ ] 회원 상세 조회 / 등록 / 수정 / 삭제 기능
- [ ] 회원권 상태 관리 (유효 / 만기 / 정지)
- [ ] 회원 검색 및 필터링

### 직원 관리
- [ ] 직원 목록 페이지 (`/staff`) 구현
- [ ] 직원 등록 / 수정 / 삭제
- [ ] 역할(Role) 구분 (슈퍼 관리자 / 일반 관리자 / 트레이너)

### 수업 관리
- [ ] 그룹 수업 / 개인 레슨 / OT 일정 관리
- [ ] 수업 신청자 목록 조회
- [ ] 수업 등록 / 수정 / 취소

### 출석 관리
- [ ] 출석 체크 기능 (`/attendance`)
- [ ] 일별 / 주별 / 월별 출석 현황 조회
- [ ] 유증(정지) 처리 기능

### 상담 관리
- [ ] 신규 상담 / 기존 회원 상담 등록 및 조회
- [ ] 상담 이력 관리

### 매출 관리
- [ ] 등록/재등록, 그룹 수업, 개인 레슨, 락커, 공동 물품 매출 상세 조회
- [ ] 매출 펼치기(expand) 기능 구현 (현재 버튼만 존재, 동작 없음)
- [ ] 기간별 매출 집계 (일간 / 주간 / 월간)

### 상품 관리
- [ ] 회원권 상품 등록 / 수정 / 삭제
- [ ] 수업 상품 관리
- [ ] 락커 / 공동 물품 관리

---

## 🟡 Medium (대시보드 개선)

- [ ] **날짜 네비게이션 동작 구현** — `<` `>` 버튼 클릭 시 날짜 변경 및 데이터 갱신 (현재 정적 표시)
- [ ] **기간 탭 데이터 연동** — 일간 / 주간 / 월간 탭 전환 시 실제 데이터 반영
- [ ] **서브 탭 데이터 연동** — 회원 통계, 수업 통계, 출석 통계의 서브 탭 필터링 동작 구현
- [ ] **실시간 데이터 연동** — 현재 대시보드의 모든 수치가 하드코딩됨; API 또는 서버사이드 데이터로 교체
- [ ] **메시지 기능** — 사이드바의 메시지 메뉴 페이지 구현

---

## 🟢 Low (개선 사항)

### 백엔드
- [ ] **데이터베이스 연동 — MyBatis + MariaDB**
  - `build.gradle` 의존성 추가
    ```groovy
    implementation 'org.mybatis.spring.boot:mybatis-spring-boot-starter:3.0.3'
    runtimeOnly 'org.mariadb.jdbc:mariadb-java-client'
    ```
  - `application.properties` DataSource 및 MyBatis 설정
    ```properties
    spring.datasource.url=jdbc:mariadb://localhost:3306/linkfit_admin
    spring.datasource.driver-class-name=org.mariadb.jdbc.Driver
    spring.datasource.username=
    spring.datasource.password=
    mybatis.mapper-locations=classpath:mapper/**/*.xml
    mybatis.type-aliases-package=com.linkfit.admin.domain
    mybatis.configuration.map-underscore-to-camel-case=true
    ```
  - `src/main/resources/mapper/` 디렉토리 및 XML Mapper 파일 구성
  - 각 도메인별 Mapper 인터페이스 + ServiceImpl 계층 구현
- [ ] **로그아웃 처리** — 현재 `/login` 리다이렉트만 존재; 세션 무효화 처리 추가
- [ ] **예외 처리** — 전역 예외 핸들러(`@ControllerAdvice`) 및 에러 페이지 구성
- [ ] **로깅** — 접근 로그, 오류 로그 설정

### 프론트엔드
- [ ] **반응형(Responsive) 지원** — 현재 사이드바 고정 너비(`220px`), 모바일 대응 미비
- [ ] **설정 페이지** — 사이드바의 설정 메뉴 구현
- [ ] **로딩 상태 표시** — 데이터 요청 중 스켈레톤 UI 또는 스피너 추가
- [ ] **Favicon 및 메타 태그** 설정

### 인프라 / 배포
- [ ] **환경 변수 분리** — DB 접속 정보 등 민감 정보를 `application-prod.properties` 또는 환경 변수로 분리
- [ ] **포트 설정 문서화** — 현재 `18080` 포트 사용 중 (README는 `8080` 기재 → 불일치 수정)
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
| 서버 포트 | `18080` (application.properties 기준) |
| 임시 계정 | `admin` / `admin123` |
| Spring Boot | `4.0.4` |
| Java | `21` |
| 빌드 도구 | Gradle `8.14` |
| DB | MariaDB + MyBatis |
