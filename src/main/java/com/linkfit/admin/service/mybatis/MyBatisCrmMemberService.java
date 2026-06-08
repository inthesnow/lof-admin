package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.CrmMemberNote;
import com.linkfit.admin.domain.CrmMemberTag;
import com.linkfit.admin.domain.CrmMembershipHistory;
import com.linkfit.admin.mapper.CrmMemberMapper;
import com.linkfit.admin.service.CrmMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MyBatisCrmMemberService implements CrmMemberService {

    private final CrmMemberMapper mapper;

    public MyBatisCrmMemberService(CrmMemberMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public List<CrmMemberNote> findNotes(String memberId, Long gymId) {
        return mapper.findNotesByMemberId(memberId, gymId);
    }

    @Override
    public CrmMemberNote addNote(String memberId, Long gymId, String authorId, String content) {
        CrmMemberNote note = new CrmMemberNote();
        note.setId(UUID.randomUUID().toString());
        note.setMemberId(memberId);
        note.setGymId(gymId);
        note.setAuthorId(authorId);
        note.setContent(content);
        mapper.insertNote(note);
        return note;
    }

    @Override
    public List<CrmMemberTag> findTags(String memberId, Long gymId) {
        return mapper.findTagsByMemberId(memberId, gymId);
    }

    @Override
    public CrmMemberTag addTag(String memberId, Long gymId, String tag, String color) {
        CrmMemberTag t = new CrmMemberTag();
        t.setId(UUID.randomUUID().toString());
        t.setMemberId(memberId);
        t.setGymId(gymId);
        t.setTag(tag);
        t.setColor(color);
        mapper.insertTag(t);
        return t;
    }

    @Override
    public void deleteTag(String tagId, Long gymId) {
        mapper.deleteTag(tagId, gymId);
    }

    @Override
    public void assignTrainer(String memberId, Long gymId, String trainerId) {
        mapper.upsertTrainer(UUID.randomUUID().toString(), memberId, trainerId, gymId);
    }

    @Override
    public List<CrmMembershipHistory> findMembershipHistory(String memberId, Long gymId) {
        return mapper.findHistoryByMemberId(memberId, gymId);
    }

    @Override
    public void recordMembershipAction(String memberId, Long gymId, String action, String reason, String processedBy) {
        CrmMembershipHistory h = new CrmMembershipHistory();
        h.setId(UUID.randomUUID().toString());
        h.setMemberId(memberId);
        h.setGymId(gymId);
        h.setAction(action);
        h.setReason(reason);
        h.setProcessedBy(processedBy);
        mapper.insertHistory(h);
    }
}
