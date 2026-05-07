package com.onepass.practice.practice;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onepass.practice.category.CategoryCatalog;
import com.onepass.practice.category.CategoryNode;
import com.onepass.practice.common.JsonResourceReader;
import com.onepass.practice.practice.persistence.QuestionAnswerDO;
import com.onepass.practice.practice.persistence.QuestionAnswerMapper;
import com.onepass.practice.practice.persistence.QuestionDO;
import com.onepass.practice.practice.persistence.QuestionMapper;
import com.onepass.practice.practice.persistence.QuestionOptionDO;
import com.onepass.practice.practice.persistence.QuestionOptionMapper;
import com.onepass.practice.practice.persistence.QuestionTagDO;
import com.onepass.practice.practice.persistence.QuestionTagMapper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class PracticeQuestionCatalog {

    private static final Logger log = LoggerFactory.getLogger(PracticeQuestionCatalog.class);

    private final boolean mockEnabled;
    private final List<PracticeQuestionDefinition> questions;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper questionOptionMapper;
    private final QuestionTagMapper questionTagMapper;
    private final QuestionAnswerMapper questionAnswerMapper;
    private final CategoryCatalog categoryCatalog;

    public PracticeQuestionCatalog(
            @Value("${app.mock.enabled:true}") boolean mockEnabled,
            JsonResourceReader jsonResourceReader,
            ObjectProvider<QuestionMapper> questionMapperProvider,
            ObjectProvider<QuestionOptionMapper> questionOptionMapperProvider,
            ObjectProvider<QuestionTagMapper> questionTagMapperProvider,
            ObjectProvider<QuestionAnswerMapper> questionAnswerMapperProvider,
            CategoryCatalog categoryCatalog
    ) {
        this.mockEnabled = mockEnabled;
        this.questionMapper = questionMapperProvider.getIfAvailable();
        this.questionOptionMapper = questionOptionMapperProvider.getIfAvailable();
        this.questionTagMapper = questionTagMapperProvider.getIfAvailable();
        this.questionAnswerMapper = questionAnswerMapperProvider.getIfAvailable();
        this.categoryCatalog = categoryCatalog;
        this.questions = List.copyOf(
                jsonResourceReader.read(
                        "mock-data/practice-questions.json",
                        new TypeReference<List<PracticeQuestionDefinition>>() {
                        }
                )
        );
        if (mockEnabled || questionMapper == null) {
            log.info("Loaded practice question catalog mode=resource count={}", questions.size());
        } else {
            int mysqlCount = questionMapper.selectAllActive().size();
            log.info("Loaded practice question catalog mode=mysql count={} resourceFallbackCount={}", mysqlCount, questions.size());
        }
    }

    public List<PracticeQuestionDefinition> listAll() {
        if (!mockEnabled) {
            return loadFromMysql();
        }
        return questions;
    }

    private List<PracticeQuestionDefinition> loadFromMysql() {
        if (questionMapper == null || questionOptionMapper == null || questionTagMapper == null || questionAnswerMapper == null) {
            throw new IllegalStateException("Question mappers are not available");
        }

        List<QuestionDO> questionRows = questionMapper.selectAllActive();
        Map<String, List<QuestionOptionDO>> optionMap = groupOptions(questionOptionMapper.selectAll());
        Map<String, List<QuestionTagDO>> tagMap = groupTags(questionTagMapper.selectAll());
        Map<String, List<QuestionAnswerDO>> answerMap = groupAnswers(questionAnswerMapper.selectAll());
        Map<String, List<String>> categoryPathMap = buildCategoryPathMap();

        return questionRows.stream()
                .map(question -> new PracticeQuestionDefinition(
                        question.getId(),
                        question.getCategoryId(),
                        categoryPathMap.getOrDefault(question.getCategoryId(), List.of(question.getCategoryId())),
                        question.getQuestionType(),
                        tagMap.getOrDefault(question.getId(), List.of()).stream().map(QuestionTagDO::getTagName).toList(),
                        question.getStem(),
                        optionMap.getOrDefault(question.getId(), List.of()).stream()
                                .map(item -> new PracticeQuestionOptionView(item.getOptionKey(), item.getContent()))
                                .toList(),
                        answerMap.getOrDefault(question.getId(), List.of()).stream().map(QuestionAnswerDO::getAnswerKey).toList(),
                        question.getAnalysis()
                ))
                .toList();
    }

    private Map<String, List<String>> buildCategoryPathMap() {
        Map<String, CategoryNode> categoryMap = new HashMap<>();
        for (CategoryNode node : categoryCatalog.listAll()) {
            categoryMap.put(node.id(), node);
        }

        Map<String, List<String>> result = new HashMap<>();
        for (CategoryNode node : categoryMap.values()) {
            List<String> path = new ArrayList<>();
            CategoryNode current = node;
            while (current != null) {
                path.add(0, current.id());
                String parentId = current.parentId();
                current = parentId == null ? null : categoryMap.get(parentId);
            }
            result.put(node.id(), List.copyOf(path));
        }
        return result;
    }

    private Map<String, List<QuestionOptionDO>> groupOptions(List<QuestionOptionDO> rows) {
        Map<String, List<QuestionOptionDO>> result = new HashMap<>();
        for (QuestionOptionDO row : rows) {
            result.computeIfAbsent(row.getQuestionId(), key -> new ArrayList<>()).add(row);
        }
        return result;
    }

    private Map<String, List<QuestionTagDO>> groupTags(List<QuestionTagDO> rows) {
        Map<String, List<QuestionTagDO>> result = new HashMap<>();
        for (QuestionTagDO row : rows) {
            result.computeIfAbsent(row.getQuestionId(), key -> new ArrayList<>()).add(row);
        }
        return result;
    }

    private Map<String, List<QuestionAnswerDO>> groupAnswers(List<QuestionAnswerDO> rows) {
        Map<String, List<QuestionAnswerDO>> result = new HashMap<>();
        for (QuestionAnswerDO row : rows) {
            result.computeIfAbsent(row.getQuestionId(), key -> new ArrayList<>()).add(row);
        }
        return result;
    }
}
