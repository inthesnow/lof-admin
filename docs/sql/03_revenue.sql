-- =============================================================================
-- 03_revenue.sql  ·  매출 및 티켓 판매 관리
-- =============================================================================
-- 대상 테이블: sale, ticket_purchases, product, user_profiles
-- 관련 Mapper:  SaleMapper.java / SaleMapper.xml
--               TicketPurchaseMapper.java / TicketPurchaseMapper.xml
-- 관련 API:     RevenueApiController
-- =============================================================================

-- -----------------------------------------------------------------------------
-- [1] 기존 테이블 DDL (참고용 — 재생성 금지)
-- -----------------------------------------------------------------------------

/*
CREATE TABLE `sale` (
  `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `product_id`     bigint(20) unsigned DEFAULT NULL,
  `product_name`   varchar(100)        DEFAULT NULL,
  `product_type`   varchar(30)         DEFAULT NULL,  -- 'membership'|'pt'|'group'|'locker'|'item' 등
  `amount`         int(11)             NOT NULL DEFAULT 0,
  `payment_method` varchar(30)         NOT NULL DEFAULT 'CARD',
  `sale_date`      date                NOT NULL,
  `memo`           text                DEFAULT NULL,
  `created_at`     datetime            NOT NULL DEFAULT current_timestamp(),
  `user_id`        varchar(50)         DEFAULT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_sale_product` FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE SET NULL,
  CONSTRAINT `fk_sale_user`    FOREIGN KEY (`user_id`)    REFERENCES `users`(`user_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `ticket_purchases` (
  `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `user_id`        varchar(50)         NOT NULL,
  `product_id`     varchar(30)         NOT NULL,           -- 'FEEDBACK_TICKET' | 'ONEPOINT_CARD'
  `quantity`       int(11)             NOT NULL DEFAULT 1,
  `price_krw`      int(11)             NOT NULL,
  `receipt_id`     varchar(50)         NOT NULL,           -- UNIQUE
  `payment_method` varchar(20)         NOT NULL DEFAULT 'CARD',
  `payment_token`  varchar(255)        DEFAULT NULL,
  `description`    varchar(100)        NOT NULL DEFAULT '',
  `created_at`     datetime            NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`),
  UNIQUE KEY `receipt_id` (`receipt_id`),
  KEY `idx_user_product` (`user_id`, `product_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/


-- -----------------------------------------------------------------------------
-- [2] 매출 통계 (SaleMapper.revenueStats)
-- -----------------------------------------------------------------------------

-- 2-1. 이달 카테고리별 매출 요약
SELECT SUM(CASE WHEN product_type = 'membership' THEN amount ELSE 0 END) AS membership,
       SUM(CASE WHEN product_type = 'pt'         THEN amount ELSE 0 END) AS pt,
       SUM(CASE WHEN product_type = 'group'      THEN amount ELSE 0 END) AS groupClass,
       SUM(CASE WHEN product_type = 'locker'     THEN amount ELSE 0 END) AS locker,
       SUM(CASE WHEN product_type = 'item'       THEN amount ELSE 0 END) AS items,
       SUM(amount)                                                         AS total
FROM   sale
WHERE  DATE_FORMAT(sale_date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m');

-- 2-2. 특정 날짜 기준 일별
SELECT SUM(amount) AS total, COUNT(*) AS count
FROM   sale
WHERE  sale_date = CURDATE();


-- -----------------------------------------------------------------------------
-- [3] 결제 내역 페이지네이션 (SaleMapper.findPaged)
-- -----------------------------------------------------------------------------

-- 3-1. 전체 내역 (최신순, 20건)
SELECT s.id,
       s.user_id        AS memberId,
       up.name          AS memberName,
       s.product_name   AS productName,
       s.product_type   AS productType,
       s.amount,
       s.payment_method AS paymentMethod,
       s.sale_date      AS saleDate,
       s.memo
FROM   sale s
LEFT JOIN user_profiles up ON s.user_id = up.user_id
ORDER BY s.sale_date DESC, s.created_at DESC
LIMIT 20 OFFSET 0;

-- 3-2. 유형 필터 + 날짜 범위 필터
SELECT s.id, up.name AS memberName, s.product_name, s.amount, s.sale_date
FROM   sale s
LEFT JOIN user_profiles up ON s.user_id = up.user_id
WHERE  s.product_type = 'membership'           -- 선택적
  AND  s.sale_date >= '2026-06-01'             -- 선택적
  AND  s.sale_date <= '2026-06-30'             -- 선택적
ORDER BY s.sale_date DESC
LIMIT 20 OFFSET 0;

-- 3-3. 건수 조회 (페이지네이션용)
SELECT COUNT(*) FROM sale
WHERE  product_type = 'membership'
  AND  sale_date BETWEEN '2026-06-01' AND '2026-06-30';

-- 3-4. 환불 처리 (행 삭제)
DELETE FROM sale WHERE id = 1;


-- -----------------------------------------------------------------------------
-- [4] CSV 내보내기용 쿼리 (SaleMapper.findForExport, 최대 5000건)
-- -----------------------------------------------------------------------------

SELECT s.id,
       s.sale_date      AS saleDate,
       up.name          AS memberName,
       s.product_name   AS productName,
       s.product_type   AS productType,
       s.amount,
       s.payment_method AS paymentMethod,
       s.memo
FROM   sale s
LEFT JOIN user_profiles up ON s.user_id = up.user_id
ORDER BY s.sale_date DESC, s.created_at DESC
LIMIT 5000 OFFSET 0;


-- -----------------------------------------------------------------------------
-- [5] 월별 매출 추이 (SaleMapper.monthlyTrend)
-- -----------------------------------------------------------------------------

-- 최근 6개월 유형별 월 매출
SELECT DATE_FORMAT(sale_date, '%Y-%m')                              AS ym,
       SUM(CASE WHEN product_type = 'membership' THEN amount ELSE 0 END) AS membership,
       SUM(CASE WHEN product_type = 'pt'         THEN amount ELSE 0 END) AS pt,
       SUM(CASE WHEN product_type = 'group'      THEN amount ELSE 0 END) AS groupClass,
       SUM(amount)                                                         AS total
FROM   sale
WHERE  sale_date >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
GROUP BY DATE_FORMAT(sale_date, '%Y-%m')
ORDER BY ym;


-- -----------------------------------------------------------------------------
-- [6] 구독 등급 분포 (SaleMapper.tierDistribution)
-- -----------------------------------------------------------------------------

-- 현재 활성 회원의 tier 분포 (등급별 가입자 수)
SELECT up.tier,
       COUNT(*) AS memberCount
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
GROUP BY up.tier
ORDER BY FIELD(up.tier, 'INTENSIVE_FIT', 'REGULAR_FIT', 'LIGHT_FIT', 'BASIC');
--        ↑ ENUM 순서대로 정렬


-- -----------------------------------------------------------------------------
-- [7] 티켓 구매 이력 (TicketPurchaseMapper)
-- -----------------------------------------------------------------------------

-- 7-1. 전체 티켓 구매 내역
SELECT tp.id, tp.user_id AS memberId, up.name AS memberName,
       tp.product_id AS productId,       -- 'FEEDBACK_TICKET' | 'ONEPOINT_CARD'
       tp.quantity, tp.price_krw AS priceKrw,
       tp.payment_method AS paymentMethod,
       tp.description,
       DATE(tp.created_at) AS purchaseDate
FROM   ticket_purchases tp
LEFT JOIN user_profiles up ON tp.user_id = up.user_id
ORDER BY tp.created_at DESC
LIMIT 20 OFFSET 0;

-- 7-2. 특정 티켓 유형 필터
SELECT tp.id, up.name AS memberName, tp.quantity, tp.price_krw, tp.created_at
FROM   ticket_purchases tp
LEFT JOIN user_profiles up ON tp.user_id = up.user_id
WHERE  tp.product_id = 'FEEDBACK_TICKET'
  AND  tp.created_at >= '2026-06-01'
ORDER BY tp.created_at DESC;

-- 7-3. 건수 조회
SELECT COUNT(*) FROM ticket_purchases tp
WHERE  tp.product_id = 'FEEDBACK_TICKET';

-- 7-4. 월별 티켓 유형별 판매 추이 (TicketPurchaseMapper.statsByType)
SELECT product_id                       AS productId,
       COUNT(*)                         AS purchaseCount,
       SUM(quantity)                    AS totalQty,
       SUM(price_krw)                   AS totalRevenue,
       DATE_FORMAT(created_at, '%Y-%m') AS ym
FROM   ticket_purchases
WHERE  created_at >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH)
GROUP BY product_id, DATE_FORMAT(created_at, '%Y-%m')
ORDER BY ym DESC, product_id;


-- -----------------------------------------------------------------------------
-- [8] 샘플 매출 데이터 (테스트용)
-- -----------------------------------------------------------------------------

/*
INSERT INTO sale (user_id, product_name, product_type, amount, payment_method, sale_date, memo)
VALUES
  ('gildong_hong',  'INTENSIVE FIT 1개월', 'membership', 150000, 'CARD', CURDATE(), NULL),
  ('jisoo_kim',     'PT 10회권',            'pt',         500000, 'CARD', CURDATE(), '첫 등록 할인'),
  ('jiwoo_kim',     '그룹 요가 1개월',      'group',       80000, 'CASH', CURDATE(), NULL);
*/
