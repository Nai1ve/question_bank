package com.onepass.practice.student.persistence;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StudentDashboardTemplateMapper {

    StudentDashboardTemplateDO selectActiveTemplate();
}
