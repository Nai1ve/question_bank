package com.onepass.practice.student;

import com.fasterxml.jackson.core.type.TypeReference;
import com.onepass.practice.common.AppException;
import com.onepass.practice.common.JsonResourceReader;
import com.onepass.practice.student.persistence.StudentProfileDO;
import com.onepass.practice.student.persistence.StudentProfileMapper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StudentProfileCatalog {

    private static final Logger log = LoggerFactory.getLogger(StudentProfileCatalog.class);

    private final boolean mockEnabled;
    private final List<StudentProfileData> mockProfiles;
    private final StudentProfileMapper studentProfileMapper;

    public StudentProfileCatalog(
            @Value("${app.mock.enabled:true}") boolean mockEnabled,
            JsonResourceReader jsonResourceReader,
            ObjectProvider<StudentProfileMapper> studentProfileMapperProvider
    ) {
        this.mockEnabled = mockEnabled;
        this.studentProfileMapper = studentProfileMapperProvider.getIfAvailable();
        this.mockProfiles = List.copyOf(
                jsonResourceReader.read("mock-data/students.json", new TypeReference<List<StudentProfileData>>() {
                })
        );
        log.info("Loaded student profile catalog mode={}", mockEnabled ? "resource" : "mysql");
    }

    public StudentProfileData requireById(Long studentId) {
        if (mockEnabled) {
            return mockProfiles.stream()
                    .filter(profile -> profile.id().equals(studentId))
                    .findFirst()
                    .orElseThrow(() -> new AppException("Student profile does not exist"));
        }

        if (studentProfileMapper == null) {
            throw new IllegalStateException("StudentProfileMapper is not available");
        }

        StudentProfileDO profile = studentProfileMapper.selectById(studentId);
        if (profile == null) {
            throw new AppException("Student profile does not exist");
        }
        return new StudentProfileData(profile.getId(), profile.getDisplayName(), profile.getAvatarUrl());
    }
}
