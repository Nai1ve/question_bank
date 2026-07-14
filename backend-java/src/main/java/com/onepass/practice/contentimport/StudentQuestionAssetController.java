package com.onepass.practice.contentimport;

import com.onepass.practice.common.AppException;
import com.onepass.practice.contentimport.persistence.QuestionAssetDO;
import com.onepass.practice.contentimport.persistence.QuestionAssetMapper;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/question-assets")
@ConditionalOnProperty(prefix = "app.mock", name = "enabled", havingValue = "false")
public class StudentQuestionAssetController {

    private final ContentImportProperties properties;
    private final QuestionAssetMapper assetMapper;

    public StudentQuestionAssetController(ContentImportProperties properties, QuestionAssetMapper assetMapper) {
        this.properties = properties;
        this.assetMapper = assetMapper;
    }

    @GetMapping("/{questionId}/{filename:.+}")
    public ResponseEntity<byte[]> readQuestionAsset(
            @PathVariable String questionId,
            @PathVariable String filename
    ) {
        String relativePath = "assets/" + filename;
        QuestionAssetDO asset = assetMapper.selectByQuestionIdAndRelativePath(questionId, relativePath);
        if (asset == null) {
            throw new AppException("题目资源不存在");
        }

        Path storageRoot = Path.of(properties.getStorageRoot()).toAbsolutePath().normalize();
        Path assetPath = storageRoot
                .resolve(asset.getBatchId())
                .resolve(asset.getRelativePath())
                .normalize();
        if (!assetPath.startsWith(storageRoot)) {
            throw new AppException("题目资源路径非法");
        }
        if (!Files.exists(assetPath)) {
            throw new AppException("题目资源文件不存在");
        }

        try {
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(asset.getContentType()))
                    .body(Files.readAllBytes(assetPath));
        } catch (IOException exception) {
            throw new AppException("题目资源读取失败");
        }
    }
}
