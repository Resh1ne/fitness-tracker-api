package com.example.fitnesstracker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.NoSuchBucketException;

import java.net.URI;

@Configuration
public class S3Config {

    @Value("${s3.endpoint}")
    private String endpoint;
    @Value("${s3.access-key}")
    private String accessKey;
    @Value("${s3.secret-key}")
    private String secretKey;
    @Value("${s3.bucket-name}")
    private String bucketName;

    @Bean
    public S3Client s3Client() {
        S3Client client = S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(accessKey, secretKey)))
                .forcePathStyle(true)
                .build();

        createBucketIfNotExists(client);

        return client;
    }

    private void createBucketIfNotExists(S3Client client) {
        try {
            client.headBucket(HeadBucketRequest.builder().bucket(bucketName).build());
        } catch (NoSuchBucketException e) {
            client.createBucket(CreateBucketRequest.builder().bucket(bucketName).build());
        }
    }
}