package com.s3.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.s3.api.services.IS3Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("s3")
public class AppController {

    @Autowired
    private IS3Service s3Service;

    @Value("${spring.destination.folder}")
    private String destinationFolder;



    @PostMapping("/create")
    public ResponseEntity<String> createBucket(@RequestParam String bucketName) {
        return ResponseEntity.ok(s3Service.createBucket(bucketName));
    }

    @GetMapping("/check/{bucketName}")
    public ResponseEntity<String> checkBucket(@PathVariable("bucketName") String bucketName) {
        return new ResponseEntity<>(s3Service.checkBucketExist(bucketName), HttpStatus.OK);
    }

    @GetMapping("/list")
    public ResponseEntity<List<String>> listBuckets() {
        return new ResponseEntity<>(s3Service.getAllBuckets(),HttpStatus.OK);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String bucketName,
                                             @RequestParam String key,
                                             @RequestPart MultipartFile file) {
        try{
            Path staticDir = Paths.get(destinationFolder);
            if (!Files.exists(staticDir)) {
                Files.createDirectories(staticDir);
            }

            Path filePath = staticDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));
            Path finalPath = Files.write(filePath, file.getBytes());
            var result = s3Service.uploadFile(bucketName,key,finalPath);
            if(result){
                Files.delete(filePath);
                return ResponseEntity.ok("File uploaded successfully");
            }else{
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
            }

        }catch (IOException e){
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/download")
    public ResponseEntity<String> downloadFile(@RequestParam String bucketName,
                                               @RequestParam String key) throws IOException {
        s3Service.downloadFile(bucketName,key);
        return ResponseEntity.ok("File downloaded successfully");
    }
}
