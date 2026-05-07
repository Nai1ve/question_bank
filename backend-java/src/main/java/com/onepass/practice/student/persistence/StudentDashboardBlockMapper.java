package com.onepass.practice.student.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentDashboardBlockMapper {

    List<StudentDashboardBlockDO> selectByTemplateId(@Param("templateId") Long templateId);
}
