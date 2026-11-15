# LINK_Fit Admin — SQL 레퍼런스

> DB: `linkfit` (MariaDB 10.11.14)
> 모든 테이블은 앱 서비스와 공유합니다. **DROP/TRUNCATE 금지.**

---

## 이 디렉토리의 운영 방식

**운영 DB에 적용이 끝난 마이그레이션 파일은 커밋 후 삭제합니다** (2026-11-16부터).

적용 완료 파일을 계속 쌓아두면 "지금 적용해야 할 것"과 "이미 끝난 것"이 섞여,
실제로 2026-11-08에 적용 대상을 빠뜨려 운영 장애(상품 부여 500)로 이어진 적이 있습니다.
여기 파일이 보이면 **아직 운영에 반영되지 않은 것**이라는 뜻입니다.

### 지난 마이그레이션을 찾으려면

삭제된 파일은 git 히스토리에 그대로 남아 있습니다.

```bash
# 파일명으로 찾기
git log --all --diff-filter=D --name-only -- 'docs/sql/*' | grep -i <키워드>

# 내용 복원
git show <commit>:docs/sql/<파일명>.sql
```

`CLAUDE.md`가 파일명으로 인용하는 과거 마이그레이션(`gym_id_backfill_20260820.sql`,
`PROD_DEPLOY_gym_scoping_20260825.sql` 등)도 같은 방법으로 열람할 수 있습니다.

---

## 새 마이그레이션을 추가할 때

- 파일명은 `PROD_DEPLOY_YYYYMMDD_<내용>.sql`
- **재실행 안전(idempotent)하게** 작성합니다 — `information_schema` 확인 후 분기하거나
  `INSERT ... WHERE NOT EXISTS` 사용
- 상단 주석에 **증상 / 원인 / 왜 이 방법인지**를 남깁니다. 나중에 같은 판단을 다시 하지 않도록
- 운영 적용 전 **백업 → 트랜잭션 예행(ROLLBACK) → 실제 적용 → 2회차 재실행 확인** 순서를 지킵니다
  (`CREATE TABLE ... AS SELECT` 같은 DDL은 암묵적 커밋이라 예행에서 제외)

적용이 끝나면 운영 반영 사실을 `CLAUDE.md`에 기록하고 이 디렉토리에서 파일을 지웁니다.

---

## 주의사항

- 테이블 구조 변경(ALTER/DROP)은 앱 서비스에 영향을 주므로 반드시 앱팀과 협의
- `message_conversation`은 `UNIQUE KEY(member_id, trainer_id, category)` 제약이 있어
  동일 조합 INSERT 시 `ON DUPLICATE KEY UPDATE` 처리가 필요합니다
- 인덱스 추가는 트래픽이 낮은 시간대에 적용
