package com.onepass.practice.practice.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionAnswerMapper {

    List<QuestionAnswerDO> selectAll();
}
