package com.onepass.practice.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

@Component
public class JsonResourceReader {

    private final ObjectMapper objectMapper;

    public JsonResourceReader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public <T> T read(String classpathLocation, TypeReference<T> typeReference) {
        try (InputStream inputStream = new ClassPathResource(classpathLocation).getInputStream()) {
            return objectMapper.readValue(inputStream, typeReference);
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to load resource: " + classpathLocation, exception);
        }
    }
}
