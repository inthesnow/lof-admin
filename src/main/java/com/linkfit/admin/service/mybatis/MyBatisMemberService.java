package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Member;
import com.linkfit.admin.mapper.MemberMapper;
import com.linkfit.admin.service.MemberService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Profile("dev")
public class MyBatisMemberService implements MemberService {

    private final MemberMapper memberMapper;

    public MyBatisMemberService(MemberMapper memberMapper) {
        this.memberMapper = memberMapper;
    }

    @Override
    public List<Member> findAll(String keyword, String status, int page, int size) {
        return memberMapper.findAll(keyword, status, page * size, size);
    }

    @Override
    public long count(String keyword, String status) {
        return memberMapper.count(keyword, status);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return memberMapper.findById(id);
    }

    @Override
    public Member save(Member member) {
        memberMapper.insertUser(member);
        memberMapper.insertProfile(member);
        return member;
    }

    @Override
    public Member update(Long id, Member member) {
        member.setId(id);
        memberMapper.update(member);
        return member;
    }

    @Override
    public void delete(Long id) {
        memberMapper.delete(id);
    }

    @Override
    public void updateStatus(Long id, String status) {
        memberMapper.updateStatus(id, "ACTIVE".equals(status) ? 1 : 0);
    }

    @Override
    public void freeze(Long id, String startDate, String endDate) {
        memberMapper.insertFreeze(id, startDate, endDate, null);
        memberMapper.updateStatus(id, 0);
    }
}
