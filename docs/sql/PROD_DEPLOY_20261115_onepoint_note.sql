-- 원포인트 신청 — 관리자 처리 메모 컬럼 추가 (2026-11-15)
--
-- 증상: 관리자페이지 트레이너 관리 > "원포인트 신청" 탭이 항상 500.
--       (운영 access log에 실제 사용자 요청도 500으로 남아 있었다)
--
-- 원인: OnepointRequestMapper가 `op.note`(관리자 처리 메모)를 읽고 쓰는데
--       운영 DB엔 이 컬럼이 없다. 로컬에만 있어서 개발 중엔 드러나지 않았다
--       — 2026-11-08 컬럼 전수 비교 때 "매퍼는 운영에도 있는 notes를 쓴다"고
--       잘못 판단해 대상에서 제외했던 항목이다(실제로는 notes와 note를 둘 다 쓴다).
--
--   notes : 회원이 신청 시 작성하는 요청 내용 (운영에 이미 있음)
--   note  : 관리자가 승인/반려하며 남기는 처리 사유 (이번에 추가)
--
-- 두 값은 용도가 달라 합칠 수 없다 — note를 notes로 매핑하면 관리자 메모가
-- 회원이 쓴 내용을 덮어쓴다.
--
-- 이 컬럼이 없으면 목록 조회(findAll)뿐 아니라 승인/반려(updateStatus)도 실패한다.
-- 재실행 안전.

SELECT '적용 전' AS 구분,
       (SELECT COUNT(*) FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = 'onepoint_requests'
           AND COLUMN_NAME = 'note') AS note컬럼;

SET @ddl = (
    SELECT IF(COUNT(*) = 0,
        'ALTER TABLE onepoint_requests ADD COLUMN note TEXT NULL COMMENT ''관리자 처리 메모(승인/반려 사유)''',
        'SELECT ''이미 존재 — 건너뜀'' AS msg')
      FROM information_schema.COLUMNS
     WHERE TABLE_SCHEMA = DATABASE()
       AND TABLE_NAME = 'onepoint_requests'
       AND COLUMN_NAME = 'note'
);
PREPARE s FROM @ddl; EXECUTE s; DEALLOCATE PREPARE s;

SELECT '적용 후' AS 구분,
       (SELECT COUNT(*) FROM information_schema.COLUMNS
         WHERE TABLE_SCHEMA = DATABASE()
           AND TABLE_NAME = 'onepoint_requests'
           AND COLUMN_NAME = 'note') AS note컬럼;
