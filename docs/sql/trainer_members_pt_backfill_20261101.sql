-- trainer_members.member_type 오분류 백필 (2026-11-01)
--
-- 원인: 트레이너가 처음 배정될 때 trainer_members.member_type이 'ONE_POINT'로 기본값
-- 저장되고, 그 뒤 관리자가 PT 세션을 등록/지급해도 이 컬럼을 따로 안 건드려서 트레이너
-- 앱의 "내 회원" 화면에서는 계속 "원포인트 회원"으로 보이는 버그가 있었음. 코드 수정
-- (MyBatisMemberService/PtApiController/MyBatisMemberImportService가 PT 지급 시
-- markPtMemberType 호출, TrainerService.assignTrainerByMember가 최초 배정 시 실제
-- PT 보유량을 확인)은 이후 신규 발생을 막지만, 이미 잘못 저장된 기존 행은 따로 고쳐야 함.
--
-- 적용 전 확인(영향 받는 행 미리보기):
SELECT tm.member_id, tm.trainer_id, tm.member_type,
       up.pt_sessions_left, up.service_pt_sessions_left
FROM trainer_members tm
JOIN user_profiles up ON up.user_id = tm.member_id
WHERE tm.member_type <> 'PT'
  AND (COALESCE(up.pt_sessions_left, 0) + COALESCE(up.service_pt_sessions_left, 0)) > 0;

-- 실제 백필:
UPDATE trainer_members tm
JOIN user_profiles up ON up.user_id = tm.member_id
SET tm.member_type = 'PT'
WHERE tm.member_type <> 'PT'
  AND (COALESCE(up.pt_sessions_left, 0) + COALESCE(up.service_pt_sessions_left, 0)) > 0;
