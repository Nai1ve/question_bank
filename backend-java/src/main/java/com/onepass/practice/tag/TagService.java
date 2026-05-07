package com.onepass.practice.tag;

import com.onepass.practice.category.CategoryCatalog;
import com.onepass.practice.category.CategoryNode;
import com.onepass.practice.practice.PracticeQuestionCatalog;
import com.onepass.practice.practice.PracticeQuestionDefinition;
import com.onepass.practice.practice.persistence.QuestionTagMapper;
import com.onepass.practice.practice.persistence.QuestionTagSummaryDO;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class TagService {

    private final boolean mockEnabled;
    private final CategoryCatalog categoryCatalog;
    private final PracticeQuestionCatalog practiceQuestionCatalog;
    private final QuestionTagMapper questionTagMapper;

    public TagService(
            @Value("${app.mock.enabled:true}") boolean mockEnabled,
            CategoryCatalog categoryCatalog,
            PracticeQuestionCatalog practiceQuestionCatalog,
            ObjectProvider<QuestionTagMapper> questionTagMapperProvider
    ) {
        this.mockEnabled = mockEnabled;
        this.categoryCatalog = categoryCatalog;
        this.practiceQuestionCatalog = practiceQuestionCatalog;
        this.questionTagMapper = questionTagMapperProvider.getIfAvailable();
    }

    public List<TagView> listTags(String categoryId) {
        String normalizedCategoryId = normalizeCategoryId(categoryId);
        return mockEnabled ? listMockTags(normalizedCategoryId) : listMysqlTags(normalizedCategoryId);
    }

    private List<TagView> listMockTags(String categoryId) {
        Map<String, Integer> counts = new HashMap<>();
        for (PracticeQuestionDefinition question : practiceQuestionCatalog.listAll()) {
            if (StringUtils.hasText(categoryId) && !question.categoryPathIds().contains(categoryId)) {
                continue;
            }

            Set<String> uniqueTags = new LinkedHashSet<>(question.tags());
            for (String tag : uniqueTags) {
                counts.merge(tag, 1, Integer::sum);
            }
        }

        return counts.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder())
                        .thenComparing(Map.Entry.comparingByKey()))
                .map(entry -> new TagView(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<TagView> listMysqlTags(String categoryId) {
        if (questionTagMapper == null) {
            throw new IllegalStateException("QuestionTagMapper is not available");
        }

        List<QuestionTagSummaryDO> rows;
        if (!StringUtils.hasText(categoryId)) {
            rows = questionTagMapper.selectAllTagSummaries();
        } else {
            List<String> scopeIds = resolveScopeCategoryIds(categoryId);
            if (scopeIds.isEmpty()) {
                return List.of();
            }
            rows = questionTagMapper.selectTagSummariesByCategoryIds(scopeIds);
        }

        return rows.stream()
                .map(item -> new TagView(item.getTagName(), item.getQuestionCount() == null ? 0 : item.getQuestionCount()))
                .toList();
    }

    private List<String> resolveScopeCategoryIds(String categoryId) {
        Map<String, List<String>> childrenMap = new HashMap<>();
        Set<String> knownCategoryIds = new LinkedHashSet<>();
        for (CategoryNode node : categoryCatalog.listAll()) {
            knownCategoryIds.add(node.id());
            String parentId = node.parentId();
            if (parentId != null) {
                childrenMap.computeIfAbsent(parentId, key -> new ArrayList<>()).add(node.id());
            }
        }

        if (!knownCategoryIds.contains(categoryId)) {
            return List.of();
        }

        List<String> result = new ArrayList<>();
        ArrayDeque<String> queue = new ArrayDeque<>();
        queue.add(categoryId);
        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            result.add(current);
            for (String childId : childrenMap.getOrDefault(current, List.of())) {
                queue.addLast(childId);
            }
        }
        return result;
    }

    private String normalizeCategoryId(String categoryId) {
        return StringUtils.hasText(categoryId) ? categoryId.trim() : null;
    }
}
