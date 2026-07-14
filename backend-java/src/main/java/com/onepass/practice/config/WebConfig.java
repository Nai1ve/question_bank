package com.onepass.practice.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final WebCorsProperties corsProperties;

    public WebConfig(WebCorsProperties corsProperties) {
        this.corsProperties = corsProperties;
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration registration = registry.addMapping("/**")
                .allowedMethods(toArray(corsProperties.getAllowedMethods()))
                .allowedHeaders("*")
                .exposedHeaders("Authorization")
                .maxAge(3600);

        List<String> allowedOrigins = corsProperties.getAllowedOrigins();
        if (allowedOrigins == null || allowedOrigins.isEmpty() || allowedOrigins.contains("*")) {
            registration.allowedOriginPatterns("*");
            return;
        }
        registration.allowedOrigins(toArray(allowedOrigins));
    }

    private String[] toArray(List<String> values) {
        return values.stream()
                .map(String::trim)
                .filter(value -> !value.isEmpty())
                .toArray(String[]::new);
    }
}
