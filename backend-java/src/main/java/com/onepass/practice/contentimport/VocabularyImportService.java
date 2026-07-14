package com.onepass.practice.contentimport;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onepass.practice.common.AppException;
import com.onepass.practice.vocabulary.persistence.VocabularyBookMapper;
import com.onepass.practice.vocabulary.persistence.VocabularyWordDO;
import com.onepass.practice.vocabulary.persistence.VocabularyWordMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class VocabularyImportService {

    private final ContentImportProperties properties;
    private final DocxVocabularyParser vocabularyParser;
    private final ObjectMapper objectMapper;
    private final VocabularyBookMapper vocabularyBookMapper;
    private final VocabularyWordMapper vocabularyWordMapper;

    public VocabularyImportService(
            ContentImportProperties properties,
            DocxVocabularyParser vocabularyParser,
            ObjectMapper objectMapper,
            VocabularyBookMapper vocabularyBookMapper,
            VocabularyWordMapper vocabularyWordMapper
    ) {
        this.properties = properties;
        this.vocabularyParser = vocabularyParser;
        this.objectMapper = objectMapper;
        this.vocabularyBookMapper = vocabularyBookMapper;
        this.vocabularyWordMapper = vocabularyWordMapper;
    }

    @Transactional
    public VocabularyImportBatchView uploadDocx(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("请选择词库 docx 文件");
        }
        String originalFilename = file.getOriginalFilename() == null ? "words.docx" : file.getOriginalFilename();
        if (!originalFilename.toLowerCase(Locale.ROOT).endsWith(".docx")) {
            throw new AppException("词库导入仅支持 docx 文件");
        }

        String batchId = "vib-" + UUID.randomUUID().toString().replace("-", "").substring(0, 24);
        Path batchDir = Path.of(properties.getStorageRoot()).resolve(batchId).toAbsolutePath().normalize();
        ParsedVocabularyDocument document = vocabularyParser.parse(file);
        StoredVocabularyImportBatch batch = new StoredVocabularyImportBatch(
                batchId,
                safeFilename(originalFilename),
                "PARSED",
                batchDir.toString(),
                document,
                0
        );
        writeBatch(batch);
        return toBatchView(batch);
    }

    public VocabularyImportBatchView getBatch(String batchId) {
        return toBatchView(readBatch(batchId));
    }

    @Transactional
    public VocabularyImportBatchView confirm(String batchId) {
        StoredVocabularyImportBatch batch = readBatch(batchId);
        if ("IMPORTED".equals(batch.status())) {
            return toBatchView(batch);
        }
        if ("CANCELED".equals(batch.status())) {
            throw new AppException("该词库导入批次已取消");
        }
        ParsedVocabularyDocument document = batch.document();
        failIfInvalid(document);

        int sortOrder = nextBookSortOrder();
        vocabularyBookMapper.upsertImportBook(
                document.bookId(),
                document.bookName(),
                document.description(),
                sortOrder
        );
        vocabularyWordMapper.deleteByBookId(document.bookId());
        for (ParsedVocabularyWord word : document.words()) {
            VocabularyWordDO item = new VocabularyWordDO();
            item.setBookId(document.bookId());
            item.setEnglish(word.english());
            item.setChinese(word.chinese());
            item.setPartOfSpeech(word.partOfSpeech());
            item.setSortOrder(word.sortOrder());
            vocabularyWordMapper.insert(item);
        }

        StoredVocabularyImportBatch imported = new StoredVocabularyImportBatch(
                batch.batchId(),
                batch.originalFilename(),
                "IMPORTED",
                batch.storageDir(),
                document,
                document.words().size()
        );
        writeBatch(imported);
        return toBatchView(imported);
    }

    public VocabularyImportBatchView cancel(String batchId) {
        StoredVocabularyImportBatch batch = readBatch(batchId);
        StoredVocabularyImportBatch canceled = new StoredVocabularyImportBatch(
                batch.batchId(),
                batch.originalFilename(),
                "CANCELED",
                batch.storageDir(),
                batch.document(),
                batch.importedCount()
        );
        writeBatch(canceled);
        return toBatchView(canceled);
    }

    private void failIfInvalid(ParsedVocabularyDocument document) {
        List<String> messages = new ArrayList<>(document.errors());
        for (ParsedVocabularyWord word : document.words()) {
            if (!word.errors().isEmpty()) {
                messages.add("第 " + word.itemOrder() + " 条：" + String.join("；", word.errors()));
            }
        }
        if (!messages.isEmpty()) {
            throw new AppException("词库文档校验失败：" + String.join("；", messages.stream().limit(5).toList()));
        }
    }

    private VocabularyImportBatchView toBatchView(StoredVocabularyImportBatch batch) {
        ParsedVocabularyDocument document = batch.document();
        int errorCount = document.errors().size();
        int warningCount = document.warnings().size();
        List<VocabularyImportItemView> itemViews = new ArrayList<>();
        for (ParsedVocabularyWord word : document.words()) {
            errorCount += word.errors().size();
            warningCount += word.warnings().size();
            itemViews.add(new VocabularyImportItemView(
                    word.itemOrder(),
                    word.english(),
                    word.chinese(),
                    word.partOfSpeech(),
                    word.sortOrder(),
                    statusOf(word),
                    word.errors(),
                    word.warnings()
            ));
        }
        int importableCount = document.errors().isEmpty()
                ? (int) document.words().stream().filter(item -> item.errors().isEmpty()).count()
                : 0;
        return new VocabularyImportBatchView(
                batch.batchId(),
                batch.originalFilename(),
                batch.status(),
                document.bookId(),
                document.bookName(),
                document.description(),
                document.words().size(),
                importableCount,
                errorCount,
                warningCount,
                batch.importedCount(),
                document.errors(),
                document.warnings(),
                itemViews
        );
    }

    private String statusOf(ParsedVocabularyWord word) {
        if (!word.errors().isEmpty()) {
            return "ERROR";
        }
        if (!word.warnings().isEmpty()) {
            return "WARNING";
        }
        return "READY";
    }

    private StoredVocabularyImportBatch readBatch(String batchId) {
        Path path = batchPath(batchId);
        if (!Files.exists(path)) {
            throw new AppException("词库导入批次不存在");
        }
        try {
            return objectMapper.readValue(Files.readString(path, StandardCharsets.UTF_8), StoredVocabularyImportBatch.class);
        } catch (IOException exception) {
            throw new AppException("词库导入批次解析失败");
        }
    }

    private void writeBatch(StoredVocabularyImportBatch batch) {
        Path path = batchPath(batch.batchId());
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, objectMapper.writeValueAsString(batch), StandardCharsets.UTF_8);
        } catch (JsonProcessingException exception) {
            throw new AppException("词库导入批次序列化失败");
        } catch (IOException exception) {
            throw new AppException("词库导入批次保存失败");
        }
    }

    private Path batchPath(String batchId) {
        String safeBatchId = batchId == null ? "" : batchId.replaceAll("[^A-Za-z0-9_-]", "");
        if (safeBatchId.isBlank()) {
            throw new AppException("词库导入批次不存在");
        }
        return Path.of(properties.getStorageRoot()).resolve(safeBatchId).resolve("vocabulary.json").toAbsolutePath().normalize();
    }

    private int nextBookSortOrder() {
        Integer maxSortOrder = vocabularyBookMapper.selectMaxSortOrder();
        return (maxSortOrder == null ? 0 : maxSortOrder) + 10;
    }

    private String safeFilename(String originalFilename) {
        String value = originalFilename == null ? "words.docx" : originalFilename;
        return value.replaceAll("[\\\\/]+", "_");
    }
}
