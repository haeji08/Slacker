package woowoong.slacker.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;

@Service
public class S3Service {

    private final S3Client s3Client;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        String bucketName = "slacker-image";
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        // S3에 파일 업로드
        s3Client.putObject(PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .build(),
                RequestBody.fromBytes(file.getBytes()));

        // S3 URL 생성
        return String.format("https://%s.s3.amazonaws.com/%s", bucketName, fileName);
    }
}

