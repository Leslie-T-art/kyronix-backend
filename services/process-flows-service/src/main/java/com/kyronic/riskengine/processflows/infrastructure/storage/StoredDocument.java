package com.kyronic.riskengine.processflows.infrastructure.storage;

public record StoredDocument(
        String originalFileName,
        String contentType,
        long fileSize,
        String bucketName,
        String objectKey
) {
}
