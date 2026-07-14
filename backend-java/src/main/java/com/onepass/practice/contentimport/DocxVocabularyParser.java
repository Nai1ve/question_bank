package com.onepass.practice.contentimport;

import com.onepass.practice.common.AppException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocxVocabularyParser {

    private static final Pattern FIELD_VALUE = Pattern.compile("^\\s*(?:[•\\-]\\s*)?([^【】]+)【(.*)】\\s*$");
    private static final Pattern BRACKET_FIELD_VALUE = Pattern.compile("^\\s*(?:[•\\-]\\s*)?【([^】]+)】\\s*(.*)\\s*$");
    private static final Pattern COLON_FIELD_VALUE = Pattern.compile("^\\s*(?:[•\\-]\\s*)?([^：:]+)[：:]\\s*(.*)\\s*$");
    private static final Pattern BOOK_ID_PATTERN = Pattern.compile("^[A-Za-z0-9_-]{2,64}$");

    public ParsedVocabularyDocument parse(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream(); XWPFDocument document = new XWPFDocument(inputStream)) {
            return parseLines(extractLines(document));
        } catch (IOException exception) {
            throw new AppException("词库 Docx 文件解析失败");
        }
    }

    ParsedVocabularyDocument parseLines(List<String> rawLines) {
        Map<String, String> metadata = new LinkedHashMap<>();
        List<List<String>> blocks = splitBlocks(rawLines, metadata);
        List<VocabularyWordDraft> drafts = new ArrayList<>();
        int order = 1;
        for (List<String> block : blocks) {
            VocabularyWordDraft draft = parseWordBlock(order, block);
            drafts.add(draft);
            order += 1;
        }

        List<String> documentErrors = validateMetadata(metadata);
        List<String> documentWarnings = new ArrayList<>();
        if (drafts.isEmpty()) {
            documentErrors.add("缺少单词条目");
        }
        markDuplicateEnglish(drafts);

        List<ParsedVocabularyWord> words = drafts.stream()
                .map(VocabularyWordDraft::toParsedWord)
                .toList();
        return new ParsedVocabularyDocument(
                nullToEmpty(metadata.get("bookId")),
                nullToEmpty(metadata.get("bookName")),
                nullToEmpty(metadata.get("description")),
                words,
                documentErrors,
                documentWarnings
        );
    }

    private List<String> extractLines(XWPFDocument document) {
        List<String> lines = new ArrayList<>();
        for (IBodyElement element : document.getBodyElements()) {
            if (element.getElementType() == BodyElementType.PARAGRAPH) {
                appendParagraph(lines, (XWPFParagraph) element);
            } else if (element.getElementType() == BodyElementType.TABLE) {
                appendTable(lines, (XWPFTable) element);
            }
        }
        return lines;
    }

    private void appendParagraph(List<String> lines, XWPFParagraph paragraph) {
        String text = cleanLine(paragraph.getText());
        if (!text.isBlank()) {
            lines.add(text);
        }
    }

    private void appendTable(List<String> lines, XWPFTable table) {
        List<List<String>> rows = new ArrayList<>();
        for (XWPFTableRow row : table.getRows()) {
            List<String> cells = new ArrayList<>();
            for (XWPFTableCell cell : row.getTableCells()) {
                String value = cleanLine(cell.getText());
                cells.add(value);
            }
            if (cells.stream().anyMatch(StringUtils::hasText)) {
                rows.add(cells);
            }
        }
        if (rows.isEmpty()) {
            return;
        }

        Map<String, Integer> headerIndexes = headerIndexes(rows.get(0));
        if (headerIndexes.containsKey("english") && headerIndexes.containsKey("chinese")) {
            for (int index = 1; index < rows.size(); index += 1) {
                List<String> row = rows.get(index);
                lines.add("#####");
                appendFieldFromTable(lines, "英文", rowValue(row, headerIndexes.get("english")));
                appendFieldFromTable(lines, "中文", rowValue(row, headerIndexes.get("chinese")));
                appendFieldFromTable(lines, "词性", rowValue(row, headerIndexes.get("partOfSpeech")));
                appendFieldFromTable(lines, "排序", rowValue(row, headerIndexes.get("sortOrder")));
            }
            return;
        }

        for (List<String> row : rows) {
            if (row.size() >= 2) {
                String normalizedName = normalizeFieldName(row.get(0));
                if (!normalizedName.isBlank()) {
                    lines.add(row.get(0).trim() + "【" + row.get(1).trim() + "】");
                    continue;
                }
            }
            lines.add(String.join(" ", row).trim());
        }
    }

    private Map<String, Integer> headerIndexes(List<String> headerRow) {
        Map<String, Integer> result = new HashMap<>();
        for (int index = 0; index < headerRow.size(); index += 1) {
            String normalized = normalizeFieldName(headerRow.get(index));
            if (!normalized.isBlank()) {
                result.put(normalized, index);
            }
        }
        return result;
    }

    private void appendFieldFromTable(List<String> lines, String name, String value) {
        if (StringUtils.hasText(value)) {
            lines.add(name + "【" + value.trim() + "】");
        }
    }

    private String rowValue(List<String> row, Integer index) {
        if (index == null || index < 0 || index >= row.size()) {
            return "";
        }
        return row.get(index);
    }

    private List<List<String>> splitBlocks(List<String> rawLines, Map<String, String> metadata) {
        List<List<String>> blocks = new ArrayList<>();
        List<String> current = new ArrayList<>();
        boolean inBlock = false;
        for (String rawLine : rawLines) {
            String line = cleanLine(rawLine);
            if (line.isBlank()) {
                continue;
            }
            if ("#####".equals(line)) {
                if (!current.isEmpty()) {
                    blocks.add(current);
                    current = new ArrayList<>();
                }
                inBlock = true;
                continue;
            }

            FieldValue fieldValue = parseFieldLine(line);
            if (!inBlock && fieldValue != null && isMetadataField(fieldValue.name())) {
                metadata.put(fieldValue.name(), fieldValue.value());
                continue;
            }
            if (!inBlock && fieldValue != null && isWordField(fieldValue.name())) {
                inBlock = true;
            }
            if (inBlock && fieldValue != null && "english".equals(fieldValue.name()) && containsEnglish(current)) {
                blocks.add(current);
                current = new ArrayList<>();
            }
            if (inBlock) {
                current.add(line);
            }
        }
        if (!current.isEmpty()) {
            blocks.add(current);
        }
        return blocks;
    }

    private VocabularyWordDraft parseWordBlock(int order, List<String> lines) {
        VocabularyWordDraft draft = new VocabularyWordDraft(order);
        Map<String, String> values = new HashMap<>();
        for (String line : lines) {
            FieldValue fieldValue = parseFieldLine(line);
            if (fieldValue != null && isWordField(fieldValue.name())) {
                values.put(fieldValue.name(), fieldValue.value());
            }
        }
        draft.english = nullToEmpty(values.get("english"));
        draft.chinese = nullToEmpty(values.get("chinese"));
        draft.partOfSpeech = nullToEmpty(values.get("partOfSpeech"));
        String rawSortOrder = nullToEmpty(values.get("sortOrder"));
        if (rawSortOrder.isBlank()) {
            draft.sortOrder = order * 10;
            draft.warnings.add("缺少排序，已按条目顺序自动设置");
        } else {
            try {
                draft.sortOrder = Integer.parseInt(rawSortOrder);
            } catch (NumberFormatException exception) {
                draft.sortOrder = order * 10;
                draft.errors.add("排序必须是整数：" + rawSortOrder);
            }
        }
        if (!StringUtils.hasText(draft.english)) {
            draft.errors.add("缺少英文");
        }
        if (!StringUtils.hasText(draft.chinese)) {
            draft.errors.add("缺少中文");
        }
        return draft;
    }

    private List<String> validateMetadata(Map<String, String> metadata) {
        List<String> errors = new ArrayList<>();
        String bookId = nullToEmpty(metadata.get("bookId"));
        String bookName = nullToEmpty(metadata.get("bookName"));
        if (!StringUtils.hasText(bookId)) {
            errors.add("缺少词库ID");
        } else if (!BOOK_ID_PATTERN.matcher(bookId).matches()) {
            errors.add("词库ID 只能包含英文、数字、下划线和短横线，长度 2-64");
        }
        if (!StringUtils.hasText(bookName)) {
            errors.add("缺少词库名称");
        }
        return errors;
    }

    private void markDuplicateEnglish(List<VocabularyWordDraft> drafts) {
        Set<String> seen = new HashSet<>();
        for (VocabularyWordDraft draft : drafts) {
            String key = draft.english.toLowerCase(Locale.ROOT);
            if (!key.isBlank() && !seen.add(key)) {
                draft.errors.add("英文重复：" + draft.english);
            }
        }
    }

    private boolean containsEnglish(List<String> lines) {
        return lines.stream()
                .map(this::parseFieldLine)
                .anyMatch(field -> field != null && "english".equals(field.name()));
    }

    private FieldValue parseFieldLine(String line) {
        Matcher fieldMatcher = FIELD_VALUE.matcher(line);
        if (fieldMatcher.matches()) {
            return fieldValue(fieldMatcher.group(1), fieldMatcher.group(2));
        }
        Matcher bracketMatcher = BRACKET_FIELD_VALUE.matcher(line);
        if (bracketMatcher.matches()) {
            return fieldValue(bracketMatcher.group(1), bracketMatcher.group(2));
        }
        Matcher colonMatcher = COLON_FIELD_VALUE.matcher(line);
        if (colonMatcher.matches()) {
            return fieldValue(colonMatcher.group(1), colonMatcher.group(2));
        }
        return null;
    }

    private FieldValue fieldValue(String rawName, String rawValue) {
        String name = normalizeFieldName(rawName);
        if (name.isBlank()) {
            return null;
        }
        return new FieldValue(name, cleanLine(rawValue));
    }

    private String normalizeFieldName(String value) {
        String normalized = nullToEmpty(value)
                .replaceAll("\\s+", "")
                .replace("_", "")
                .replace("-", "")
                .toLowerCase(Locale.ROOT);
        return switch (normalized) {
            case "词库id", "词库编号", "bookid", "vocabularybookid" -> "bookId";
            case "词库名称", "词库名", "bookname", "vocabularybookname" -> "bookName";
            case "词库说明", "说明", "描述", "description", "bookdescription" -> "description";
            case "英文", "单词", "word", "english" -> "english";
            case "中文", "释义", "意思", "meaning", "chinese" -> "chinese";
            case "词性", "partofspeech", "pos" -> "partOfSpeech";
            case "排序", "序号", "sortorder", "order" -> "sortOrder";
            default -> "";
        };
    }

    private boolean isMetadataField(String name) {
        return Set.of("bookId", "bookName", "description").contains(name);
    }

    private boolean isWordField(String name) {
        return Set.of("english", "chinese", "partOfSpeech", "sortOrder").contains(name);
    }

    private String cleanLine(String value) {
        return nullToEmpty(value)
                .replace('\u00A0', ' ')
                .replace('\u2028', ' ')
                .replace('\u2029', ' ')
                .trim();
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record FieldValue(String name, String value) {
    }

    private static final class VocabularyWordDraft {
        private final int itemOrder;
        private String english = "";
        private String chinese = "";
        private String partOfSpeech = "";
        private int sortOrder;
        private final List<String> errors = new ArrayList<>();
        private final List<String> warnings = new ArrayList<>();

        private VocabularyWordDraft(int itemOrder) {
            this.itemOrder = itemOrder;
        }

        private ParsedVocabularyWord toParsedWord() {
            return new ParsedVocabularyWord(
                    itemOrder,
                    english,
                    chinese,
                    partOfSpeech,
                    sortOrder,
                    List.copyOf(errors),
                    List.copyOf(warnings)
            );
        }
    }
}
