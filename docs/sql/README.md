# LINK_Fit Admin — SQL 레퍼런스

이 디렉토리에는 어드민 기능 개발 시 사용·추가된 SQL 쿼리와 스키마 참고 자료를 기능 단위로 정리합니다.

> DB: `linkfit` (MariaDB 10.11.14 · localhost:3306)  
> 모든 테이블은 앱 서비스와 공유. **DROP/TRUNCATE 금지.**

---

## 파일 목록

| 파일 | 대상 기능 | 상태 |
|------|-----------|------|
| [01_messages.sql](01_messages.sql) | 메시지 공지·이벤트 발송 시스템 | ✅ 완료 |
| [02_attendance.sql](02_attendance.sql) | 출석 관리 (현황·통계·유증) | ✅ 완료 |
| [03_revenue.sql](03_revenue.sql) | 매출·티켓 판매 관리 | ✅ 완료 |
| [04_indexes.sql](04_indexes.sql) | 성능 개선 인덱스 추가 | ⚠️ 선택 적용 |

---

## 주의사항

- 테이블 구조 변경(ALTER, DROP)은 앱 서비스에 영향을 줄 수 있으므로 반드시 앱팀과 협의.
- 인덱스 추가(`04_indexes.sql`)는 서비스 트래픽이 낮은 시간에 적용 권장.
- `message_conversation`은 `UNIQUE KEY(member_id, trainer_id, category)` 제약이 있어  
  동일 조합 INSERT 시 `ON DUPLICATE KEY UPDATE` 처리 필요 (자세한 내용: `01_messages.sql`).
