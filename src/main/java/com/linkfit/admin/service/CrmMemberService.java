package com.linkfit.admin.service;

import com.linkfit.admin.domain.CrmMemberNote;
import com.linkfit.admin.domain.CrmMemberTag;
import com.linkfit.admin.domain.CrmMembershipHistory;

import java.util.List;

public interface CrmMemberService {

    List<CrmMemberNote> findNotes(String memberId, Long gymId);
    CrmMemberNote addNote(String memberId, Long gymId, String authorId, String content);

    List<CrmMemberTag> findTags(String memberId, Long gymId);
    CrmMemberTag addTag(String memberId, Long gymId, String tag, String color);
    void deleteTag(String tagId, Long gymId);

    void assignTrainer(String memberId, Long gymId, String trainerId);

    List<CrmMembershipHistory> findMembershipHistory(String memberId, Long gymId);
    void recordMembershipAction(String memberId, Long gymId, String action, String reason, String processedBy);
}
