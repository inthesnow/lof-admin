package com.linkfit.admin.service.mybatis;

import com.linkfit.admin.domain.Message;
import com.linkfit.admin.mapper.MessageMapper;
import com.linkfit.admin.service.MessageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Profile("dev")
public class MyBatisMessageService implements MessageService {

    private final MessageMapper messageMapper;

    public MyBatisMessageService(MessageMapper messageMapper) {
        this.messageMapper = messageMapper;
    }

    @Override
    public List<Message> findAll(int page, int size) {
        return messageMapper.findAll(page * size, size);
    }

    @Override
    public long count() {
        return messageMapper.count();
    }

    @Override
    public Optional<Message> findById(Long id) {
        return messageMapper.findById(id);
    }

    @Override
    public Message send(Message message) {
        message.setStatus("SENT");
        message.setSentAt(LocalDateTime.now());
        messageMapper.insert(message);
        return message;
    }

    @Override
    public void delete(Long id) {
        messageMapper.delete(id);
    }
}
