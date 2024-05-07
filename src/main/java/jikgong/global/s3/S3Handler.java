package jikgong.global.s3;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import jikgong.global.exception.CustomException;
import jikgong.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3Handler {
    private final AmazonS3Client amazonS3Client;
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Value("${cloud.aws.s3.resize-bucket}")
    private String resize_bucket;


    public ImageDto uploadCertification(MultipartFile file) {
        String extension; //확장자명
        String contentType = file.getContentType();

        if (ObjectUtils.isEmpty(contentType)) {
            throw new CustomException(ErrorCode.FILE_NOT_FOUND_EXTENSION);
        } else { //확장자명이 jpeg, png 인 파일들만 받아서 처리
            if (contentType.contains("image/jpeg")) extension = ".jpg";
            else if (contentType.contains("image/png")) extension = ".png";
            else {
                log.error("사진이 아닌 파일 입니다.");
                throw new CustomException(ErrorCode.FILE_NOT_SUPPORTED);
            }
        }

        // unique 이름 생성
        String storeImageName = "certification/" + createStoreImageName(extension);

        InputStream inputStream = null;
        try {
            inputStream = file.getInputStream();
        } catch (IOException e) {
            throw new CustomException(ErrorCode.IMAGE_EXCEPTION);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        PutObjectRequest request = new PutObjectRequest(bucket, storeImageName, inputStream, metadata);

        // s3 put
        amazonS3Client.putObject(request);
        log.info("s3에 사진 저장");

        // s3 get
        String s3Url = getImgPath(storeImageName);

        return ImageDto.builder()
                .s3Url(s3Url)
                .storeImgName(storeImageName)
                .build();
    }

    public List<ImageDto> uploadImageList(List<MultipartFile> files) {
        List<ImageDto> imageDtoList = new ArrayList<>();
        if (files == null) {
            return imageDtoList;
        }

        // 첫번째 이미지는 썸네일로 등록
        boolean isFirst = true; // 첫 번째 이미지를 식별하기 위한 플래그

        for (MultipartFile file : files) {
            String extension; //확장자명
            String contentType = file.getContentType();

            if (ObjectUtils.isEmpty(contentType)) {
                throw new CustomException(ErrorCode.FILE_NOT_FOUND_EXTENSION);
            } else { //확장자명이 jpeg, png 인 파일들만 받아서 처리
                if (contentType.contains("image/jpeg")) extension = ".jpg";
                else if (contentType.contains("image/png")) extension = ".png";
                else {
                    log.error("사진이 아닌 파일 입니다.");
                    throw new CustomException(ErrorCode.FILE_NOT_SUPPORTED);
                }
            }
            // unique 이름 생성
            String storeImageName = createStoreImageName(extension);
            // 썸네일 이미지라면 Prefix를 등록해 AWS Lambda가 실행되도록 세팅
            String prefix = isFirst ? "thumbnail_" : "";
            storeImageName = "jobPost/" + prefix + storeImageName;


            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                throw new CustomException(ErrorCode.IMAGE_EXCEPTION);
            }
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            PutObjectRequest request = new PutObjectRequest(bucket, storeImageName, inputStream, metadata);

            // s3 put
            amazonS3Client.putObject(request);
            log.info("s3에 사진 저장");

            // s3 get
            String s3Url = getImgPath(storeImageName);

            ImageDto imageDto = ImageDto.builder()
                    .s3Url(s3Url)
                    .storeImgName(storeImageName)
                    .isThumbnail(isFirst) // 첫 번째 이미지만 썸네일로 설정
                    .build();
            imageDtoList.add(imageDto);

            isFirst = false; // 첫 번째 이미지 처리 후, 플래그 변경
        }
        return imageDtoList;
    }

    public void deleteImage(List<String> storeImgList) {
        for (String storeImgName : storeImgList) {
            DeleteObjectRequest deleteObjectRequest = new DeleteObjectRequest(bucket, storeImgName);
            amazonS3Client.deleteObject(deleteObjectRequest);
        }
        log.info("S3 파일 삭제 완료");
    }

    // 저장할 이름 생성
    private String createStoreImageName(String extension) {
        String uuid = UUID.randomUUID().toString();
        return uuid + extension;
    }

    // 버킷에서 이미지 조회
    public String getImgPath(String fileName) {
        return amazonS3Client.getUrl(bucket, fileName).toString();
    }

    // resize버킷에서 이미지 조회
    public String getThumbnailImgPath(String fileName) {
        return amazonS3Client.getUrl(resize_bucket, fileName).toString();
    }
}
