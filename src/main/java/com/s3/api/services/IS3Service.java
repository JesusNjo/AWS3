package com.s3.api.services;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.util.List;

public interface IS3Service {
    //Create bucket
    String createBucket(String bucketName);

    //Bucket exist
    String checkBucketExist(String bucketName);

    // Bucket list

    List<String> getAllBuckets();

    //Upload file bucket

    Boolean uploadFile(String bucketName, String key, Path filePath);

    //Download files

    void downloadFile(String bucketName, String key) throws IOException;

    // URL prefirmadas upload

    String generatePresignedUploadUrl(String bucketName, String key, Duration duration);

    // URL prefirmadas download

    String generatePresignedDownloadUrl(String bucketName, String key, Duration duration);
}
