-- =============================================================================
-- 04_indexes.sql  ·  성능 최적화 인덱스 추가 권고
-- =============================================================================
-- 현재 누락된 인덱스 중 어드민 기능 사용 빈도가 높은 컬럼에 대한 추가 권고.
-- 데이터 규모가 작은 현재는 성능 차이가 미미하나, 회원 수 증가 시 체감됨.
--
-- ⚠️  적용 전 주의사항
--   - 서비스 트래픽이 낮은 시간대(새벽)에 적용 권장
--   - MariaDB 에서 CREATE INDEX 는 기본적으로 온라인(InnoDB Online DDL) 지원
--   - 적용 전 EXPLAIN 으로 쿼리 플랜 확인 후 결정
-- =============================================================================


-- -----------------------------------------------------------------------------
-- [1] message_conversation — 어드민 발송 이력 조회 최적화
-- -----------------------------------------------------------------------------

-- 1-1. category 단독 인덱스 (현재 없음)
--      → "WHERE category IN ('공지','이벤트')" 쿼리에서 테이블 풀스캔 방지
CREATE INDEX IF NOT EXISTS idx_mc_category
    ON message_conversation (category);

-- 1-2. category + created_at 복합 인덱스
--      → 이력 조회 시 category 필터 + 최신순 정렬 동시 최적화
CREATE INDEX IF NOT EXISTS idx_mc_category_created
    ON message_conversation (category, created_at DESC);


-- -----------------------------------------------------------------------------
-- [2] chat_message — 발송 이력 조회 최적화
-- -----------------------------------------------------------------------------

-- 2-1. conversation_id + created_at 복합 인덱스 (conversation_id 단독은 이미 존재)
--      → "JOIN + ORDER BY created_at" 정렬 비용 감소
CREATE INDEX IF NOT EXISTS idx_cm_conv_created
    ON chat_message (conversation_id, created_at DESC);

-- 2-2. is_read + conversation_id 복합 인덱스
--      → 앱에서 "읽지 않은 메시지 수" 카운트 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_cm_unread
    ON chat_message (is_read, conversation_id);


-- -----------------------------------------------------------------------------
-- [3] attendance — 출석 통계 쿼리 최적화
-- -----------------------------------------------------------------------------

-- 3-1. attend_date 단독 인덱스
--      → 날짜 기반 필터 (일별/주간/월별 조회) 최적화
CREATE INDEX IF NOT EXISTS idx_att_date
    ON attendance (attend_date);

-- 3-2. user_id + attend_date 복합 인덱스
--      → 회원별 출석 현황 (member-stats) 조회 최적화
CREATE INDEX IF NOT EXISTS idx_att_user_date
    ON attendance (user_id, attend_date);


-- -----------------------------------------------------------------------------
-- [4] member_freeze — 유증 조회 최적화
-- -----------------------------------------------------------------------------

-- 4-1. freeze_start + freeze_end 복합 인덱스
--      → "WHERE freeze_start <= ? AND freeze_end >= ?" 범위 쿼리 최적화
CREATE INDEX IF NOT EXISTS idx_freeze_range
    ON member_freeze (freeze_start, freeze_end);


-- -----------------------------------------------------------------------------
-- [5] sale — 매출 통계·검색 최적화
-- -----------------------------------------------------------------------------

-- 5-1. sale_date + product_type 복합 인덱스
--      → 날짜+유형 필터 조합 검색 최적화 (결제 내역, 통계 모두 사용)
CREATE INDEX IF NOT EXISTS idx_sale_date_type
    ON sale (sale_date, product_type);

-- 5-2. user_id + sale_date 복합 인덱스
--      → 특정 회원 매출 이력 조회 최적화
CREATE INDEX IF NOT EXISTS idx_sale_user_date
    ON sale (user_id, sale_date);


-- -----------------------------------------------------------------------------
-- [6] ticket_purchases — 티켓 판매 검색 최적화
-- -----------------------------------------------------------------------------
-- 현재 이미 idx_user_product (user_id, product_id) 인덱스 존재.
-- 추가로 날짜 범위 검색 시:

-- 6-1. product_id + created_at 복합 인덱스
CREATE INDEX IF NOT EXISTS idx_tp_product_created
    ON ticket_purchases (product_id, created_at DESC);


-- -----------------------------------------------------------------------------
-- [7] 적용 확인 쿼리
-- -----------------------------------------------------------------------------

-- 현재 인덱스 전체 확인
SHOW INDEX FROM message_conversation;
SHOW INDEX FROM chat_message;
SHOW INDEX FROM attendance;
SHOW INDEX FROM member_freeze;
SHOW INDEX FROM sale;
SHOW INDEX FROM ticket_purchases;

-- 특정 쿼리 실행 계획 확인 (인덱스 사용 여부)
EXPLAIN
SELECT mc.category, cm.content, COUNT(DISTINCT mc.id) AS cnt
FROM   message_conversation mc
JOIN   chat_message cm ON cm.conversation_id = mc.id
WHERE  mc.category IN ('공지', '이벤트')
GROUP BY mc.category, cm.content, DATE_FORMAT(mc.created_at, '%Y-%m-%d %H:%i')
ORDER BY mc.created_at DESC
LIMIT 20;


-- -----------------------------------------------------------------------------
-- [8] 인덱스 제거 (롤백 필요 시)
-- -----------------------------------------------------------------------------

/*
DROP INDEX IF EXISTS idx_mc_category         ON message_conversation;
DROP INDEX IF EXISTS idx_mc_category_created ON message_conversation;
DROP INDEX IF EXISTS idx_cm_conv_created     ON chat_message;
DROP INDEX IF EXISTS idx_cm_unread           ON chat_message;
DROP INDEX IF EXISTS idx_att_date            ON attendance;
DROP INDEX IF EXISTS idx_att_user_date       ON attendance;
DROP INDEX IF EXISTS idx_freeze_range        ON member_freeze;
DROP INDEX IF EXISTS idx_sale_date_type      ON sale;
DROP INDEX IF EXISTS idx_sale_user_date      ON sale;
DROP INDEX IF EXISTS idx_tp_product_created  ON ticket_purchases;
*/
