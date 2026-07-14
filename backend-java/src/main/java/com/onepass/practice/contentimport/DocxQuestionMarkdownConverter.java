package com.onepass.practice.contentimport;

import com.onepass.practice.common.AppException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.poi.xwpf.usermodel.BodyElementType;
import org.apache.poi.xwpf.usermodel.IBodyElement;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFPicture;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class DocxQuestionMarkdownConverter {

    private static final Pattern BLOCK_START = Pattern.compile("^\\s*\\d+[、.．]\\s*【.+?】.*$");
    private static final Pattern FIRST_LINE = Pattern.compile("^\\s*(\\d+)[、.．]\\s*【([^】]+)】(.*)$");
    private static final Pattern FIELD_LINE = Pattern.compile("^\\s*(?:[•\\-]\\s*)?【([^】]+)】(.*)$");
    private static final Pattern OPTION_LINE = Pattern.compile("^\\s*(?:[•\\-]\\s*)?【([A-Z])】(.*)$");

    public MarkdownConversionResult convert(
            MultipartFile file,
            String batchId,
            Path batchDir,
            String defaultCategoryPath
    ) {
        try {
            Files.createDirectories(batchDir.resolve("assets"));
            List<ExtractedImportAsset> assets = new ArrayList<>();
            List<String> rawLines = extractLines(file, batchDir, assets);
            String markdown = normalizeToStandardMarkdown(rawLines, file.getOriginalFilename(), defaultCategoryPath);
            Path markdownPath = batchDir.resolve("questions.md");
            Files.writeString(markdownPath, markdown, StandardCharsets.UTF_8);
            return new MarkdownConversionResult(markdownPath, markdown, assets);
        } catch (IOException exception) {
            throw new AppException("Docx 文件解析失败");
        }
    }

    private List<String> extractLines(MultipartFile file, Path batchDir, List<ExtractedImportAsset> assets) throws IOException {
        try (InputStream inputStream = file.getInputStream(); XWPFDocument document = new XWPFDocument(inputStream)) {
            List<String> lines = new ArrayList<>();
            int[] imageCounter = new int[]{1};
            for (IBodyElement element : document.getBodyElements()) {
                if (element.getElementType() == BodyElementType.PARAGRAPH) {
                    appendParagraph(lines, (XWPFParagraph) element, batchDir, assets, imageCounter);
                } else if (element.getElementType() == BodyElementType.TABLE) {
                    appendTable(lines, (XWPFTable) element);
                }
            }
            return lines;
        }
    }

    private void appendParagraph(
            List<String> lines,
            XWPFParagraph paragraph,
            Path batchDir,
            List<ExtractedImportAsset> assets,
            int[] imageCounter
    ) throws IOException {
        String text = cleanLine(paragraph.getText());
        if (!text.isBlank()) {
            lines.add(text);
        }

        for (XWPFRun run : paragraph.getRuns()) {
            for (XWPFPicture picture : run.getEmbeddedPictures()) {
                byte[] data = picture.getPictureData().getData();
                String extension = safeExtension(picture.getPictureData().suggestFileExtension());
                String imageName = "image" + imageCounter[0] + "." + extension;
                imageCounter[0] += 1;
                Path target = batchDir.resolve("assets").resolve(imageName);
                Files.write(target, data);
                String relativePath = "assets/" + imageName;
                assets.add(new ExtractedImportAsset(
                        picture.getPictureData().getFileName(),
                        relativePath,
                        picture.getPictureData().getPackagePart().getContentType(),
                        data.length
                ));
                lines.add("![image](" + relativePath + ")");
            }
        }
    }

    private void appendTable(List<String> lines, XWPFTable table) {
        List<List<String>> rows = new ArrayList<>();
        for (XWPFTableRow row : table.getRows()) {
            List<String> cells = new ArrayList<>();
            for (XWPFTableCell cell : row.getTableCells()) {
                cells.add(cleanLine(cell.getText()).replace("|", "\\|"));
            }
            if (!cells.isEmpty()) {
                rows.add(cells);
            }
        }
        if (rows.isEmpty()) {
            return;
        }
        lines.add(toMarkdownRow(rows.get(0)));
        lines.add(toMarkdownSeparator(rows.get(0).size()));
        for (int index = 1; index < rows.size(); index += 1) {
            lines.add(toMarkdownRow(rows.get(index)));
        }
    }

    private String normalizeToStandardMarkdown(List<String> rawLines, String filename, String defaultCategoryPath) {
        List<List<String>> blocks = splitBlocks(rawLines);
        List<String> markdownBlocks = new ArrayList<>();
        for (List<String> block : blocks) {
            String normalized = normalizeBlock(block, filename, defaultCategoryPath);
            if (!normalized.isBlank()) {
                markdownBlocks.add(normalized);
            }
        }
        return String.join("\n\n", markdownBlocks) + "\n";
    }

    private List<List<String>> splitBlocks(List<String> lines) {
        List<List<String>> blocks = new ArrayList<>();
        List<String> current = new ArrayList<>();
        for (String line : lines) {
            String value = cleanLine(line);
            if (value.isBlank()) {
                continue;
            }
            if ("#####".equals(value) || (BLOCK_START.matcher(value).matches() && !current.isEmpty())) {
                if (!current.isEmpty()) {
                    blocks.add(current);
                    current = new ArrayList<>();
                }
                if (!"#####".equals(value)) {
                    current.add(value);
                }
                continue;
            }
            current.add(value);
        }
        if (!current.isEmpty()) {
            blocks.add(current);
        }
        return blocks;
    }

    private String normalizeBlock(List<String> lines, String filename, String defaultCategoryPath) {
        if (lines.isEmpty()) {
            return "";
        }

        String sourceQuestionNo = "";
        String questionType = "";
        String categoryPath = defaultCategoryPath;
        String answer = "";
        String knowledgePoint = "";
        String difficulty = "";
        String microCourseId = "";
        List<String> stemLines = new ArrayList<>();
        List<String> optionLines = new ArrayList<>();
        List<String> analysisLines = new ArrayList<>();
        String section = "stem";

        Matcher firstLineMatcher = FIRST_LINE.matcher(lines.get(0));
        int startIndex = 0;
        if (firstLineMatcher.matches()) {
            sourceQuestionNo = firstLineMatcher.group(1).trim();
            questionType = firstLineMatcher.group(2).trim();
            String firstStem = firstLineMatcher.group(3).trim();
            if (!firstStem.isBlank()) {
                stemLines.add(firstStem);
            }
            startIndex = 1;
        }

        for (int index = startIndex; index < lines.size(); index += 1) {
            String line = lines.get(index);
            Matcher optionMatcher = OPTION_LINE.matcher(line);
            if (optionMatcher.matches()) {
                optionLines.add(optionMatcher.group(1).trim() + ". " + optionMatcher.group(2).trim());
                section = "options";
                continue;
            }

            Matcher fieldMatcher = FIELD_LINE.matcher(line);
            if (fieldMatcher.matches()) {
                String fieldName = fieldMatcher.group(1).trim();
                String fieldValue = fieldMatcher.group(2).trim();
                switch (fieldName) {
                    case "分类" -> {
                    }
                    case "标签" -> {
                    }
                    case "题型" -> questionType = fieldValue;
                    case "题干" -> {
                        section = "stem";
                        appendIfPresent(stemLines, fieldValue);
                    }
                    case "选项" -> section = "options";
                    case "正确答案", "参考答案", "答案" -> answer = fieldValue;
                    case "知识点" -> knowledgePoint = fieldValue;
                    case "难易度" -> difficulty = fieldValue;
                    case "题目解析", "解析" -> {
                        section = "analysis";
                        appendIfPresent(analysisLines, fieldValue);
                    }
                    case "微课ID" -> microCourseId = fieldValue;
                    case "来源题号" -> sourceQuestionNo = fieldValue;
                    default -> appendBySection(section, stemLines, optionLines, analysisLines, line);
                }
                continue;
            }

            if (line.startsWith("![image](")) {
                stemLines.add(line);
                continue;
            }
            appendBySection(section, stemLines, optionLines, analysisLines, line);
        }

        List<String> output = new ArrayList<>();
        output.add("#####");
        output.add("来源题号【" + sourceQuestionNo + "】");
        output.add("分类【" + categoryPath + "】");
        output.add("标签【】");
        output.add("题型【" + questionType + "】");
        output.add("");
        output.add("题干：");
        output.addAll(stemLines);
        output.add("");
        output.add("选项：");
        output.addAll(optionLines);
        output.add("");
        output.add("答案：");
        output.add(answer);
        output.add("");
        output.add("解析：");
        output.addAll(analysisLines.isEmpty() ? List.of("暂无解析") : analysisLines);
        output.add("");
        output.add("知识点【" + knowledgePoint + "】");
        output.add("难易度【" + difficulty + "】");
        output.add("微课ID【" + microCourseId + "】");
        output.add("来源【" + nullToEmpty(filename) + "】");
        return String.join("\n", output);
    }

    private void appendIfPresent(List<String> target, String value) {
        if (value != null && !value.isBlank()) {
            target.add(value);
        }
    }

    private void appendBySection(
            String section,
            List<String> stemLines,
            List<String> optionLines,
            List<String> analysisLines,
            String line
    ) {
        if ("analysis".equals(section)) {
            analysisLines.add(line);
        } else if ("options".equals(section) && line.matches("^[A-Z][.．、].*")) {
            optionLines.add(line.replaceFirst("^([A-Z])[.．、]\\s*", "$1. "));
        } else {
            stemLines.add(line);
        }
    }

    private String toMarkdownRow(List<String> cells) {
        return "| " + String.join(" | ", cells) + " |";
    }

    private String toMarkdownSeparator(int size) {
        List<String> cells = new ArrayList<>();
        for (int index = 0; index < size; index += 1) {
            cells.add("---");
        }
        return toMarkdownRow(cells);
    }

    private String cleanLine(String value) {
        return nullToEmpty(value)
                .replace('\u00A0', ' ')
                .replace('\u2028', ' ')
                .replace('\u2029', ' ')
                .trim();
    }

    private String safeExtension(String extension) {
        String value = nullToEmpty(extension).toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]", "");
        return value.isBlank() ? "bin" : value;
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }
}
