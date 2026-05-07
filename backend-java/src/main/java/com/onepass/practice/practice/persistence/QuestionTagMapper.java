package com.onepass.practice.practice.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface QuestionTagMapper {

    List<QuestionTagDO> selectAll();

    List<QuestionTagSummaryDO> selectAllTagSummaries();

    List<QuestionTagSummaryDO> selectTagSummariesByCategoryIds(@Param("categoryIds") List<String> categoryIds);
}
