package com.onepass.practice.contentimport;

public record ExtractedImportAsset(
        String originalName,
        String relativePath,
        String contentType,
        long fileSize
) {
}
