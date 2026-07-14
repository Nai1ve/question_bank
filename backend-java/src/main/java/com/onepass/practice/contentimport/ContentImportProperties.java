package com.onepass.practice.contentimport;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.import")
public class ContentImportProperties {

    private String storageRoot = "import-storage";
    private String defaultCategoryPath = "考研/政治/基础阶段";
    private String adminToken = "dev-admin-import-token";

    public String getStorageRoot() {
        return storageRoot;
    }

    public void setStorageRoot(String storageRoot) {
        this.storageRoot = storageRoot;
    }

    public String getDefaultCategoryPath() {
        return defaultCategoryPath;
    }

    public void setDefaultCategoryPath(String defaultCategoryPath) {
        this.defaultCategoryPath = defaultCategoryPath;
    }

    public String getAdminToken() {
        return adminToken;
    }

    public void setAdminToken(String adminToken) {
        this.adminToken = adminToken;
    }
}
