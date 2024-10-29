package com.sparta.gathering.common.config.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import java.io.File;
import java.io.InputStream;
import java.util.Map;
import org.springframework.context.annotation.Bean;

public class S3Client {

    private final AmazonS3 amazonS3;
    private final String url;
    private final String bucketName;

    public S3Client(AmazonS3 amazonS3, String url, String bucketName) {
        this.amazonS3 = amazonS3;
        this.url = url;
        this.bucketName = bucketName;
    }

    public String upload(File file) {
        PutObjectRequest request = new PutObjectRequest(bucketName, file.getName(), file);
        return executePut(request);
    }

    public String upload(InputStream in, long length, String key, String contentType,
            Map<String, String> metadata) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(length);
        objectMetadata.setContentType(contentType);
        if (metadata != null && !metadata.isEmpty()) {
            objectMetadata.setUserMetadata(metadata);
        }

        PutObjectRequest request = new PutObjectRequest(bucketName, key, in, objectMetadata);
        return executePut(request);
    }

    private String executePut(PutObjectRequest request) {
        amazonS3.putObject(request.withCannedAcl(CannedAccessControlList.PublicRead));
        return url + "/" + bucketName + "/" + request.getKey();
    }

    @Bean
    public S3Client s3Client(AmazonS3 amazonS3, AwsConfigure awsConfigure) {
        return new S3Client(amazonS3, awsConfigure.getUrl(), awsConfigure.getBucketName());
    }

}
