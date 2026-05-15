package com.linkfit.admin.service;

import com.linkfit.admin.domain.Message;
import java.util.List;
import java.util.Optional;

public interface MessageService {
    List<Message> findAll(int page, int size);
    long count();
    Optional<Message> findById(Long id);
    Message send(Message message);
    void delete(Long id);
}
