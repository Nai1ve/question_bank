package com.onepass.practice.tag;

import com.onepass.practice.common.ApiResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/tags")
public class StudentTagController {

    private static final Logger log = LoggerFactory.getLogger(StudentTagController.class);

    private final TagService tagService;

    public StudentTagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping
    public ApiResponse<List<TagView>> listTags(@RequestParam(required = false) String categoryId) {
        List<TagView> tags = tagService.listTags(categoryId);
        log.info("Listed tags categoryId={} count={}", categoryId == null ? "" : categoryId, tags.size());
        return ApiResponse.ok(tags);
    }
}
