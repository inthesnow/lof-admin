package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.Message;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MessageMapper {
    List<Message> findAll(@Param("offset") int offset, @Param("size") int size);
    long count();
    Optional<Message> findById(@Param("id") Long id);
    void insert(Message message);
    void insertRecipients(@Param("messageId") Long messageId, @Param("memberIds") List<Long> memberIds);
    void delete(@Param("id") Long id);
}
