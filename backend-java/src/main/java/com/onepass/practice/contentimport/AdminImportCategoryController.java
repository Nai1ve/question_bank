package com.onepass.practice.contentimport;

import com.onepass.practice.common.ApiResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/import/categories")
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class AdminImportCategoryController {

    private final AdminCategoryService adminCategoryService;
    private final AdminImportTokenService tokenService;

    public AdminImportCategoryController(
            AdminCategoryService adminCategoryService,
            AdminImportTokenService tokenService
    ) {
        this.adminCategoryService = adminCategoryService;
        this.tokenService = tokenService;
    }

    @GetMapping
    public ApiResponse<AdminCategoryListView> list(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(adminCategoryService.listCategories());
    }

    @PostMapping
    public ApiResponse<AdminCategoryView> create(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @RequestBody AdminCategoryCreateRequest request
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(adminCategoryService.createCategory(request));
    }

    @DeleteMapping("/{categoryId}")
    public ApiResponse<AdminCategoryListView> delete(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String categoryId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(adminCategoryService.deleteCategory(categoryId));
    }

    private String resolveToken(String headerToken, String queryToken) {
        return StringUtils.hasText(headerToken) ? headerToken : queryToken;
    }
}
