package com.onepass.practice.recite.persistence;

import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface RecitePlanDayMapper {

    List<RecitePlanDayDO> selectByPlanId(@Param("planId") Long planId);

    RecitePlanDayDO selectByPlanIdAndDayNumber(
            @Param("planId") Long planId,
            @Param("dayNumber") Integer dayNumber
    );

    int countCompletedDays(@Param("planId") Long planId);

    int insert(RecitePlanDayDO item);

    int updateStudyCompletedAt(
            @Param("id") Long id,
            @Param("studyCompletedAt") LocalDateTime studyCompletedAt
    );

    int updateCompletion(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("lastAccuracy") String lastAccuracy,
            @Param("lastCorrectCount") Integer lastCorrectCount,
            @Param("lastWrongCount") Integer lastWrongCount,
            @Param("completedAt") LocalDateTime completedAt
    );
}
