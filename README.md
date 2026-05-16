# LINK_Fit Admin

LINK_Fit 서비스의 관리자 웹 페이지입니다.

## 기술 스택

| 항목 | 내용 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.0.4 |
| Template | Thymeleaf |
| Build | Gradle |

## 프로젝트 구조

```
link-fit-admin/
├── build.gradle
├── settings.gradle
├── gradle/
│   └── wrapper/
│       └── gradle-wrapper.properties
└── src/
    ├── main/
    │   ├── java/com/linkfit/admin/
    │   │   ├── LinkFitAdminApplication.java     ← Spring Boot 진입점
    │   │   └── controller/
    │   │       └── LoginController.java         ← 로그인 라우팅
    │   └── resources/
    │       ├── application.properties
    │       ├── templates/
    │       │   ├── login.html                   ← 로그인 페이지
    │       │   └── dashboard.html               ← 대시보드 (임시)
    │       └── static/css/
    │           └── login.css                    ← 다크 테마 스타일
    └── test/
        └── java/com/linkfit/admin/
```

## 실행 방법

### 1. 사전 요구 사항

- JDK 21 이상
- (Gradle Wrapper 포함되어 있어 별도 Gradle 설치 불필요)

### 2. 빌드

```bash
./gradlew build
```

### 3. 실행

```bash
./gradlew bootRun
```

### 4. 접속

브라우저에서 아래 주소로 접속

```
http://localhost:17579
```

### Windows 환경

```bat
gradlew.bat bootRun
```

## 임시 로그인 계정

> 실제 서비스 전 반드시 인증 로직을 교체하세요.

| 아이디 | 비밀번호 |
|---|---|
| admin | admin123 |

## 디자인 테마

LINK_Fit 앱과 동일한 다크 테마를 사용합니다.

| 색상 | 값 |
|---|---|
| 배경 | `#0D1117` |
| 카드/서피스 | `#161B22` |
| 액센트(블루) | `#58A6FF` |
| 에러(레드) | `#F85149` |
| 테두리 | `#30363D` |
