package com.onepass.practice.category;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CategoryView(
        String id,
        String name,
        String subtitle,
        @JsonProperty("is_leaf") boolean isLeaf
) {
}
