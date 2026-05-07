package com.onepass.practice.common;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestLoggingFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String requestId = resolveRequestId(request);
        String path = buildPath(request);
        long startedAt = System.nanoTime();

        response.setHeader("X-Request-Id", requestId);
        log.info("[{}] -> {} {}", requestId, request.getMethod(), path);

        try {
            filterChain.doFilter(request, response);
        } finally {
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            log.info(
                    "[{}] <- {} {} status={} durationMs={}",
                    requestId,
                    request.getMethod(),
                    path,
                    response.getStatus(),
                    durationMs
            );
        }
    }

    private String resolveRequestId(HttpServletRequest request) {
        String headerValue = request.getHeader("X-Request-Id");
        if (StringUtils.hasText(headerValue)) {
            return headerValue;
        }
        return "req-" + UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private String buildPath(HttpServletRequest request) {
        String queryString = request.getQueryString();
        if (!StringUtils.hasText(queryString)) {
            return request.getRequestURI();
        }
        return request.getRequestURI() + "?" + queryString;
    }
}
