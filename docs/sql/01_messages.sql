-- =============================================================================
-- 01_messages.sql  ·  메시지(공지·이벤트) 발송 시스템
-- =============================================================================
-- 대상 테이블: message_conversation, chat_message
-- 관련 Mapper:  ConversationMapper.java / ConversationMapper.xml
-- 관련 API:     MessageApiController  (POST /api/messages/broadcast)
-- =============================================================================

-- -----------------------------------------------------------------------------
-- [1] 기존 테이블 DDL (참고용 — 재생성 금지, 앱 서비스 사용 중)
-- -----------------------------------------------------------------------------

/*
CREATE TABLE `message_conversation` (
  `id`         int(11)       NOT NULL AUTO_INCREMENT,
  `created_at` datetime      NOT NULL DEFAULT current_timestamp(),
  `member_id`  varchar(50)   NOT NULL,
  `trainer_id` varchar(50)   NOT NULL,
  `category`   enum('쪽지','기록','공지','이벤트','원포인트') NOT NULL DEFAULT '쪽지',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_conversation` (`member_id`, `trainer_id`, `category`),  -- ⚠️ 중요
  KEY `message_conversation_ibfk_2` (`trainer_id`),
  CONSTRAINT `message_conversation_ibfk_1`
    FOREIGN KEY (`member_id`)  REFERENCES `users` (`user_id`) ON DELETE CASCADE,
  CONSTRAINT `message_conversation_ibfk_2`
    FOREIGN KEY (`trainer_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `chat_message` (
  `id`              int(11)       NOT NULL AUTO_INCREMENT,
  `conversation_id` int(11)       NOT NULL,
  `content`         text          NOT NULL,
  `is_read`         tinyint(1)    NOT NULL DEFAULT 0,
  `created_at`      datetime      NOT NULL DEFAULT current_timestamp(),
  `sender_id`       varchar(50)   NOT NULL,
  `record_id`       bigint(20)    DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `conversation_id` (`conversation_id`),
  KEY `chat_message_ibfk_2` (`sender_id`),
  CONSTRAINT `chat_message_ibfk_1`
    FOREIGN KEY (`conversation_id`) REFERENCES `message_conversation` (`id`) ON DELETE CASCADE,
  CONSTRAINT `chat_message_ibfk_2`
    FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/


-- -----------------------------------------------------------------------------
-- [2] 제약 사항 — UNIQUE KEY 처리
-- -----------------------------------------------------------------------------
-- message_conversation 에는 (member_id, trainer_id, category) 복합 UNIQUE KEY 가 있다.
-- 동일한 회원-트레이너-카테고리 조합이 이미 존재할 때 INSERT 하면 Duplicate Key 오류 발생.
--
-- 어드민 공지 발송 시 처리 방법:
--   기존 대화방이 있으면 → 그 대화방에 새 메시지 추가 (이력 누적)
--   기존 대화방이 없으면 → 새 대화방 생성 후 메시지 추가
--
-- ConversationMapper.xml 에서 아래와 같이 처리:

-- 대화방 upsert (기존 있으면 id만 반환, 없으면 신규 생성)
INSERT INTO message_conversation (member_id, trainer_id, category, created_at)
VALUES (?, ?, ?, NOW())
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);
-- → LAST_INSERT_ID() 가 새 id 또는 기존 id 로 세팅됨
-- → MyBatis useGeneratedKeys="true" 로 Java Map에 id 반환


-- -----------------------------------------------------------------------------
-- [3] 관리자 발송 쿼리 — 발송 대상 조회
-- -----------------------------------------------------------------------------

-- 3-1. 전체 활성 회원 user_id 목록
SELECT u.user_id
FROM   users u
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL;

-- 3-2. 특정 등급(tier) 회원 목록
SELECT u.user_id
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
  AND  up.tier = 'INTENSIVE_FIT';  -- 'BASIC' | 'LIGHT_FIT' | 'REGULAR_FIT' | 'INTENSIVE_FIT'

-- 3-3. 특정 트레이너 담당 회원 목록
SELECT u.user_id
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
  AND  up.trainer_id = 'hyunwoo_choi';

-- 3-4. 전체 활성 트레이너 user_id 목록
SELECT user_id
FROM   users
WHERE  role = 'TRAINER'
  AND  is_active = 1
  AND  deleted_at IS NULL;

-- 3-5. 발신자 선택용 트레이너 목록 (이름 포함)
SELECT u.user_id AS userId, up.name AS name
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
WHERE  u.role = 'TRAINER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
ORDER BY up.name;

-- 3-6. 발송 대상 인원 수 미리보기
SELECT COUNT(u.user_id)
FROM   users u
LEFT JOIN user_profiles up ON u.user_id = up.user_id
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
  AND  up.tier = 'REGULAR_FIT';  -- 선택적 필터


-- -----------------------------------------------------------------------------
-- [4] 메시지 발송 INSERT 흐름 (단건 예시)
-- -----------------------------------------------------------------------------

-- Step 1: 대화방 생성 or 기존 ID 반환
INSERT INTO message_conversation (member_id, trainer_id, category, created_at)
VALUES ('gildong_hong', 'hyunwoo_choi', '공지', NOW())
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);

-- Step 2: 위에서 얻은 conversation_id 로 메시지 INSERT
INSERT INTO chat_message (conversation_id, sender_id, content, is_read, created_at)
VALUES (LAST_INSERT_ID(), 'hyunwoo_choi', '이번 주 토요일 헬스장 단체 행사 안내드립니다.', 0, NOW());


-- -----------------------------------------------------------------------------
-- [5] 발송 이력 조회 — 어드민 이력 화면
-- -----------------------------------------------------------------------------

-- 5-1. 발송 배치 단위 그룹핑 (같은 분·카테고리·내용·발신자를 하나의 배치로)
SELECT mc.category,
       cm.content,
       up_t.name                                        AS senderName,
       COUNT(DISTINCT mc.id)                            AS recipientCount,
       DATE_FORMAT(mc.created_at, '%Y-%m-%d %H:%i')    AS sentAt,
       MIN(mc.id)                                       AS id
FROM   message_conversation mc
JOIN   chat_message cm     ON cm.conversation_id = mc.id
LEFT JOIN user_profiles up_t ON up_t.user_id = cm.sender_id
WHERE  mc.category IN ('공지', '이벤트')
GROUP BY mc.category,
         cm.content,
         cm.sender_id,
         DATE_FORMAT(mc.created_at, '%Y-%m-%d %H:%i')
ORDER BY sentAt DESC
LIMIT 20 OFFSET 0;

-- 5-2. 특정 카테고리만 필터
SELECT mc.category,
       cm.content,
       COUNT(DISTINCT mc.id) AS recipientCount,
       DATE_FORMAT(mc.created_at, '%Y-%m-%d %H:%i') AS sentAt
FROM   message_conversation mc
JOIN   chat_message cm ON cm.conversation_id = mc.id
WHERE  mc.category = '이벤트'
GROUP BY mc.category, cm.content, cm.sender_id,
         DATE_FORMAT(mc.created_at, '%Y-%m-%d %H:%i')
ORDER BY sentAt DESC;

-- 5-3. 특정 회원이 받은 공지 목록 (앱 쪽 참고)
SELECT cm.content, mc.category, cm.is_read, cm.created_at
FROM   message_conversation mc
JOIN   chat_message cm ON cm.conversation_id = mc.id
WHERE  mc.member_id = 'gildong_hong'
  AND  mc.category IN ('공지', '이벤트')
ORDER BY cm.created_at DESC;

-- 5-4. 읽지 않은 공지 수 (앱 쪽 배지 카운트 참고)
SELECT COUNT(*)
FROM   message_conversation mc
JOIN   chat_message cm ON cm.conversation_id = mc.id
WHERE  mc.member_id = ?
  AND  mc.category = '공지'
  AND  cm.is_read = 0;


-- -----------------------------------------------------------------------------
-- [6] 읽음 처리 (앱 참고용)
-- -----------------------------------------------------------------------------

-- 특정 회원의 공지 전체 읽음 처리
UPDATE chat_message cm
JOIN   message_conversation mc ON mc.id = cm.conversation_id
SET    cm.is_read = 1
WHERE  mc.member_id = 'gildong_hong'
  AND  mc.category = '공지'
  AND  cm.is_read = 0;


-- -----------------------------------------------------------------------------
-- [7] 샘플 데이터 — 공지 전체 발송 테스트
-- -----------------------------------------------------------------------------

-- 모든 활성 회원에게 'hyunwoo_choi' 트레이너 명의로 공지 발송
-- (실제 실행 전 트랜잭션으로 감싸고 ROLLBACK 테스트 권장)
/*
BEGIN;

INSERT INTO message_conversation (member_id, trainer_id, category, created_at)
SELECT u.user_id, 'hyunwoo_choi', '공지', NOW()
FROM   users u
WHERE  u.role = 'MEMBER' AND u.is_active = 1 AND u.deleted_at IS NULL
ON DUPLICATE KEY UPDATE id = LAST_INSERT_ID(id);

-- 위 INSERT로 생성된 대화방에 각각 메시지 INSERT (루프로 처리하거나 아래처럼)
-- ※ 배치 INSERT 는 Java 서비스 레이어에서 루프로 처리 (conversation_id 별도 관리 필요)

ROLLBACK;
*/
