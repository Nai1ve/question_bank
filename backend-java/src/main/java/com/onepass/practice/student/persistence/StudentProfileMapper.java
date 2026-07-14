package com.onepass.practice.student.persistence;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface StudentProfileMapper {

    StudentProfileDO selectById(@Param("id") Long id);

    StudentProfileDO selectByWechatOpenid(@Param("wechatOpenid") String wechatOpenid);

    int insert(StudentProfileDO profile);

    int updateWechatUnionid(@Param("id") Long id, @Param("wechatUnionid") String wechatUnionid);
}
