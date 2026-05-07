package com.onepass.practice.practice;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.practice")
public class PracticeProperties {

    private int sessionRetentionDays = 7;

    public int getSessionRetentionDays() {
        return sessionRetentionDays;
    }

    public void setSessionRetentionDays(int sessionRetentionDays) {
        this.sessionRetentionDays = sessionRetentionDays;
    }
}
