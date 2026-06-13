-- =============================================================================
-- 02_attendance.sql  ·  출석 관리 (현황·통계·유증)
-- =============================================================================
-- 대상 테이블: attendance, member_freeze, users, user_profiles
-- 관련 Mapper:  AttendanceMapper.java / AttendanceMapper.xml
-- 관련 API:     AttendanceApiController
-- =============================================================================

-- -----------------------------------------------------------------------------
-- [1] 기존 테이블 DDL (참고용 — 재생성 금지)
-- -----------------------------------------------------------------------------

/*
CREATE TABLE `attendance` (
  `id`             bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `type`           varchar(20)         NOT NULL DEFAULT 'GENERAL',  -- 'GENERAL' | 'PT' | 'ROUTINE'
  `attend_date`    date                NOT NULL,
  `check_in_time`  time                NOT NULL,
  `created_at`     datetime            NOT NULL DEFAULT current_timestamp(),
  `user_id`        varchar(50)         NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_attendance_user` (`user_id`),
  CONSTRAINT `fk_attendance_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE `member_freeze` (
  `id`           bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `freeze_start` date                NOT NULL,
  `freeze_end`   date                NOT NULL,
  `reason`       varchar(255)        DEFAULT NULL,
  `created_at`   datetime            NOT NULL DEFAULT current_timestamp(),
  `user_id`      varchar(50)         NOT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_freeze_user` (`user_id`),
  CONSTRAINT `fk_freeze_user`
    FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
*/


-- -----------------------------------------------------------------------------
-- [2] 출석 현황 조회
-- -----------------------------------------------------------------------------

-- 2-1. 특정 날짜 출석 목록 (일별)
SELECT a.id, a.user_id AS memberId, up.name AS memberName,
       a.type, a.attend_date AS attendDate, a.check_in_time AS checkInTime
FROM   attendance a
JOIN   user_profiles up ON a.user_id = up.user_id
WHERE  a.attend_date = CURDATE()
ORDER BY a.check_in_time;

-- 2-2. 주간 출석 (이번 주 월~일)
SELECT a.attend_date AS attendDate,
       COUNT(*)      AS count
FROM   attendance a
WHERE  a.attend_date BETWEEN
         DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY)
         AND DATE_ADD(DATE_SUB(CURDATE(), INTERVAL WEEKDAY(CURDATE()) DAY), INTERVAL 6 DAY)
GROUP BY a.attend_date
ORDER BY a.attend_date;

-- 2-3. 월간 출석 집계
SELECT DATE_FORMAT(a.attend_date, '%Y-%m') AS ym,
       COUNT(*)                             AS total,
       COUNT(CASE WHEN a.type='PT'      THEN 1 END) AS ptCount,
       COUNT(CASE WHEN a.type='GENERAL' THEN 1 END) AS generalCount
FROM   attendance a
WHERE  a.attend_date >= DATE_FORMAT(CURDATE(), '%Y-%m-01')
GROUP BY ym;


-- -----------------------------------------------------------------------------
-- [3] 회원별 이달 출석 현황 (AttendanceMapper.memberMonthlyStats)
-- -----------------------------------------------------------------------------

-- 이달 출석 횟수 + 마지막 출석일
SELECT u.user_id                  AS memberId,
       up.name                    AS memberName,
       up.tier                    AS tier,
       COUNT(a.id)                AS attendCount,
       MAX(a.attend_date)         AS lastAttend
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
LEFT JOIN attendance a
       ON a.user_id = u.user_id
      AND DATE_FORMAT(a.attend_date, '%Y-%m') = DATE_FORMAT(CURDATE(), '%Y-%m')
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
GROUP BY u.user_id, up.name, up.tier
ORDER BY attendCount DESC;

-- 특정 월 지정 (파라미터 버전)
SELECT u.user_id                  AS memberId,
       up.name                    AS memberName,
       up.tier                    AS tier,
       COUNT(a.id)                AS attendCount,
       MAX(a.attend_date)         AS lastAttend
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
LEFT JOIN attendance a
       ON a.user_id = u.user_id
      AND DATE_FORMAT(a.attend_date, '%Y-%m') = '2026-06'   -- #{yearMonth}
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
GROUP BY u.user_id, up.name, up.tier
ORDER BY attendCount DESC;


-- -----------------------------------------------------------------------------
-- [4] 장기 미출석 회원 (AttendanceMapper.inactiveMembers)
-- -----------------------------------------------------------------------------

-- 최근 N일 이상 미출석 (or 출석 이력 없음)
SELECT u.user_id                        AS memberId,
       up.name                          AS memberName,
       up.tier                          AS tier,
       up.trainer_id                    AS trainerId,
       up2.name                         AS trainerName,
       MAX(a.attend_date)               AS lastAttend,
       DATEDIFF(CURDATE(), MAX(a.attend_date)) AS daysSince
FROM   users u
JOIN   user_profiles up ON u.user_id = up.user_id
LEFT JOIN user_profiles up2 ON up2.user_id = up.trainer_id
LEFT JOIN attendance a ON a.user_id = u.user_id
WHERE  u.role = 'MEMBER'
  AND  u.is_active = 1
  AND  u.deleted_at IS NULL
GROUP BY u.user_id, up.name, up.tier, up.trainer_id, up2.name
HAVING daysSince >= 30           -- #{days} 파라미터
    OR lastAttend IS NULL
ORDER BY daysSince DESC;


-- -----------------------------------------------------------------------------
-- [5] 유증(정지) 관리 (member_freeze)
-- -----------------------------------------------------------------------------

-- 5-1. 현재 유효한 유증 회원 목록 (특정 날짜 기준)
SELECT mf.id, mf.user_id AS memberId, up.name AS memberName,
       mf.freeze_start AS freezeStart, mf.freeze_end AS freezeEnd,
       mf.reason, mf.created_at AS createdAt
FROM   member_freeze mf
JOIN   user_profiles up ON mf.user_id = up.user_id
WHERE  mf.freeze_start <= CURDATE()
  AND  mf.freeze_end   >= CURDATE()   -- #{date} 기준 유효한 유증만
ORDER BY mf.freeze_start;

-- 5-2. 전체 유증 이력 (기간 무관)
SELECT mf.id, mf.user_id AS memberId, up.name AS memberName,
       mf.freeze_start, mf.freeze_end,
       mf.reason, mf.created_at
FROM   member_freeze mf
JOIN   user_profiles up ON mf.user_id = up.user_id
ORDER BY mf.freeze_start DESC;

-- 5-3. 유증 등록
INSERT INTO member_freeze (user_id, freeze_start, freeze_end, reason, created_at)
VALUES ('gildong_hong', '2026-06-15', '2026-06-30', '해외 출장', NOW());

-- 5-4. 유증 해제 (삭제)
DELETE FROM member_freeze WHERE id = 1;

-- 5-5. 현재 유증 중인 회원 수
SELECT COUNT(DISTINCT user_id) AS frozenCount
FROM   member_freeze
WHERE  freeze_start <= CURDATE()
  AND  freeze_end   >= CURDATE();


-- -----------------------------------------------------------------------------
-- [6] 출석 체크 INSERT
-- -----------------------------------------------------------------------------

-- 출석 등록 (체크인)
INSERT INTO attendance (user_id, type, attend_date, check_in_time, created_at)
VALUES ('gildong_hong', 'GENERAL', CURDATE(), CURTIME(), NOW());

-- 출석 취소 (삭제)
DELETE FROM attendance WHERE id = 1;


-- -----------------------------------------------------------------------------
-- [7] 출석 추이 (30일 트렌드)
-- -----------------------------------------------------------------------------

SELECT a.attend_date    AS date,
       COUNT(*)         AS count
FROM   attendance a
WHERE  a.attend_date >= DATE_SUB(CURDATE(), INTERVAL 30 DAY)
GROUP BY a.attend_date
ORDER BY a.attend_date;
