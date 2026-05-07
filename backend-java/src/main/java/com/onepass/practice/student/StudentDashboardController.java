package com.onepass.practice.student;

import com.onepass.practice.auth.StudentIdentityService;
import com.onepass.practice.common.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student/dashboard")
public class StudentDashboardController {

    private static final Logger log = LoggerFactory.getLogger(StudentDashboardController.class);

    private final StudentIdentityService studentIdentityService;
    private final StudentDashboardService studentDashboardService;

    public StudentDashboardController(
            StudentIdentityService studentIdentityService,
            StudentDashboardService studentDashboardService
    ) {
        this.studentIdentityService = studentIdentityService;
        this.studentDashboardService = studentDashboardService;
    }

    @GetMapping
    public ApiResponse<StudentDashboardResponse> getDashboard(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader
    ) {
        Long studentId = studentIdentityService.requireStudentId(authorizationHeader);
        log.info("Received student dashboard request studentId={}", studentId);
        return ApiResponse.ok(studentDashboardService.getDashboard(studentId));
    }
}
