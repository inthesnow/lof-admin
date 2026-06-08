package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.FeedbackRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedbackRequestMapper {

    List<FeedbackRequest> findAll(@Param("gymId") Long gymId, @Param("status") String status,
                                   @Param("trainerId") String trainerId,
                                   @Param("offset") int offset, @Param("size") int size);
    long count(@Param("gymId") Long gymId, @Param("status") String status, @Param("trainerId") String trainerId);

    Optional<FeedbackRequest> findById(@Param("id") String id);
    void insert(FeedbackRequest request);
    void assignTrainer(@Param("id") String id, @Param("trainerId") String trainerId);
    void respond(@Param("id") String id, @Param("response") String response);
    void updateStatus(@Param("id") String id, @Param("status") String status);
}
