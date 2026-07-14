package com.onepass.practice.contentimport;

import com.onepass.practice.common.ApiResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/import/vocabulary")
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class VocabularyImportAdminController {

    private final VocabularyImportService vocabularyImportService;
    private final AdminImportTokenService tokenService;

    public VocabularyImportAdminController(
            VocabularyImportService vocabularyImportService,
            AdminImportTokenService tokenService
    ) {
        this.vocabularyImportService = vocabularyImportService;
        this.tokenService = tokenService;
    }

    @PostMapping("/docx")
    public ApiResponse<VocabularyImportBatchView> uploadDocx(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @RequestParam("file") MultipartFile file
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(vocabularyImportService.uploadDocx(file));
    }

    @GetMapping("/{batchId}")
    public ApiResponse<VocabularyImportBatchView> getBatch(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(vocabularyImportService.getBatch(batchId));
    }

    @PostMapping("/{batchId}/confirm")
    public ApiResponse<VocabularyImportBatchView> confirm(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(vocabularyImportService.confirm(batchId));
    }

    @PostMapping("/{batchId}/cancel")
    public ApiResponse<VocabularyImportBatchView> cancel(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(vocabularyImportService.cancel(batchId));
    }

    private String resolveToken(String headerToken, String queryToken) {
        return StringUtils.hasText(headerToken) ? headerToken : queryToken;
    }
}
