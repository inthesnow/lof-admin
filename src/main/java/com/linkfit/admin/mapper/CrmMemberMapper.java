package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.CrmMemberNote;
import com.linkfit.admin.domain.CrmMemberTag;
import com.linkfit.admin.domain.CrmMembershipHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CrmMemberMapper {

    // notes
    List<CrmMemberNote> findNotesByMemberId(@Param("memberId") String memberId, @Param("gymId") Long gymId);
    void insertNote(CrmMemberNote note);

    // tags
    List<CrmMemberTag> findTagsByMemberId(@Param("memberId") String memberId, @Param("gymId") Long gymId);
    void insertTag(CrmMemberTag tag);
    void deleteTag(@Param("tagId") String tagId, @Param("gymId") Long gymId);

    // trainer assignment
    Optional<String> findTrainerIdByMemberId(@Param("memberId") String memberId, @Param("gymId") Long gymId);
    void upsertTrainer(@Param("id") String id, @Param("memberId") String memberId,
                       @Param("trainerId") String trainerId, @Param("gymId") Long gymId);

    // membership history
    List<CrmMembershipHistory> findHistoryByMemberId(@Param("memberId") String memberId, @Param("gymId") Long gymId);
    void insertHistory(CrmMembershipHistory history);
}
