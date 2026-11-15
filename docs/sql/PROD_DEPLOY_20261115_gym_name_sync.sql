-- 지점명 정본 통일 — gym_setting.gym_name → gym.name 백필 (2026-11-15, crm-8)
--
-- 증상: 본사 포탈에 보이는 지점명과 관리자페이지 "헬스장 이름"이 다름.
--
-- 원인: 저장 위치가 아예 다른 컬럼이었다.
--   gym_setting.gym_name : 관리자페이지 설정 화면에서 입력/표시 (관리자가 직접 관리)
--   gym.name             : 본사 포탈 목록·정산, 그리고 **회원에게 가는 쪽지 발신자명**
--   관리자가 이름을 바꿔도 gym.name은 그대로라 둘이 영구히 갈라졌다.
--
-- 결정(2026-11-15 팀): 관리자가 입력한 이름을 정본으로 한다.
--   → 코드: PUT /api/settings/gym 이 저장 시 gym.name도 함께 갱신(syncGymName)
--   → 데이터: 이미 갈라진 기존 행을 이 스크립트로 맞춘다
--
-- gym_setting 행이 없는 지점은 관리자가 아직 설정을 저장한 적이 없다는 뜻이므로
-- 건드리지 않는다(기존 gym.name 유지). 빈 문자열도 제외 — 이름을 지우면 안 된다.
--
-- 재실행 안전(이미 같으면 0행 갱신).

SELECT '적용 전' AS 구분, g.id, g.branch_code AS 지점코드,
       g.name AS `gym.name`, gs.gym_name AS `gym_setting`,
       IF(g.name = gs.gym_name, '일치', '불일치') AS 상태
  FROM gym g
  JOIN gym_setting gs ON gs.gym_id = g.id
 WHERE gs.gym_name IS NOT NULL AND TRIM(gs.gym_name) <> '';

UPDATE gym g
  JOIN gym_setting gs ON gs.gym_id = g.id
   SET g.name = gs.gym_name
 WHERE gs.gym_name IS NOT NULL
   AND TRIM(gs.gym_name) <> ''
   AND g.name <> gs.gym_name;

SELECT '적용 후' AS 구분, g.id, g.branch_code AS 지점코드,
       g.name AS `gym.name`, gs.gym_name AS `gym_setting`,
       IF(g.name = gs.gym_name, '일치', '불일치') AS 상태
  FROM gym g
  JOIN gym_setting gs ON gs.gym_id = g.id
 WHERE gs.gym_name IS NOT NULL AND TRIM(gs.gym_name) <> '';
