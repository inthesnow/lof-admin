package com.linkfit.admin.service.mock;

import com.linkfit.admin.domain.Message;
import com.linkfit.admin.service.MessageService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

// @Service (replaced by MyBatis implementation)
public class MockMessageService implements MessageService {

    private final Map<Long, Message> store = new LinkedHashMap<>();
    private final AtomicLong seq = new AtomicLong(1);

    public MockMessageService() {
        seed();
    }

    private void seed() {
        String[][] data = {
            {"회원권 만료 안내", "ALL", "SENT"},
            {"이번 주 수업 일정 안내", "ALL", "SENT"},
            {"신규 PT 프로모션", "MEMBER", "SENT"},
        };
        for (String[] row : data) {
            Message m = new Message();
            m.setId(seq.getAndIncrement());
            m.setTitle(row[0]);
            m.setContent("내용 준비 중");
            m.setTargetType(row[1]);
            m.setStatus(row[2]);
            m.setSentAt(LocalDateTime.now().minusDays(new Random().nextInt(7)));
            m.setRecipientCount(new Random().nextInt(100));
            m.setSenderName("김관리");
            store.put(m.getId(), m);
        }
    }

    @Override
    public List<Message> findAll(int page, int size) {
        return store.values().stream()
            .skip((long) page * size)
            .limit(size)
            .collect(Collectors.toList());
    }

    @Override
    public long count() { return store.size(); }

    @Override
    public Optional<Message> findById(Long id) { return Optional.ofNullable(store.get(id)); }

    @Override
    public Message send(Message message) {
        message.setId(seq.getAndIncrement());
        message.setStatus("SENT");
        message.setSentAt(LocalDateTime.now());
        store.put(message.getId(), message);
        return message;
    }

    @Override
    public void delete(Long id) { store.remove(id); }
}
