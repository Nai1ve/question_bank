package com.onepass.practice.category;

import com.onepass.practice.common.ApiResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/categories")
public class StudentCategoryController {

    private static final Logger log = LoggerFactory.getLogger(StudentCategoryController.class);

    private final CategoryService categoryService;

    public StudentCategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ApiResponse<List<CategoryView>> listCategories(
            @RequestParam(required = false) String parentId
    ) {
        List<CategoryView> categories = categoryService.listByParentId(parentId);
        log.info("Listed categories parentId={} count={}", parentId == null ? "" : parentId, categories.size());
        return ApiResponse.ok(categories);
    }
}
