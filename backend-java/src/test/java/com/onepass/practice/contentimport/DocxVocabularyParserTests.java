package com.onepass.practice.contentimport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

class DocxVocabularyParserTests {

    private final DocxVocabularyParser parser = new DocxVocabularyParser();

    @Test
    void parsesImportWordsDocxSample() throws IOException {
        Path sample = findSample();
        assertTrue(Files.exists(sample), "import_words.docx sample must exist");

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import_words.docx",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                Files.readAllBytes(sample)
        );

        ParsedVocabularyDocument document = parser.parse(file);

        assertTrue(document.errors().isEmpty(), () -> "Document errors: " + document.errors());
        assertEquals("kaoyan-test-vocab", document.bookId());
        assertEquals("考研测试词库", document.bookName());
        assertEquals(20, document.words().size());
        assertEquals("abandon", document.words().get(0).english());
        assertEquals("evaluate", document.words().get(19).english());
        assertTrue(document.words().stream().allMatch(word -> word.errors().isEmpty()));
    }

    private Path findSample() {
        Path cwd = Path.of("").toAbsolutePath();
        List<Path> candidates = List.of(
                cwd.resolve("docs/prototypes/vocabulary/import_words.docx"),
                cwd.resolve("../docs/prototypes/vocabulary/import_words.docx")
        );
        return candidates.stream()
                .filter(Files::exists)
                .findFirst()
                .orElse(candidates.get(0));
    }
}
