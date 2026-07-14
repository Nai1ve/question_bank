package com.onepass.practice.contentimport;

import com.onepass.practice.common.ApiResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@RequestMapping("/api/admin/import/questions")
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class QuestionImportAdminController {

    private final QuestionImportService questionImportService;
    private final AdminImportTokenService tokenService;

    public QuestionImportAdminController(
            QuestionImportService questionImportService,
            AdminImportTokenService tokenService
    ) {
        this.questionImportService = questionImportService;
        this.tokenService = tokenService;
    }

    @PostMapping("/docx")
    public ApiResponse<QuestionImportBatchView> uploadDocx(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "categoryPath", required = false) String categoryPath
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(questionImportService.uploadDocx(file, categoryPath));
    }

    @GetMapping("/{batchId}")
    public ApiResponse<QuestionImportBatchView> getBatch(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(questionImportService.getBatch(batchId));
    }

    @GetMapping(value = "/{batchId}/markdown", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> downloadMarkdown(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        String markdown = questionImportService.readMarkdown(batchId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, ContentDisposition.attachment()
                        .filename("questions-" + batchId + ".md")
                        .build()
                        .toString())
                .contentType(MediaType.TEXT_PLAIN)
                .body(markdown);
    }

    @PostMapping("/{batchId}/confirm")
    public ApiResponse<QuestionImportBatchView> confirm(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(questionImportService.confirm(batchId));
    }

    @PostMapping("/{batchId}/cancel")
    public ApiResponse<QuestionImportBatchView> cancel(
            @RequestHeader(value = "X-Admin-Import-Token", required = false) String headerToken,
            @RequestParam(value = "token", required = false) String queryToken,
            @PathVariable String batchId
    ) {
        tokenService.requireValidToken(resolveToken(headerToken, queryToken));
        return ApiResponse.ok(questionImportService.cancel(batchId));
    }

    private String resolveToken(String headerToken, String queryToken) {
        return StringUtils.hasText(headerToken) ? headerToken : queryToken;
    }
}
