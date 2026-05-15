package com.linkfit.admin.service;

import com.linkfit.admin.domain.Member;
import java.util.List;
import java.util.Optional;

public interface MemberService {
    List<Member> findAll(String keyword, String status, int page, int size);
    long count(String keyword, String status);
    Optional<Member> findById(Long id);
    Member save(Member member);
    Member update(Long id, Member member);
    void delete(Long id);
    void updateStatus(Long id, String status);
    void freeze(Long id, String startDate, String endDate);
}
