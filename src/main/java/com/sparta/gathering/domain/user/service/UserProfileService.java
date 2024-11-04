package com.sparta.gathering.domain.user.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final S3Client s3Client;
    private final UserRepository userRepository;

    @Value("${default.profile.image.url}")
    private String defaultProfileImageUrl;

    @Value("${cloud.aws.s3.bucket-name}")
    private String bucketName;

    // 이미지 수정
    public String updateProfileImage(UserDTO userDto, UUID userId, MultipartFile newImage) {
        isValidUser(userDto, userId);
        validateFile(newImage);

        // 기존 이미지 삭제
        ListObjectsV2Request listRequest = findImage(userId);
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        listResponse.contents().forEach(object ->
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build())
        );

        // 새로운 파일 이름 설정 (UUID)
        String fileName = userId + "_" + UUID.randomUUID() + "_" + newImage.getOriginalFilename();

        // S3에 파일 업로드
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(fileName)
                            .contentType(newImage.getContentType())
                            .build(),
                    RequestBody.fromInputStream(newImage.getInputStream(), newImage.getSize())
            );
        } catch (IOException e) {
            throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        user.setUpdateProfileImage(fileName);
        userRepository.save(user);

        return s3Client.utilities().getUrl(GetUrlRequest.builder().bucket(bucketName).key(fileName).build()).toString();
    }

    // 이미지 조회 - 누구나 조회 가능
    @Transactional(readOnly = true)
    public String getUserProfileImages(UUID userId) {
        // S3에서 이미지 조회
        ListObjectsV2Request listRequest = findImage(userId);
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        // S3에 이미지가 없으면 기본 이미지 URL 반환
        if (listResponse.contents().isEmpty()) {
            return defaultProfileImageUrl;
        }

        // 이미지가 있으면 첫 번째 객체의 URL 반환
        return s3Client.utilities().getUrl(GetUrlRequest.builder()
                .bucket(bucketName)
                .key(listResponse.contents().get(0).key())
                .build()).toString();
    }

    // 이미지 삭제
    public void deleteUserProfileImage(UserDTO userDto, UUID userId) {
        isValidUser(userDto, userId);

        ListObjectsV2Request listRequest = findImage(userId);
        ListObjectsV2Response listResponse = s3Client.listObjectsV2(listRequest);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        listResponse.contents().forEach(object -> {
            try {
                s3Client.deleteObject(DeleteObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build());
            } catch (S3Exception e) {
                throw new BaseException(ExceptionEnum.DELETE_FAILED);
            }
        });

        user.setDeleteProfileImage();
        userRepository.save(user);
    }

    // 파일 유효성 확인
    private void validateFile(MultipartFile file) {
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BaseException(ExceptionEnum.INVALID_FILE_SIZE);
        }

        String contentType = file.getContentType();
        if (contentType == null || !isSupportedContentType(contentType)) {
            throw new BaseException(ExceptionEnum.INVALID_FILE_TYPE);
        }
    }

    // 지원하는 파일 형식 확인
    private boolean isSupportedContentType(String contentType) {
        return List.of("image/jpeg", "image/png").contains(contentType);
    }

    // 유저 아이디와 유저 토큰이 맞는지 검증
    public void isValidUser(UserDTO userDto, UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        if (!userDto.getUserId().equals(userId)) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        }
    }

    // 사용자에게 이미 등록된 이미지가 있는지 확인
    public ListObjectsV2Request findImage(UUID userId) {
        return ListObjectsV2Request.builder()
                .bucket(bucketName)
                .prefix(userId + "_")
                .build();
    }
}