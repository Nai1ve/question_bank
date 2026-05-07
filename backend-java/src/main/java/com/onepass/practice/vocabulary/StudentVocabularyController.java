package com.onepass.practice.vocabulary;

import com.onepass.practice.common.ApiResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/vocabulary/books")
public class StudentVocabularyController {

    private static final Logger log = LoggerFactory.getLogger(StudentVocabularyController.class);

    private final VocabularyService vocabularyService;

    public StudentVocabularyController(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    @GetMapping
    public ApiResponse<List<VocabularyBookView>> listBooks() {
        List<VocabularyBookView> books = vocabularyService.listBooks();
        log.info("Listed vocabulary books count={}", books.size());
        return ApiResponse.ok(books);
    }
}
