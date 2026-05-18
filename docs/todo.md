# LINK_Fit Admin — TODO

> `docs/srs-dev.md` 기반 작업 목록  
> 백엔드 API: `localhost:17577/api` | 인증: `ADMIN` role JWT

---

## ✅ 완료 — Spring Boot 서버 Mock 모드 전환 (DB 없이 실행 가능)

> 빌드: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew build -x test`  
> 실행: `JAVA_HOME=/usr/lib/jvm/java-21-openjdk-amd64 ./gradlew bootRun`  
> 접속: `http://localhost:17579` | 계정: `admin` / `admin123`

- [x] `application.properties` — `spring.profiles.active=dev` 주석 처리
- [x] `MyBatisConfig` — `@Profile("dev")` 추가 (DB 없을 때 MyBatis 설정 스킵)
- [x] `AdminUserDetailsService` — `@Profile("dev")` 추가
- [x] `SecurityConfig` — `AdminUserDetailsService` optional 처리, InMemory 폴백 추가 (`admin`/`admin123`)
- [x] `MyBatis*Service` 8개 — `@Profile("dev")` 추가 (DB 없을 때 비활성)
- [x] `Mock*Service` 7개 — `@Service` 복구 (DB 없이 더미 데이터로 동작)

> DB 연동 시: `application.properties`에서 `spring.profiles.active=dev` 주석 해제

---

## Sprint 1 — 프로젝트 셋업 + 인증 + 라우트 보호

### A1. 관리자 인증

- [ ] `POST /auth/login` 호출 후 `role == 'ADMIN'` 검증 → 대시보드 진입
- [ ] `role != 'ADMIN'` 이면 접근 거부 처리
- [ ] JWT `localStorage` 저장 (또는 httpOnly 쿠키 — 백엔드 협의)
- [ ] `ApiClient` — axios 기반, JWT 헤더 자동 주입
- [ ] 라우트 보호 (인증되지 않으면 `/admin/login`으로 redirect)

```
/admin/login               로그인 페이지
/admin/dashboard           전체 현황 요약
/admin/members             회원 목록
/admin/members/:id         회원 상세·등급 설정
/admin/trainers            트레이너 목록
/admin/trainers/:id        트레이너 상세·토큰 지급
/admin/exercises           운동 종목 관리
/admin/gym                 헬스장 설정
/admin/feedback            피드백 요청 현황
/admin/payments            결제 내역 (Phase 2)
```

### A8. 공통 컴포넌트

- [ ] `AdminLayout` — 사이드바 + 헤더 + 콘텐츠 영역
- [ ] `DataTable` — 정렬·검색·페이징 공통 테이블
- [ ] `TierBadge` — FREE / BASIC / PREMIUM / VIP 색상 뱃지
- [ ] `ConfirmModal` — 삭제·등급변경 등 2차 확인 모달

---

## Sprint 1 — 전체 현황 대시보드

### A2. 대시보드 (`/admin/dashboard`)

```
GET /admin/dashboard/summary
Response: {
  "totalMembers": 142,
  "activeTrainers": 8,
  "todayRecords": 37,
  "pendingFeedbacks": 12,
  "weeklyRecords": [5, 12, 8, 20, 15, 37, 10],
  "tierDistribution": { "FREE": 80, "BASIC": 30, "PREMIUM": 25, "VIP": 7 }
}
```

- [ ] 요약 카드 4종 (전체 회원 / 활성 트레이너 / 오늘 기록 수 / 미처리 피드백)
- [ ] 주간 기록 수 차트 (최근 7일 bar/line)
- [ ] 등급별 회원 비율 파이 차트 (FREE / BASIC / PREMIUM / VIP)

---

## Sprint 2 — 회원 관리

### A3. 회원 목록 (`/admin/members`)

```
GET  /admin/members?keyword=&tier=&page=&size=
PUT  /admin/members/{userId}/membership     Body: { "tier": "PREMIUM" }
PUT  /admin/members/{userId}/pt-sessions    Body: { "delta": 5 }
PUT  /admin/members/{userId}/trainer        Body: { "trainerId": 3 }
```

- [ ] 회원 목록 테이블 (이름 검색 / tier 필터 / 페이징)
- [ ] tier 뱃지 클릭 → tier 변경 (`PUT /admin/members/{userId}/membership`)
- [ ] PT 잔여 직접 입력·차감 (`PUT /admin/members/{userId}/pt-sessions`)
- [ ] 담당 트레이너 드롭다운 변경 (`PUT /admin/members/{userId}/trainer`)

### A3. 회원 상세 (`/admin/members/:id`)

- [ ] 운동·식단 기록 캘린더 뷰 (트레이너 `MemberDetailPage`와 동일 데이터)
- [ ] 등급 변경 인풋
- [ ] PT 세션 직접 입력 인풋

---

## Sprint 2 — 운동 종목 관리

### A4. 운동 종목 (`/admin/exercises`)

```
GET    /exercises                              기존 재사용
POST   /admin/exercises                        Body: { name, category, imageUrl }
PUT    /admin/exercises/{exerciseId}
DELETE /admin/exercises/{exerciseId}
POST   /admin/exercises/image                  multipart → { "imageUrl": "..." }
```

- [ ] 카테고리 필터 탭 (상단)
- [ ] 종목 카드 리스트 (이름 / 카테고리 / 썸네일 / [수정] [삭제])
- [ ] [+ 종목 추가] 버튼
- [ ] 종목 추가·수정 모달 (종목명 / 카테고리 드롭다운 / 이미지 파일 업로드)
- [ ] 이미지 업로드 → S3 or 서버 URL 저장 (백엔드 협의 필요 — 미결 #6)

---

## Sprint 3 — 헬스장 설정

### A5. 헬스장 설정 (`/admin/gym`)

```
GET /gym/setting       기존 재사용
PUT /admin/gym/setting Body: { gymName, gymAddress, isOpen, todayClosed, todayOpen, todayClose }
```

- [ ] 헬스장 이름 / 주소 입력
- [ ] 정상 운영 여부 toggle (`isOpen`)
- [ ] 오늘 휴무 toggle (`todayClosed`)
- [ ] 오늘 운영 시작·종료 시간 picker (`todayOpen`, `todayClose`)

---

## Sprint 3 — 트레이너·토큰 관리

### A6. 트레이너 목록 (`/admin/trainers`)

```
GET  /admin/trainers
POST /admin/trainers/{trainerId}/tokens       Body: { "amount": 100 }
GET  /trainer/tokens                          트레이너 앱 연동용
POST /admin/trainers/{trainerId}/members      Body: { "userId": 42 }
DELETE /admin/trainers/{trainerId}/members/{userId}
```

- [ ] 트레이너 목록 테이블 (이름 / 이메일 / 토큰 잔액 / 담당 회원 수)
- [ ] [토큰 지급] 버튼 → 수량 입력 모달 (`POST /admin/trainers/{trainerId}/tokens`)
- [ ] 트레이너 상세에서 회원 검색 후 배정 (`POST /admin/trainers/{trainerId}/members`)
- [ ] 담당 회원 해제 (`DELETE /admin/trainers/{trainerId}/members/{userId}`)
- [ ] `GET /trainer/tokens` — 트레이너 앱에서 실제 잔액 조회 연동 확인

---

## Sprint 4 — 피드백 요청 현황

### A7. 피드백 요청 (`/admin/feedback`)

```
GET /admin/feedback-requests?status=PENDING&page=&size=
Response: { "data": [{ requestId, memberName, trainerName, exerciseNames, requestedAt, status, respondedAt }] }
```

- [ ] 피드백 요청 목록 테이블 (회원명 / 담당 트레이너 / 요청 종목 / 요청일 / 상태 / 처리일)
- [ ] 상태 필터 (미처리 PENDING / 처리완료 RESPONDED)
- [ ] 페이징

---

## 📌 미결 사항 (백엔드 협의 필요)

| # | 항목 | 대상 |
|---|---|---|
| 1 | 결제 플로우: 1,900원 단품 PG 연동 방식 | link-fit-admin + 백엔드 |
| 2 | Membership tier 변경 후 앱 캐시 만료 정책 | linkfit + 백엔드 |
| 3 | 트레이너 FCM 알림: 피드백 요청 수신 토픽 설계 | linkfit + 백엔드 |
| 4 | 3D 근육 맵 — MVP 제외, Phase 2 별도 검토 | linkfit |
| 5 | 칼로리 계산 — MET 단순 추정, Phase 2에서 종목별 정밀 계산 | linkfit + 백엔드 |
| 6 | 이미지 스토리지 — S3 버킷 or 서버 로컬 경로 결정 | link-fit-admin + 백엔드 |
| 7 | ADMIN role 계정 생성 방식 — DB 직접 삽입 or 별도 API | 백엔드 |
| 8 | 관리자 웹 배포 환경 — Nginx / Vercel / S3+CloudFront | link-fit-admin + 인프라 |

---

## 📋 참고

| 항목 | 값 |
|---|---|
| 백엔드 API | `localhost:17577/api` |
| 인증 | JWT, `role == 'ADMIN'` |
| 권장 스택 | React + TypeScript + Vite / Next.js |
| 구현 순서 | Sprint 1 → 2 → 3 → 4 순서 권장 (A9 참고) |
