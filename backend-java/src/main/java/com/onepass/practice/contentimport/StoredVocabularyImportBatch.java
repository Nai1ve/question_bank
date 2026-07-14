package com.onepass.practice.contentimport;

public record StoredVocabularyImportBatch(
        String batchId,
        String originalFilename,
        String status,
        String storageDir,
        ParsedVocabularyDocument document,
        int importedCount
) {
}
