package com.onepass.practice.contentimport.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionAssetMapper {

    int insert(QuestionAssetDO item);

    QuestionAssetDO selectByQuestionIdAndRelativePath(
            @Param("questionId") String questionId,
            @Param("relativePath") String relativePath
    );

    int updateQuestionIdByBatchAndRelativePath(
            @Param("batchId") String batchId,
            @Param("relativePath") String relativePath,
            @Param("questionId") String questionId
    );
}
