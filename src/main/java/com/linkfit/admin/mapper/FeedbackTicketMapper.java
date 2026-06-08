package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.FeedbackTicket;
import com.linkfit.admin.domain.TicketSettings;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface FeedbackTicketMapper {

    List<FeedbackTicket> findAll(@Param("gymId") Long gymId, @Param("monthYear") String monthYear,
                                  @Param("status") String status,
                                  @Param("offset") int offset, @Param("size") int size);
    long count(@Param("gymId") Long gymId, @Param("monthYear") String monthYear, @Param("status") String status);

    List<FeedbackTicket> findByMemberId(@Param("memberId") String memberId, @Param("gymId") Long gymId);
    void insert(FeedbackTicket ticket);
    void updateStatus(@Param("id") String id, @Param("status") String status);
    void assignTrainer(@Param("id") String id, @Param("trainerId") String trainerId);

    int expireOverdue();
    boolean existsForMonth(@Param("memberId") String memberId,
                            @Param("gymId") Long gymId,
                            @Param("monthYear") String monthYear);

    // usage summary counts for Sector 8
    int countByStatus(@Param("gymId") Long gymId, @Param("monthYear") String monthYear, @Param("status") String status);

    // Ticket settings (Sector 8)
    Optional<TicketSettings> findSettingsByGymId(@Param("gymId") Long gymId);
    void upsertSettings(TicketSettings settings);
}
