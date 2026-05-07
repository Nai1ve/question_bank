package com.onepass.practice.category;

import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

    private final CategoryCatalog categoryCatalog;

    public CategoryService(CategoryCatalog categoryCatalog) {
        this.categoryCatalog = categoryCatalog;
    }

    public List<CategoryView> listByParentId(String parentId) {
        return categoryCatalog.listAll().stream()
                .filter(node -> parentId == null ? node.parentId() == null : parentId.equals(node.parentId()))
                .map(node -> new CategoryView(node.id(), node.name(), node.subtitle(), node.isLeaf()))
                .toList();
    }
}
