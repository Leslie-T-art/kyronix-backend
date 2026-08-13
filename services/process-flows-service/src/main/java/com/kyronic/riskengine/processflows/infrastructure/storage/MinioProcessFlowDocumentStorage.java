package com.kyronic.riskengine.processflows.infrastructure.storage;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.Instant;

@Component
public class MinioProcessFlowDocumentStorage {

    private final MinioClient minioClient;

    public MinioProcessFlowDocumentStorage(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    public StoredDocument store(Long departmentId, String flowReference, MultipartFile multipartFile) {
        try {
            String bucketName = bucketName(departmentId);
            ensureBucket(bucketName);
            String safeFileName = multipartFile.getOriginalFilename() == null ? "process-flow-document" : multipartFile.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_");
            String objectKey = flowReference + "/" + Instant.now().toEpochMilli() + "-" + safeFileName;
            try (InputStream inputStream = multipartFile.getInputStream()) {
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectKey)
                                .stream(inputStream, multipartFile.getSize(), -1)
                                .contentType(multipartFile.getContentType())
                                .build()
                );
            }
            return new StoredDocument(
                    multipartFile.getOriginalFilename(),
                    multipartFile.getContentType() == null ? "application/octet-stream" : multipartFile.getContentType(),
                    multipartFile.getSize(),
                    bucketName,
                    objectKey
            );
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to store process flow document", exception);
        }
    }

    public byte[] read(String bucketName, String objectKey) {
        try (InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder().bucket(bucketName).object(objectKey).build()
        )) {
            return inputStream.readAllBytes();
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to read process flow document", exception);
        }
    }

    public void delete(String bucketName, String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectKey).build());
        } catch (Exception exception) {
            throw new IllegalStateException("Unable to delete process flow document", exception);
        }
    }

    private void ensureBucket(String bucketName) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
        }
    }

    private String bucketName(Long departmentId) {
        return "process-flows-dept-" + departmentId;
    }
}
