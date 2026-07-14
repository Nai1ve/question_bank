package com.onepass.practice.category.persistence;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CategoryMapper {

    List<CategoryDO> selectAll();

    CategoryDO selectById(@Param("id") String id);

    Integer selectMaxSortOrderByParentId(@Param("parentId") String parentId);

    int countChildren(@Param("parentId") String parentId);

    int countQuestions(@Param("categoryId") String categoryId);

    int insert(CategoryDO item);

    int updateLeaf(
            @Param("id") String id,
            @Param("leaf") boolean leaf
    );

    int deleteById(@Param("id") String id);
}
