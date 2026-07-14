package com.onepass.practice.contentimport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onepass.practice.category.persistence.CategoryDO;
import com.onepass.practice.category.persistence.CategoryMapper;
import com.onepass.practice.common.AppException;
import com.onepass.practice.contentimport.persistence.QuestionAssetDO;
import com.onepass.practice.contentimport.persistence.QuestionAssetMapper;
import com.onepass.practice.contentimport.persistence.QuestionImportBatchDO;
import com.onepass.practice.contentimport.persistence.QuestionImportBatchMapper;
import com.onepass.practice.contentimport.persistence.QuestionImportItemDO;
import com.onepass.practice.contentimport.persistence.QuestionImportItemMapper;
import com.onepass.practice.practice.persistence.QuestionAnswerDO;
import com.onepass.practice.practice.persistence.QuestionAnswerMapper;
import com.onepass.practice.practice.persistence.QuestionDO;
import com.onepass.practice.practice.persistence.QuestionMapper;
import com.onepass.practice.practice.persistence.QuestionOptionDO;
import com.onepass.practice.practice.persistence.QuestionOptionMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class QuestionImportService {

    private static final Pattern MARKDOWN_IMAGE_PATTERN = Pattern.compile("!\\[([^]]*)]\\((assets/[^)]+)\\)");

    private final ContentImportProperties properties;
    private final DocxQuestionMarkdownConverter docxConverter;
    private final QuestionMarkdownParser markdownParser;
    private final ObjectMapper objectMapper;
    private final CategoryMapper categoryMapper;
    private final QuestionImportBatchMapper batchMapper;
    private final QuestionImportItemMapper itemMapper;
    private final QuestionAssetMapper assetMapper;
    private final QuestionMapper questionMapper;
    private final QuestionOptionMapper optionMapper;
    private final QuestionAnswerMapper answerMapper;

    public QuestionImportService(
            ContentImportProperties properties,
            DocxQuestionMarkdownConverter docxConverter,
            QuestionMarkdownParser markdownParser,
            ObjectMapper objectMapper,
            CategoryMapper categoryMapper,
            QuestionImportBatchMapper batchMapper,
            QuestionImportItemMapper itemMapper,
            QuestionAssetMapper assetMapper,
            QuestionMapper questionMapper,
            QuestionOptionMapper optionMapper,
            QuestionAnswerMapper answerMapper
    ) {
        this.properties = properties;
        this.docxConverter = docxConverter;
        this.markdownParser = markdownParser;
        this.objectMapper = objectMapper;
        this.categoryMapper = categoryMapper;
        this.batchMapper = batchMapper;
        this.itemMapper = itemMapper;
        this.assetMapper = assetMapper;
        this.questionMapper = questionMapper;
        this.optionMapper = optionMapper;
        this.answerMapper = answerMapper;
    }

    @Transactional
    public QuestionImportBatchView uploadDocx(MultipartFile file, String categoryPath) {
        if (file == null || file.isEmpty()) {
            throw new AppException("请选择 docx 文件");
        }
        String originalFilename = safeFilename(file.getOriginalFilename());
        if (!originalFilename.toLowerCase(Locale.ROOT).endsWith(".docx")) {
            throw new AppException("第一版仅支持 docx 文件");
        }

        String batchId = "qib-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        Path batchDir = Path.of(properties.getStorageRoot()).resolve(batchId).toAbsolutePath().normalize();
        String resolvedCategoryPath = StringUtils.hasText(categoryPath)
                ? categoryPath.trim()
                : properties.getDefaultCategoryPath();
        MarkdownConversionResult conversion = docxConverter.convert(file, batchId, batchDir, resolvedCategoryPath);
        Map<String, String> categoryPathToId = buildCategoryPathToId();
        List<ParsedImportQuestion> parsedQuestions = markdownParser.parse(
                conversion.markdownContent(),
                resolvedCategoryPath,
                categoryPathToId
        );

        QuestionImportBatchDO batch = toBatch(batchId, originalFilename, batchDir, conversion.markdownPath(), parsedQuestions);
        batchMapper.insert(batch);

        for (ExtractedImportAsset asset : conversion.assets()) {
            QuestionAssetDO assetDO = new QuestionAssetDO();
            assetDO.setBatchId(batchId);
            assetDO.setAssetType("image");
            assetDO.setOriginalName(asset.originalName());
            assetDO.setRelativePath(asset.relativePath());
            assetDO.setContentType(asset.contentType());
            assetDO.setFileSize(asset.fileSize());
            assetMapper.insert(assetDO);
        }

        for (ParsedImportQuestion parsedQuestion : parsedQuestions) {
            itemMapper.insert(toItem(batchId, parsedQuestion));
        }

        return getBatch(batchId);
    }

    public QuestionImportBatchView getBatch(String batchId) {
        QuestionImportBatchDO batch = requireBatch(batchId);
        List<QuestionImportItemView> items = itemMapper.selectByBatchId(batchId).stream()
                .map(this::toItemView)
                .toList();
        return new QuestionImportBatchView(
                batch.getBatchId(),
                batch.getOriginalFilename(),
                batch.getStatus(),
                safeInt(batch.getTotalCount()),
                safeInt(batch.getSupportedCount()),
                safeInt(batch.getUnsupportedCount()),
                safeInt(batch.getErrorCount()),
                safeInt(batch.getWarningCount()),
                safeInt(batch.getImportedCount()),
                batch.getMarkdownPath(),
                items
        );
    }

    public String readMarkdown(String batchId) {
        QuestionImportBatchDO batch = requireBatch(batchId);
        try {
            return Files.readString(Path.of(batch.getMarkdownPath()), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new AppException("Markdown 文件不存在");
        }
    }

    @Transactional
    public QuestionImportBatchView confirm(String batchId) {
        QuestionImportBatchDO batch = requireBatch(batchId);
        if ("IMPORTED".equals(batch.getStatus())) {
            return getBatch(batchId);
        }
        if ("CANCELED".equals(batch.getStatus())) {
            throw new AppException("该批次已取消");
        }
        List<QuestionImportItemDO> items = itemMapper.selectImportableByBatchId(batchId);
        Map<String, String> categoryPathToId = buildCategoryPathToId();
        int importedCount = 0;
        for (QuestionImportItemDO item : items) {
            ParsedImportQuestion question = readParsedQuestion(item.getParsedJson());
            String categoryId = categoryPathToId.get(question.categoryPath());
            if (!StringUtils.hasText(categoryId)) {
                continue;
            }
            String questionId = generateQuestionId(batchId, item.getItemOrder());
            insertQuestion(questionId, categoryId, question, item.getItemOrder());
            itemMapper.updateImported(item.getId(), "IMPORTED", questionId);
            linkAssets(batchId, questionId, buildAssetSearchContent(question));
            importedCount += 1;
        }
        batchMapper.updateImported(batchId, "IMPORTED", importedCount, LocalDateTime.now());
        return getBatch(batchId);
    }

    @Transactional
    public QuestionImportBatchView cancel(String batchId) {
        requireBatch(batchId);
        batchMapper.updateCanceled(batchId, "CANCELED", LocalDateTime.now());
        return getBatch(batchId);
    }

    private void insertQuestion(String questionId, String categoryId, ParsedImportQuestion question, int itemOrder) {
        QuestionDO questionDO = new QuestionDO();
        questionDO.setId(questionId);
        questionDO.setCategoryId(categoryId);
        questionDO.setQuestionType(question.normalizedQuestionType());
        questionDO.setStem(rewriteAssetLinks(question.stem(), questionId));
        questionDO.setAnalysis(rewriteAssetLinks(question.analysis(), questionId));
        questionDO.setSortOrder(100000 + itemOrder);
        questionMapper.insert(questionDO);

        int sortOrder = 10;
        for (QuestionImportOption option : question.options()) {
            QuestionOptionDO optionDO = new QuestionOptionDO();
            optionDO.setQuestionId(questionId);
            optionDO.setOptionKey(option.key());
            optionDO.setContent(rewriteAssetLinks(option.content(), questionId));
            optionDO.setSortOrder(sortOrder);
            optionMapper.insert(optionDO);
            sortOrder += 10;
        }

        sortOrder = 10;
        for (String answerKey : question.answerKeys()) {
            QuestionAnswerDO answerDO = new QuestionAnswerDO();
            answerDO.setQuestionId(questionId);
            answerDO.setAnswerKey(answerKey);
            answerDO.setSortOrder(sortOrder);
            answerMapper.insert(answerDO);
            sortOrder += 10;
        }

    }

    private void linkAssets(String batchId, String questionId, String stem) {
        for (String relativePath : extractAssetPaths(stem)) {
            assetMapper.updateQuestionIdByBatchAndRelativePath(batchId, relativePath, questionId);
        }
    }

    private String buildAssetSearchContent(ParsedImportQuestion question) {
        StringBuilder content = new StringBuilder();
        content.append(question.stem()).append('\n').append(question.analysis());
        for (QuestionImportOption option : question.options()) {
            content.append('\n').append(option.content());
        }
        return content.toString();
    }

    private String rewriteAssetLinks(String content, String questionId) {
        if (content == null || content.isBlank()) {
            return content;
        }
        Matcher matcher = MARKDOWN_IMAGE_PATTERN.matcher(content);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String alt = matcher.group(1);
            String relativePath = matcher.group(2);
            String filename = Path.of(relativePath).getFileName().toString();
            matcher.appendReplacement(
                    result,
                    Matcher.quoteReplacement("![" + alt + "](/api/student/question-assets/" + questionId + "/" + filename + ")")
            );
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private List<String> extractAssetPaths(String text) {
        List<String> paths = new ArrayList<>();
        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile("!\\[[^]]*]\\(([^)]+)\\)").matcher(text == null ? "" : text);
        while (matcher.find()) {
            paths.add(matcher.group(1));
        }
        return paths;
    }

    private QuestionImportBatchDO toBatch(
            String batchId,
            String originalFilename,
            Path batchDir,
            Path markdownPath,
            List<ParsedImportQuestion> questions
    ) {
        QuestionImportBatchDO batch = new QuestionImportBatchDO();
        batch.setBatchId(batchId);
        batch.setOriginalFilename(originalFilename);
        batch.setStatus("PARSED");
        batch.setStorageDir(batchDir.toString());
        batch.setMarkdownPath(markdownPath.toString());
        batch.setTotalCount(questions.size());
        batch.setSupportedCount((int) questions.stream().filter(item -> item.supported() && item.errors().isEmpty()).count());
        batch.setUnsupportedCount((int) questions.stream().filter(item -> !item.supported()).count());
        batch.setErrorCount((int) questions.stream().filter(item -> !item.errors().isEmpty()).count());
        batch.setWarningCount((int) questions.stream().filter(item -> !item.warnings().isEmpty()).count());
        batch.setImportedCount(0);
        batch.setErrorReportJson(writeJson(questions.stream()
                .filter(item -> !item.errors().isEmpty() || !item.warnings().isEmpty() || !item.supported())
                .map(item -> Map.of(
                        "itemOrder", item.itemOrder(),
                        "sourceQuestionNo", item.sourceQuestionNo(),
                        "status", markdownParser.statusOf(item),
                        "errors", item.errors(),
                        "warnings", item.warnings()
                ))
                .toList()));
        return batch;
    }

    private QuestionImportItemDO toItem(String batchId, ParsedImportQuestion question) {
        QuestionImportItemDO item = new QuestionImportItemDO();
        item.setBatchId(batchId);
        item.setItemOrder(question.itemOrder());
        item.setSourceQuestionNo(question.sourceQuestionNo());
        item.setQuestionType(question.rawQuestionType());
        item.setNormalizedQuestionType(question.normalizedQuestionType());
        item.setCategoryPath(question.categoryPath());
        item.setStatus(markdownParser.statusOf(question));
        item.setErrorsJson(writeJson(question.errors()));
        item.setWarningsJson(writeJson(question.warnings()));
        item.setParsedJson(writeJson(question));
        item.setMarkdownBlock(question.markdownBlock());
        return item;
    }

    private QuestionImportItemView toItemView(QuestionImportItemDO item) {
        ParsedImportQuestion question = readParsedQuestion(item.getParsedJson());
        return new QuestionImportItemView(
                safeInt(item.getItemOrder()),
                item.getSourceQuestionNo(),
                item.getQuestionType(),
                item.getNormalizedQuestionType(),
                item.getCategoryPath(),
                item.getStatus(),
                readStringList(item.getErrorsJson()),
                readStringList(item.getWarningsJson()),
                previewStem(question),
                question.stem(),
                question.options(),
                question.answerKeys(),
                question.analysis(),
                question.knowledgePoint(),
                question.difficulty(),
                item.getTargetQuestionId()
        );
    }

    private QuestionImportBatchDO requireBatch(String batchId) {
        QuestionImportBatchDO batch = batchMapper.selectByBatchId(batchId);
        if (batch == null) {
            throw new AppException("导入批次不存在");
        }
        return batch;
    }

    private Map<String, String> buildCategoryPathToId() {
        List<CategoryDO> categories = categoryMapper.selectAll();
        Map<String, CategoryDO> idMap = new HashMap<>();
        for (CategoryDO category : categories) {
            idMap.put(category.getId(), category);
        }
        Map<String, String> pathToId = new HashMap<>();
        for (CategoryDO category : categories) {
            if (!Boolean.TRUE.equals(category.getLeaf())) {
                continue;
            }
            pathToId.put(buildCategoryPath(category, idMap), category.getId());
        }
        return pathToId;
    }

    private String buildCategoryPath(CategoryDO category, Map<String, CategoryDO> idMap) {
        List<String> names = new ArrayList<>();
        CategoryDO current = category;
        while (current != null) {
            names.add(0, current.getName());
            current = StringUtils.hasText(current.getParentId()) ? idMap.get(current.getParentId()) : null;
        }
        return String.join("/", names);
    }

    private ParsedImportQuestion readParsedQuestion(String json) {
        try {
            return objectMapper.readValue(json, ParsedImportQuestion.class);
        } catch (JsonProcessingException exception) {
            throw new AppException("导入题块解析结果损坏");
        }
    }

    private List<String> readStringList(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {
            });
        } catch (JsonProcessingException exception) {
            return List.of();
        }
    }

    private String previewStem(ParsedImportQuestion question) {
        String stem = question.stem() == null ? "" : question.stem().replaceAll("\\s+", " ");
        return stem.length() > 120 ? stem.substring(0, 120) + "..." : stem;
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new AppException("导入数据序列化失败");
        }
    }

    private String generateQuestionId(String batchId, int itemOrder) {
        String compactBatch = batchId.replace("qib-", "");
        return "imp-" + compactBatch.substring(0, Math.min(18, compactBatch.length())) + "-" + itemOrder;
    }

    private String safeFilename(String originalFilename) {
        String value = originalFilename == null ? "questions.docx" : originalFilename;
        return value.replaceAll("[\\\\/]+", "_");
    }

    private int safeInt(Integer value) {
        return value == null ? 0 : value;
    }
}
