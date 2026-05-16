package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.ClassSession;
import com.linkfit.admin.mapper.ClassMapper;
import com.linkfit.admin.service.ClassService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MyBatisClassService implements ClassService {

    private final ClassMapper classMapper;

    public MyBatisClassService(ClassMapper classMapper) {
        this.classMapper = classMapper;
    }

    @Override
    public List<ClassSession> findAll(String type, String date, int page, int size) {
        return classMapper.findAll(type, date, page * size, size);
    }

    @Override
    public long count(String type, String date) {
        return classMapper.count(type, date);
    }

    @Override
    public Optional<ClassSession> findById(Long id) {
        return classMapper.findById(id);
    }

    @Override
    public ClassSession save(ClassSession session) {
        classMapper.insert(session);
        return session;
    }

    @Override
    public ClassSession update(Long id, ClassSession session) {
        session.setId(id);
        classMapper.update(session);
        return session;
    }

    @Override
    public void cancel(Long id) {
        classMapper.cancel(id);
    }

    @Override
    public void enroll(Long classId, Long memberId) {
        classMapper.enroll(classId, memberId);
        classMapper.incrementEnrolled(classId);
    }

    @Override
    public void cancelEnrollment(Long classId, Long memberId) {
        classMapper.cancelEnrollment(classId, memberId);
        classMapper.decrementEnrolled(classId);
    }
}
