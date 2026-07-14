package com.onepass.practice.contentimport;

import java.util.List;

public record QuestionImportBatchView(
        String batchId,
        String originalFilename,
        String status,
        int totalCount,
        int supportedCount,
        int unsupportedCount,
        int errorCount,
        int warningCount,
        int importedCount,
        String markdownPath,
        List<QuestionImportItemView> items
) {
}
