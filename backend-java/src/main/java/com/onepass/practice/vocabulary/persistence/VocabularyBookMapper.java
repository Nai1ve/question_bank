package com.onepass.practice.vocabulary.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface VocabularyBookMapper {

    List<VocabularyBookDO> selectActiveBooks();

    VocabularyBookDO selectById(@Param("id") String id);
}
