package com.onepass.practice.contentimport;

import java.util.List;

public record AdminCategoryListView(
        List<AdminCategoryView> categories,
        List<AdminCategoryView> leafCategories
) {
}
