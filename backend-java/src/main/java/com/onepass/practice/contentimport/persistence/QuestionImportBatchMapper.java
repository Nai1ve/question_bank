package com.onepass.practice.contentimport.persistence;

import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionImportBatchMapper {

    int insert(QuestionImportBatchDO item);

    QuestionImportBatchDO selectByBatchId(@Param("batchId") String batchId);

    int updateImported(
            @Param("batchId") String batchId,
            @Param("status") String status,
            @Param("importedCount") int importedCount,
            @Param("importedAt") LocalDateTime importedAt
    );

    int updateCanceled(
            @Param("batchId") String batchId,
            @Param("status") String status,
            @Param("canceledAt") LocalDateTime canceledAt
    );
}
