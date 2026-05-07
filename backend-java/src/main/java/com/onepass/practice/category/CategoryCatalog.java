package com.onepass.practice.category;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onepass.practice.category.persistence.CategoryDO;
import com.onepass.practice.category.persistence.CategoryMapper;
import com.onepass.practice.common.JsonResourceReader;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CategoryCatalog {

    private static final Logger log = LoggerFactory.getLogger(CategoryCatalog.class);

    private final boolean mockEnabled;
    private final List<CategoryNode> categoryNodes;
    private final CategoryMapper categoryMapper;

    public CategoryCatalog(
            @Value("${app.mock.enabled:true}") boolean mockEnabled,
            JsonResourceReader jsonResourceReader,
            ObjectProvider<CategoryMapper> categoryMapperProvider
    ) {
        this.mockEnabled = mockEnabled;
        this.categoryMapper = categoryMapperProvider.getIfAvailable();
        this.categoryNodes = List.copyOf(
                jsonResourceReader.read("mock-data/categories.json", new TypeReference<List<CategoryNode>>() {
                })
        );
        log.info("Loaded category catalog mode={} count={}", mockEnabled ? "resource" : "mysql", categoryNodes.size());
    }

    public List<CategoryNode> listAll() {
        if (!mockEnabled) {
            if (categoryMapper == null) {
                throw new IllegalStateException("CategoryMapper is not available");
            }
            return categoryMapper.selectAll().stream()
                    .map(this::toCategoryNode)
                    .toList();
        }
        return categoryNodes;
    }

    private CategoryNode toCategoryNode(CategoryDO item) {
        return new CategoryNode(
                item.getId(),
                item.getParentId(),
                item.getName(),
                item.getSubtitle(),
                Boolean.TRUE.equals(item.getLeaf())
        );
    }
}
