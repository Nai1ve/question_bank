package com.onepass.practice.practice.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PracticeSessionQuestionMapper {

    List<PracticeSessionQuestionDO> selectBySessionId(@Param("sessionId") String sessionId);

    int batchInsert(@Param("items") List<PracticeSessionQuestionDO> items);

    int updateAnswerSnapshot(
            @Param("sessionId") String sessionId,
            @Param("questionId") String questionId,
            @Param("userAnswerJson") String userAnswerJson,
            @Param("userAnswerLabel") String userAnswerLabel,
            @Param("answerLabel") String answerLabel,
            @Param("submitted") boolean submitted,
            @Param("correct") boolean correct,
            @Param("version") Long version
    );
}
