# link-fit-admin — 관리자 웹 개발 명세

> **기술 스택**: 별도 프로젝트 (`link-fit-admin`)  
> **권장 스택**: React + TypeScript + Vite / Next.js 중 선택  
> **공통 API**: `localhost:17577/api` (동일 백엔드)  
> **인증**: `ADMIN` role JWT — 로그인 시 `role: "ADMIN"` 반환 전제

---

## A1. 관리자 인증

### A1.1 로그인

- **엔드포인트**: 기존 `POST /auth/login` 재사용
- `role == 'ADMIN'`이면 관리자 대시보드로 진입, 아니면 접근 거부
- 세션: `localStorage`에 JWT 저장 (또는 httpOnly 쿠키 — 백엔드 협의)

### A1.2 라우트 보호

```
/admin/login               → 로그인 페이지
/admin/dashboard           → 전체 현황 요약
/admin/members             → 회원 목록
/admin/members/:id         → 회원 상세·등급 설정
/admin/trainers            → 트레이너 목록
/admin/trainers/:id        → 트레이너 상세·토큰 지급
/admin/exercises           → 운동 종목 관리
/admin/gym                 → 헬스장 설정
/admin/feedback            → 피드백 요청 현황
/admin/payments            → 결제 내역 (Phase 2)
```

---

## A2. 전체 현황 대시보드 (`/admin/dashboard`)

### A2.1 UI 구성

```
┌─────────────┬─────────────┬─────────────┬─────────────┐
│ 전체 회원   │ 활성 트레이너│ 오늘 기록 수 │ 미처리 피드백│
│    142명    │    8명      │    37건      │    12건     │
└─────────────┴─────────────┴─────────────┴─────────────┘

[주간 기록 수 차트]        [등급별 회원 비율 파이차트]
```

### A2.2 필요 API

```
GET /admin/dashboard/summary
Response: {
  "totalMembers": 142,
  "activeTrainers": 8,
  "todayRecords": 37,
  "pendingFeedbacks": 12,
  "weeklyRecords": [5, 12, 8, 20, 15, 37, 10],  // 최근 7일
  "tierDistribution": {
    "FREE": 80, "BASIC": 30, "PREMIUM": 25, "VIP": 7
  }
}
```

---

## A3. 회원 관리 (`/admin/members`)

### A3.1 목록 화면

| 컬럼 | 내용 |
|---|---|
| 이름 | 검색 가능 |
| 이메일 | - |
| 등급 (tier) | 뱃지 표시, 클릭으로 변경 |
| PT 잔여 | 직접 입력·차감 |
| 담당 트레이너 | 드롭다운으로 변경 |
| 가입일 | - |

### A3.2 필요 API

```
# 전체 회원 목록 (ADMIN)
GET /admin/members?keyword=&tier=&page=&size=
Response: { "data": [...], "total": 142 }

# 회원 등급 변경
PUT /admin/members/{userId}/membership
Body: { "tier": "PREMIUM" }

# PT 세션 조정
PUT /admin/members/{userId}/pt-sessions
Body: { "delta": 5 }          // +5 추가, -3이면 차감
Response: { "ptSessionsLeft": 17 }

# 담당 트레이너 변경
PUT /admin/members/{userId}/trainer
Body: { "trainerId": 3 }
```

### A3.3 회원 상세 (`/admin/members/:id`)

- linkfit의 `MemberProfilePage`와 동일한 구조로 웹에서 조회
- 운동·식단 기록 캘린더 뷰 (트레이너의 `MemberDetailPage`와 동일 데이터)
- 등급 변경, PT 세션 직접 입력 인풋

---

## A4. 운동 종목 관리 (`/admin/exercises`)

### A4.1 현황

linkfit Flutter 앱에서 `GET /exercises` 로 목록을 조회하고 있다.  
현재 종목 추가·수정·삭제는 관리자 웹에서만 수행한다.

### A4.2 목록 화면

- 카테고리 필터 탭 (상단)
- 종목 카드 리스트: 이름 / 카테고리 / 썸네일 / [수정] [삭제]
- [+ 종목 추가] 버튼

### A4.3 종목 추가·수정 모달

| 필드 | 타입 |
|---|---|
| 종목명 | 텍스트 |
| 카테고리 | 드롭다운 (기존 카테고리 + 직접 입력) |
| 이미지 | 파일 업로드 → S3/서버 업로드 후 URL 저장 |

### A4.4 필요 API

```
# 종목 목록 (기존 재사용)
GET /exercises

# 종목 생성 (ADMIN)
POST /admin/exercises
Body: { "name": "케이블 로우", "category": "등", "imageUrl": "https://..." }

# 종목 수정 (ADMIN)
PUT /admin/exercises/{exerciseId}
Body: { "name": "...", "category": "...", "imageUrl": "..." }

# 종목 삭제 (ADMIN)
DELETE /admin/exercises/{exerciseId}

# 이미지 업로드 (ADMIN)
POST /admin/exercises/image
Content-Type: multipart/form-data
Response: { "imageUrl": "https://..." }
```

---

## A5. 헬스장 설정 (`/admin/gym`)

### A5.1 현황

[gym_setting_service.dart](lib/services/gym_setting_service.dart)에서 `GET /gym/setting`으로 읽기만 하고 있다.  
관리자 웹에서 쓰기 기능을 추가한다.

### A5.2 설정 항목

| 항목 | 필드명 |
|---|---|
| 헬스장 이름 | `gymName` |
| 주소 | `gymAddress` |
| 정상 운영 여부 | `isOpen` (toggle) |
| 오늘 휴무 | `todayClosed` (toggle) |
| 오늘 운영 시작 | `todayOpen` (시간 picker) |
| 오늘 운영 종료 | `todayClose` (시간 picker) |

### A5.3 필요 API

```
# 헬스장 설정 조회 (기존 재사용)
GET /gym/setting

# 헬스장 설정 수정 (ADMIN)
PUT /admin/gym/setting
Body: {
  "gymName": "링크핏",
  "gymAddress": "서울 ...",
  "isOpen": true,
  "todayClosed": false,
  "todayOpen": "06:00",
  "todayClose": "22:00"
}
```

---

## A6. 트레이너·토큰 관리 (`/admin/trainers`)

### A6.1 현황

[trainer_home_page.dart:402](lib/screens/trainer_home_page.dart#L402)에 토큰 잔액이 하드코딩("250 토큰")되어 있다.  
관리자 웹에서 토큰을 지급하면 트레이너 앱에서 실제 잔액을 조회한다.

### A6.2 목록 화면

| 컬럼 | 내용 |
|---|---|
| 이름 | - |
| 이메일 | - |
| 토큰 잔액 | 직접 편집 가능 |
| 담당 회원 수 | - |
| [토큰 지급] | 수량 입력 모달 |

### A6.3 필요 API

```
# 트레이너 목록 (ADMIN)
GET /admin/trainers
Response: { "data": [{ "trainerId": 1, "name": "김지훈", "tokenBalance": 250, "memberCount": 18 }] }

# 토큰 지급 (ADMIN)
POST /admin/trainers/{trainerId}/tokens
Body: { "amount": 100 }
Response: { "tokenBalance": 350 }

# 트레이너 토큰 조회 (TRAINER — linkfit 앱 연동)
GET /trainer/tokens
Response: { "balance": 350 }
```

### A6.4 담당 회원 배정

- 트레이너 상세 페이지에서 회원 검색 후 배정
- 이미 `GET /trainer/members` API가 존재하므로 역방향(관리자가 배정) API 추가

```
# 담당 회원 배정 (ADMIN)
POST /admin/trainers/{trainerId}/members
Body: { "userId": 42 }

# 담당 회원 해제 (ADMIN)
DELETE /admin/trainers/{trainerId}/members/{userId}
```

---

## A7. 피드백 요청 현황 (`/admin/feedback`)

### A7.1 목적

트레이너가 피드백 요청을 얼마나 처리했는지 어드민이 모니터링.  
미처리 요청이 누적될 경우 알림 기능 연동(Phase 2).

### A7.2 목록 화면

| 컬럼 | 내용 |
|---|---|
| 회원명 | - |
| 담당 트레이너 | - |
| 요청 종목 | 리스트 |
| 요청일 | - |
| 상태 | 미처리 / 처리완료 |
| 처리일 | - |

### A7.3 필요 API

```
# 전체 피드백 요청 목록 (ADMIN)
GET /admin/feedback-requests?status=PENDING&page=&size=
Response: {
  "data": [
    {
      "requestId": 1,
      "memberName": "홍길동",
      "trainerName": "김지훈",
      "exerciseNames": ["머신 로우", "렛풀다운"],
      "requestedAt": "2026-05-18T09:00:00",
      "status": "PENDING",
      "respondedAt": null
    }
  ]
}
```

---

## A8. 관리자 공통 컴포넌트

| 컴포넌트 | 설명 |
|---|---|
| `AdminLayout` | 사이드바 + 헤더 + 콘텐츠 영역 |
| `DataTable` | 정렬·검색·페이징 공통 테이블 |
| `TierBadge` | FREE / BASIC / PREMIUM / VIP 색상 뱃지 |
| `ConfirmModal` | 삭제·등급변경 등 2차 확인 모달 |
| `ApiClient` | axios 기반, JWT 헤더 자동 주입 |

---

## A9. 관리자 구현 순서 (스프린트)

| Sprint | 항목 | 예상 공수 |
|---|---|---|
| 1 | 프로젝트 셋업 + 인증 + 라우트 보호 | 1일 |
| 1 | 전체 현황 대시보드 (summary API 포함) | 1일 |
| 2 | 회원 목록 + 등급 변경 + PT 세션 관리 | 1.5일 |
| 2 | 운동 종목 CRUD + 이미지 업로드 | 1.5일 |
| 3 | 헬스장 설정 | 0.5일 |
| 3 | 트레이너 목록 + 토큰 지급 + 회원 배정 | 1.5일 |
| 4 | 피드백 요청 현황 | 1일 |

---

## 미결 사항 (백엔드 협의 필요)

| # | 항목 | 대상 |
|---|---|---|
| 1 | 결제 플로우: 1,900원 단품 PG 연동 방식 | linkfit + 백엔드 |
| 2 | Membership tier 변경 후 앱 캐시 만료 정책 | linkfit + 백엔드 |
| 3 | 트레이너 FCM 알림: 피드백 요청 수신 토픽 설계 | linkfit + 백엔드 |
| 4 | 3D 근육 맵 — MVP 제외, Phase 2 별도 검토 | linkfit |
| 5 | 칼로리 계산 — 현재 MET 단순 추정, Phase 2에서 종목별 정밀 계산 | linkfit + 백엔드 |
| 6 | 이미지 스토리지 — S3 버킷 or 서버 로컬 경로 결정 | link-fit-admin + 백엔드 |
| 7 | ADMIN role 계정 생성 방식 — DB 직접 삽입 or 별도 API | 백엔드 |
| 8 | 관리자 웹 배포 환경 — Nginx / Vercel / S3+CloudFront | link-fit-admin + 인프라 |
