package com.onepass.practice.contentimport.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionImportItemMapper {

    int insert(QuestionImportItemDO item);

    List<QuestionImportItemDO> selectByBatchId(@Param("batchId") String batchId);

    List<QuestionImportItemDO> selectImportableByBatchId(@Param("batchId") String batchId);

    int updateImported(
            @Param("id") Long id,
            @Param("status") String status,
            @Param("targetQuestionId") String targetQuestionId
    );
}
