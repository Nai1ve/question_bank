package com.onepass.practice.topxx;

import com.onepass.practice.auth.StudentIdentityService;
import com.onepass.practice.common.ApiResponse;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/wrong-book")
public class StudentWrongBookController {

    private static final Logger log = LoggerFactory.getLogger(StudentWrongBookController.class);

    private final StudentIdentityService studentIdentityService;
    private final WrongBookService wrongBookService;

    public StudentWrongBookController(
            StudentIdentityService studentIdentityService,
            WrongBookService wrongBookService
    ) {
        this.studentIdentityService = studentIdentityService;
        this.wrongBookService = wrongBookService;
    }

    @GetMapping
    public ApiResponse<List<WrongBookItemView>> listWrongBook(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestParam(defaultValue = "20") Integer limit
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        List<WrongBookItemView> items = wrongBookService.listWrongBook(studentId, limit);
        log.info("Listed wrong book items studentId={} count={}", studentId, items.size());
        return ApiResponse.ok(items);
    }
}
