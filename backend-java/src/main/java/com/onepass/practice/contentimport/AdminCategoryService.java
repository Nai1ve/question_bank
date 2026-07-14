package com.onepass.practice.contentimport;

import com.onepass.practice.category.persistence.CategoryDO;
import com.onepass.practice.category.persistence.CategoryMapper;
import com.onepass.practice.common.AppException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class AdminCategoryService {

    private final CategoryMapper categoryMapper;

    public AdminCategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public AdminCategoryListView listCategories() {
        List<CategoryDO> categories = categoryMapper.selectAll();
        Map<String, CategoryDO> idMap = buildIdMap(categories);
        List<AdminCategoryView> views = categories.stream()
                .map(category -> toView(category, idMap))
                .sorted(Comparator.comparing(AdminCategoryView::path))
                .toList();
        List<AdminCategoryView> leafViews = views.stream()
                .filter(AdminCategoryView::leaf)
                .toList();
        return new AdminCategoryListView(views, leafViews);
    }

    @Transactional
    public AdminCategoryView createCategory(AdminCategoryCreateRequest request) {
        List<String> segments = parsePath(request == null ? null : request.path());
        List<CategoryDO> categories = categoryMapper.selectAll();
        Map<String, CategoryDO> childMap = buildChildMap(categories);

        String parentId = null;
        CategoryDO current = null;
        for (int index = 0; index < segments.size(); index += 1) {
            String name = segments.get(index);
            boolean last = index == segments.size() - 1;
            CategoryDO existing = childMap.get(childKey(parentId, name));
            if (existing != null) {
                if (!last && Boolean.TRUE.equals(existing.getLeaf())) {
                    int questionCount = categoryMapper.countQuestions(existing.getId());
                    if (questionCount > 0) {
                        throw new AppException("分类已有题目，不能改为父分类：" + name);
                    }
                    categoryMapper.updateLeaf(existing.getId(), false);
                    existing.setLeaf(false);
                }
                current = existing;
                parentId = existing.getId();
                continue;
            }

            CategoryDO created = new CategoryDO();
            created.setId(generateCategoryId());
            created.setParentId(parentId);
            created.setName(name);
            created.setSubtitle(resolveSubtitle(last, request == null ? null : request.subtitle()));
            created.setLeaf(last);
            created.setSortOrder(nextSortOrder(parentId));
            categoryMapper.insert(created);
            childMap.put(childKey(parentId, name), created);
            current = created;
            parentId = created.getId();
        }

        if (current == null) {
            throw new AppException("分类创建失败");
        }
        if (!Boolean.TRUE.equals(current.getLeaf())) {
            throw new AppException("该路径已存在但不是叶子分类");
        }
        return toView(current, buildIdMap(categoryMapper.selectAll()));
    }

    @Transactional
    public AdminCategoryListView deleteCategory(String categoryId) {
        CategoryDO category = categoryMapper.selectById(categoryId);
        if (category == null) {
            throw new AppException("分类不存在");
        }
        if (!Boolean.TRUE.equals(category.getLeaf())) {
            throw new AppException("只能删除叶子分类");
        }
        if (categoryMapper.countChildren(categoryId) > 0) {
            throw new AppException("分类还有子分类，不能删除");
        }
        if (categoryMapper.countQuestions(categoryId) > 0) {
            throw new AppException("分类已有题目，不能删除");
        }
        categoryMapper.deleteById(categoryId);
        return listCategories();
    }

    private List<String> parsePath(String path) {
        if (!StringUtils.hasText(path)) {
            throw new AppException("请填写分类路径");
        }
        List<String> segments = new ArrayList<>();
        for (String part : path.split("/")) {
            String value = part.trim();
            if (!value.isBlank()) {
                segments.add(value);
            }
        }
        if (segments.isEmpty()) {
            throw new AppException("请填写分类路径");
        }
        if (segments.size() > 3) {
            throw new AppException("分类树最多 3 层");
        }
        return segments;
    }

    private String resolveSubtitle(boolean leaf, String subtitle) {
        if (leaf && StringUtils.hasText(subtitle)) {
            return subtitle.trim();
        }
        return leaf ? "叶子分类" : "导入分类";
    }

    private int nextSortOrder(String parentId) {
        Integer maxSortOrder = categoryMapper.selectMaxSortOrderByParentId(parentId);
        return (maxSortOrder == null ? 0 : maxSortOrder) + 10;
    }

    private AdminCategoryView toView(CategoryDO category, Map<String, CategoryDO> idMap) {
        return new AdminCategoryView(
                category.getId(),
                category.getParentId(),
                category.getName(),
                buildPath(category, idMap),
                Boolean.TRUE.equals(category.getLeaf()),
                categoryMapper.countQuestions(category.getId())
        );
    }

    private Map<String, CategoryDO> buildIdMap(List<CategoryDO> categories) {
        Map<String, CategoryDO> idMap = new HashMap<>();
        for (CategoryDO category : categories) {
            idMap.put(category.getId(), category);
        }
        return idMap;
    }

    private Map<String, CategoryDO> buildChildMap(List<CategoryDO> categories) {
        Map<String, CategoryDO> childMap = new HashMap<>();
        for (CategoryDO category : categories) {
            childMap.put(childKey(category.getParentId(), category.getName()), category);
        }
        return childMap;
    }

    private String childKey(String parentId, String name) {
        return (parentId == null ? "" : parentId) + "\u0000" + name;
    }

    private String buildPath(CategoryDO category, Map<String, CategoryDO> idMap) {
        List<String> names = new ArrayList<>();
        CategoryDO current = category;
        while (current != null) {
            names.add(0, current.getName());
            current = StringUtils.hasText(current.getParentId()) ? idMap.get(current.getParentId()) : null;
        }
        return String.join("/", names);
    }

    private String generateCategoryId() {
        return "cat-" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);
    }
}
