package com.s3.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

@Configuration

public class S3Config {

    @Value("${aws.access.key}")
    private String awsAccessKey;
    @Value("${aws.secret.key}")
    private String awsSecretKey;
    private String bucketName;
    @Value("${aws.region}")
    private String region;
    @Value("${aws.endpoint.override}")
    private String endpointOverride;

    //S3 Client Sync

    @Bean
    public S3Client getS3Client() {
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return S3Client.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }
    //S3 Client Async

    @Bean
    S3AsyncClient getS3AsyncClient() {
        AwsCredentials basicCredentials = AwsBasicCredentials.create(awsAccessKey, awsSecretKey);
        return S3AsyncClient.builder()
                .region(Region.of(region))
                .endpointOverride(URI.create(endpointOverride))
                .credentialsProvider(StaticCredentialsProvider.create(basicCredentials))
                .build();
    }

}
