package com.onepass.practice.contentimport;

import java.nio.file.Path;
import java.util.List;

public record MarkdownConversionResult(
        Path markdownPath,
        String markdownContent,
        List<ExtractedImportAsset> assets
) {
}
