package com.onepass.practice.student.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentProfileMapper {

    StudentProfileDO selectById(@Param("id") Long id);
}
