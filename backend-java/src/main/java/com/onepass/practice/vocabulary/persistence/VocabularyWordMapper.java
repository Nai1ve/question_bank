package com.onepass.practice.vocabulary.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VocabularyWordMapper {

    List<VocabularyWordDO> selectByBookId(@Param("bookId") String bookId);

    List<VocabularyWordDO> selectByBookIdAndSortRange(
            @Param("bookId") String bookId,
            @Param("startSortOrder") Integer startSortOrder,
            @Param("endSortOrder") Integer endSortOrder
    );

    int deleteByBookId(@Param("bookId") String bookId);

    int insert(VocabularyWordDO item);
}
