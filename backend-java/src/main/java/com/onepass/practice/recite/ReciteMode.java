package com.onepass.practice.recite;

import com.onepass.practice.common.AppException;

public enum ReciteMode {
    CN_TO_EN("cn_to_en"),
    EN_TO_CN("en_to_cn");

    private final String value;

    ReciteMode(String value) {
        this.value = value;
    }

    public String value() {
        return value;
    }

    public static ReciteMode fromValue(String value) {
        for (ReciteMode mode : values()) {
            if (mode.value.equals(value)) {
                return mode;
            }
        }
        throw new AppException("Unsupported recite mode");
    }
}
