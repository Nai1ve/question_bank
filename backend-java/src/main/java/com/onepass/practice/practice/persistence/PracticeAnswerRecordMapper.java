package com.onepass.practice.practice.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PracticeAnswerRecordMapper {

    Integer selectMaxSubmitSeq(
            @Param("sessionId") String sessionId,
            @Param("questionId") String questionId
    );

    int insert(PracticeAnswerRecordDO record);
}
