package com.onepass.practice.vocabulary;

import com.onepass.practice.common.AppException;
import com.onepass.practice.vocabulary.persistence.VocabularyBookDO;
import com.onepass.practice.vocabulary.persistence.VocabularyBookMapper;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

@Service
public class VocabularyService {

    private final VocabularyBookMapper vocabularyBookMapper;

    public VocabularyService(ObjectProvider<VocabularyBookMapper> vocabularyBookMapperProvider) {
        this.vocabularyBookMapper = vocabularyBookMapperProvider.getIfAvailable();
    }

    public List<VocabularyBookView> listBooks() {
        if (vocabularyBookMapper == null) {
            throw new AppException("Vocabulary book data is not available");
        }

        return vocabularyBookMapper.selectActiveBooks().stream()
                .map(this::toView)
                .toList();
    }

    private VocabularyBookView toView(VocabularyBookDO item) {
        return new VocabularyBookView(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getTotalWords() == null ? 0 : item.getTotalWords()
        );
    }
}
