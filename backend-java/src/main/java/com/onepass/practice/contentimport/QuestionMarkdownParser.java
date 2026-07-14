package com.onepass.practice.contentimport;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class QuestionMarkdownParser {

    private static final Pattern FIELD_HEADER = Pattern.compile("^([^【】]+)【(.*)】$");
    private static final Pattern OPTION_LINE = Pattern.compile("^\\s*([A-Z])[.．、]\\s*(.*)$");

    public List<ParsedImportQuestion> parse(
            String markdown,
            String defaultCategoryPath,
            Map<String, String> categoryPathToId
    ) {
        List<ParsedImportQuestion> questions = new ArrayList<>();
        List<String> blocks = splitBlocks(markdown);
        int order = 1;
        for (String block : blocks) {
            ParsedImportQuestion parsed = parseBlock(order, block, defaultCategoryPath, categoryPathToId);
            questions.add(parsed);
            order += 1;
        }
        return withDuplicateWarnings(questions);
    }

    private List<String> splitBlocks(String markdown) {
        List<String> blocks = new ArrayList<>();
        String[] rawBlocks = nullToEmpty(markdown).split("(?m)^#####\\s*$");
        for (String rawBlock : rawBlocks) {
            String block = rawBlock.trim();
            if (!block.isBlank()) {
                blocks.add("#####\n" + block);
            }
        }
        return blocks;
    }

    private ParsedImportQuestion parseBlock(
            int itemOrder,
            String block,
            String defaultCategoryPath,
            Map<String, String> categoryPathToId
    ) {
        String sourceQuestionNo = "";
        String categoryPath = defaultCategoryPath;
        String rawType = "";
        String answerText = "";
        String knowledgePoint = "";
        String difficulty = "";
        String microCourseId = "";
        String source = "";
        List<String> tags = new ArrayList<>();
        List<String> stemLines = new ArrayList<>();
        List<QuestionImportOption> options = new ArrayList<>();
        List<String> analysisLines = new ArrayList<>();
        String section = "";

        for (String rawLine : block.split("\\R")) {
            String line = rawLine.trim();
            if (line.isBlank() || "#####".equals(line)) {
                continue;
            }
            Matcher optionMatcher = OPTION_LINE.matcher(line);
            if (optionMatcher.matches() && "options".equals(section)) {
                options.add(new QuestionImportOption(optionMatcher.group(1).trim(), optionMatcher.group(2).trim()));
                continue;
            }
            Matcher fieldMatcher = FIELD_HEADER.matcher(line);
            if (fieldMatcher.matches()) {
                String fieldName = fieldMatcher.group(1).trim();
                String fieldValue = fieldMatcher.group(2).trim();
                switch (fieldName) {
                    case "来源题号" -> sourceQuestionNo = fieldValue;
                    case "分类" -> categoryPath = defaultCategoryPath;
                    case "标签" -> tags = splitTags(fieldValue);
                    case "题型" -> rawType = fieldValue;
                    case "知识点" -> knowledgePoint = fieldValue;
                    case "难易度" -> difficulty = fieldValue;
                    case "微课ID" -> microCourseId = fieldValue;
                    case "来源" -> source = fieldValue;
                    default -> {
                    }
                }
                continue;
            }
            if ("题干：".equals(line)) {
                section = "stem";
                continue;
            }
            if ("选项：".equals(line)) {
                section = "options";
                continue;
            }
            if ("答案：".equals(line)) {
                section = "answer";
                continue;
            }
            if ("解析：".equals(line)) {
                section = "analysis";
                continue;
            }

            switch (section) {
                case "stem" -> stemLines.add(line);
                case "options" -> appendLooseOption(options, line);
                case "answer" -> answerText = appendText(answerText, line);
                case "analysis" -> analysisLines.add(line);
                default -> stemLines.add(line);
            }
        }

        String normalizedType = normalizeType(rawType);
        boolean supported = isSupportedType(normalizedType);
        if ("judge".equals(normalizedType) && options.isEmpty()) {
            options.add(new QuestionImportOption("A", "正确"));
            options.add(new QuestionImportOption("B", "错误"));
        }
        List<String> answerKeys = normalizeAnswers(normalizedType, answerText);
        List<String> errors = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        String stem = String.join("\n", stemLines).trim();
        String analysis = String.join("\n", analysisLines).trim();
        String categoryId = categoryPathToId.get(categoryPath);
        validateQuestion(supported, normalizedType, categoryPath, categoryId, stem, options, answerKeys, errors);

        return new ParsedImportQuestion(
                itemOrder,
                sourceQuestionNo,
                rawType,
                normalizedType,
                categoryPath,
                categoryId,
                tags,
                stem,
                options,
                answerKeys,
                analysis.isBlank() ? "暂无解析" : analysis,
                knowledgePoint,
                difficulty,
                microCourseId,
                source,
                block,
                supported,
                errors,
                warnings
        );
    }

    private void validateQuestion(
            boolean supported,
            String normalizedType,
            String categoryPath,
            String categoryId,
            String stem,
            List<QuestionImportOption> options,
            List<String> answerKeys,
            List<String> errors
    ) {
        if (!StringUtils.hasText(normalizedType)) {
            errors.add("题型无法识别");
        }
        if (!StringUtils.hasText(categoryPath) || !StringUtils.hasText(categoryId)) {
            errors.add("分类不存在：" + nullToEmpty(categoryPath));
        }
        if (!StringUtils.hasText(stem)) {
            errors.add("缺少题干");
        }
        if (!supported) {
            return;
        }
        if (options.size() < 2) {
            errors.add("选择类题目至少需要 2 个选项");
        }
        if (answerKeys.isEmpty()) {
            errors.add("缺少答案");
        }
        if (("single".equals(normalizedType) || "judge".equals(normalizedType)) && answerKeys.size() != 1) {
            errors.add("单选/判断题必须只有 1 个正确答案");
        }
        Set<String> optionKeys = new HashSet<>();
        for (QuestionImportOption option : options) {
            optionKeys.add(option.key());
        }
        for (String answerKey : answerKeys) {
            if (!optionKeys.contains(answerKey)) {
                errors.add("答案 " + answerKey + " 不在选项中");
            }
        }
    }

    private List<ParsedImportQuestion> withDuplicateWarnings(List<ParsedImportQuestion> questions) {
        Map<String, Integer> seen = new HashMap<>();
        List<ParsedImportQuestion> result = new ArrayList<>();
        for (ParsedImportQuestion question : questions) {
            String key = normalizeStem(question.stem());
            List<String> warnings = new ArrayList<>(question.warnings());
            if (!key.isBlank()) {
                Integer firstOrder = seen.putIfAbsent(key, question.itemOrder());
                if (firstOrder != null) {
                    warnings.add("题干与第 " + firstOrder + " 题重复");
                }
            }
            result.add(new ParsedImportQuestion(
                    question.itemOrder(),
                    question.sourceQuestionNo(),
                    question.rawQuestionType(),
                    question.normalizedQuestionType(),
                    question.categoryPath(),
                    question.categoryId(),
                    question.tags(),
                    question.stem(),
                    question.options(),
                    question.answerKeys(),
                    question.analysis(),
                    question.knowledgePoint(),
                    question.difficulty(),
                    question.microCourseId(),
                    question.source(),
                    question.markdownBlock(),
                    question.supported(),
                    question.errors(),
                    warnings
            ));
        }
        return result;
    }

    public String statusOf(ParsedImportQuestion question) {
        if (!question.errors().isEmpty()) {
            return "ERROR";
        }
        if (!question.supported()) {
            return "UNSUPPORTED";
        }
        return question.warnings().isEmpty() ? "READY" : "WARNING";
    }

    private void appendLooseOption(List<QuestionImportOption> options, String line) {
        Matcher matcher = OPTION_LINE.matcher(line);
        if (matcher.matches()) {
            options.add(new QuestionImportOption(matcher.group(1).trim(), matcher.group(2).trim()));
        }
    }

    private String normalizeType(String rawType) {
        String value = nullToEmpty(rawType);
        if (value.contains("单选")) {
            return "single";
        }
        if (value.contains("多选")) {
            return "multiple";
        }
        if (value.contains("不定项")) {
            return "indefinite";
        }
        if (value.contains("判断")) {
            return "judge";
        }
        if (value.contains("简答")) {
            return "short_answer";
        }
        if (value.contains("填空")) {
            return "fill_blank";
        }
        if (value.contains("材料")) {
            return "material";
        }
        return "";
    }

    private boolean isSupportedType(String normalizedType) {
        return Set.of("single", "multiple", "indefinite", "judge").contains(normalizedType);
    }

    private List<String> normalizeAnswers(String normalizedType, String answerText) {
        String value = nullToEmpty(answerText).trim();
        if ("judge".equals(normalizedType)) {
            if ("正确".equals(value) || "对".equals(value)) {
                return List.of("A");
            }
            if ("错误".equals(value) || "错".equals(value)) {
                return List.of("B");
            }
        }
        LinkedHashSet<String> answers = new LinkedHashSet<>();
        Matcher matcher = Pattern.compile("[A-Za-z]").matcher(value);
        while (matcher.find()) {
            answers.add(matcher.group().toUpperCase(Locale.ROOT));
        }
        return new ArrayList<>(answers);
    }

    private List<String> splitTags(String value) {
        List<String> result = new ArrayList<>();
        for (String part : nullToEmpty(value).split("[,，]")) {
            String tag = part.trim();
            if (!tag.isBlank()) {
                result.add(tag);
            }
        }
        return result;
    }

    private String appendText(String current, String line) {
        return current == null || current.isBlank() ? line : current + "\n" + line;
    }

    private String normalizeStem(String value) {
        return nullToEmpty(value).replaceAll("\\s+", "");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
