package com.onepass.practice.category.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CategoryMapper {

    List<CategoryDO> selectAll();
}
