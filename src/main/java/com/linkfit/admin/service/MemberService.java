package com.linkfit.admin.service;

import com.linkfit.admin.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    List<Member> findAll(String keyword, String status, int page, int size);
    long count(String keyword, String status);
    Optional<Member> findById(String id);
    Member save(Member member);
    Member update(String id, Member member);
    void delete(String id);
    void updateStatus(String id, String status);
    void updateTier(String id, String tier);
    void updateMemberType(String id, String memberType);
    void freeze(String id, String startDate, String endDate);
}
