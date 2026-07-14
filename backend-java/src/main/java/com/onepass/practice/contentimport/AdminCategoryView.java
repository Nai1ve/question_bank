package com.onepass.practice.contentimport;

public record AdminCategoryView(
        String id,
        String parentId,
        String name,
        String path,
        boolean leaf,
        int questionCount
) {
}
