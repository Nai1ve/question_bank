package com.onepass.practice.recite.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ReciteDayRecordMapper {

    int insert(ReciteDayRecordDO item);

    ReciteDayRecordDO selectByIdAndStudentId(
            @Param("id") Long id,
            @Param("studentId") Long studentId
    );

    ReciteDayRecordDO selectLatestByPlanDayIdAndStudentId(
            @Param("planDayId") Long planDayId,
            @Param("studentId") Long studentId
    );
}
