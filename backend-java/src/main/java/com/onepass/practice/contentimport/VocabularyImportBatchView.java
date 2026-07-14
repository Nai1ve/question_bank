package com.onepass.practice.contentimport;

import java.util.List;

public record VocabularyImportBatchView(
        String batchId,
        String originalFilename,
        String status,
        String bookId,
        String bookName,
        String description,
        int totalCount,
        int importableCount,
        int errorCount,
        int warningCount,
        int importedCount,
        List<String> errors,
        List<String> warnings,
        List<VocabularyImportItemView> items
) {
}
