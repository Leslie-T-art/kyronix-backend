package com.kyronic.riskengine.processflows.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "kyronic.storage.minio")
public record MinioStorageProperties(
        String endpoint,
        String accessKey,
        String secretKey
) {
}
