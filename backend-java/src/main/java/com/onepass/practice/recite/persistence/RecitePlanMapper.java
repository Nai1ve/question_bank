package com.onepass.practice.recite.persistence;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecitePlanMapper {

    RecitePlanDO selectActiveByStudentId(@Param("studentId") Long studentId);

    RecitePlanDO selectByIdAndStudentId(
            @Param("id") Long id,
            @Param("studentId") Long studentId
    );

    int supersedeActivePlans(
            @Param("studentId") Long studentId,
            @Param("supersededAt") LocalDateTime supersededAt
    );

    int insert(RecitePlanDO item);
}
