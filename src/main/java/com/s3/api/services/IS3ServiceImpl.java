package com.s3.api.services;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;
@Service
@Slf4j
class IS3ServiceImpl implements IS3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${spring.destination.folder}")
    private String destinationFolder;

    private S3AsyncClient s3AsyncClient;

    @Override
    public String createBucket(String bucketName) {
        var response = s3Client.createBucket(bucket->{
            bucket.bucket(bucketName);
        });
        return "Bucket created: " + bucketName + "in" +response.location();
    }

    @Override
    public String checkBucketExist(String bucketName) {
        try{
            s3Client.headBucket(bucket -> bucket.bucket(bucketName));
            return "Bucket exist: " + bucketName;
        }catch (S3Exception e){
            return "Bucket does not exist: " + bucketName;
        }
    }

    @Override
    public List<String> getAllBuckets() {
        ListBucketsResponse bucketList = s3Client.listBuckets();
        if(bucketList.hasBuckets()){
            return bucketList.buckets().stream()
                    .map(Bucket::name)
                    .toList();
        }
        return List.of();
    }

    @Override
    public Boolean uploadFile(String bucketName, String key, Path filePath) {
        //log.info("Uploading file: {} to bucket name: {} from file path: {}", key, bucketName, filePath);
        //PUT

       var response = s3Client.putObject(PutObjectRequest.builder().bucket(bucketName).key(key).build(), filePath);
       return response.sdkHttpResponse().isSuccessful();
    }

    @Override
    public void downloadFile(String bucketName, String key) throws IOException {
        //GET
        GetObjectRequest request = GetObjectRequest.builder().bucket(bucketName).key(key).build();

        ResponseBytes<GetObjectResponse> objectAsBytes = s3Client.getObjectAsBytes(request);

        var fileName = key.contains("/") ? key.substring(key.lastIndexOf("/")) : key;

        var filePath = Paths.get(destinationFolder,fileName).toString();

        File file = new File(filePath);
        file.getParentFile().mkdir(); // para crear carpeta en caso que no exista
        try (FileOutputStream fileOutputStream = new FileOutputStream(file)) {
          fileOutputStream.write(objectAsBytes.asByteArray());

        } catch (IOException e){
            //log.error(e.getMessage() + "Al descargar el archivo" + filePath + e.getCause());
            throw new IOException(e.getMessage());
        }
    }

    @Override
    public String generatePresignedUploadUrl(String bucketName, String key, Duration duration) {
        return "";
    }

    @Override
    public String generatePresignedDownloadUrl(String bucketName, String key, Duration duration) {
        return "";
    }
}
