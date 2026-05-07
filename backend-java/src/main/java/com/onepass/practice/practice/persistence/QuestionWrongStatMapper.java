package com.onepass.practice.practice.persistence;

import java.util.List;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionWrongStatMapper {

    QuestionWrongStatDO selectByStudentAndQuestion(
            @Param("studentId") Long studentId,
            @Param("questionId") String questionId
    );

    List<QuestionWrongStatDO> selectByStudentAndQuestionIds(
            @Param("studentId") Long studentId,
            @Param("questionIds") List<String> questionIds
    );

    List<WrongBookQuestionRowDO> selectTopWrongQuestions(
            @Param("studentId") Long studentId,
            @Param("limit") Integer limit
    );

    int upsertIncrement(
            @Param("studentId") Long studentId,
            @Param("questionId") String questionId,
            @Param("answeredDelta") Integer answeredDelta,
            @Param("wrongDelta") Integer wrongDelta,
            @Param("lastAnsweredAt") LocalDateTime lastAnsweredAt,
            @Param("lastWrongAt") LocalDateTime lastWrongAt
    );
}
