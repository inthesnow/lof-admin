package com.linkfit.admin.mapper;

import com.linkfit.admin.domain.CrmAnnouncement;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface CrmAnnouncementMapper {

    List<CrmAnnouncement> findAll(@Param("gymId") Long gymId,
                                   @Param("target") String target,
                                   @Param("offset") int offset, @Param("size") int size);
    long count(@Param("gymId") Long gymId, @Param("target") String target);

    Optional<CrmAnnouncement> findById(@Param("id") String id);

    void insert(CrmAnnouncement announcement);
    void markSent(@Param("id") String id);
    void delete(@Param("id") String id);
}
