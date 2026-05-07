package com.onepass.practice.category;

public record CategoryNode(
        String id,
        String parentId,
        String name,
        String subtitle,
        boolean isLeaf
) {
}

