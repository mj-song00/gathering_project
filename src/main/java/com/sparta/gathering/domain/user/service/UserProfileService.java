package com.sparta.gathering.domain.user.service;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.user.dto.response.UserDTO;
import com.sparta.gathering.domain.user.entity.User;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UserProfileService {

    private final AmazonS3 s3Client;
    private final UserRepository userRepository;

    @Value("${DEFAULT_PROFILE_IMAGE_URL}")
    private String defaultProfileImageUrl;

    @Value("${S3_BUCKET_NAME}")
    private String bucketName;


    // 이미지 수정
    public String updateProfileImage(UserDTO userDto, UUID userId, MultipartFile newImage) {
        isValidUser(userDto, userId);
        validateFile(newImage);
        ListObjectsV2Request file = findImage(userId);

        ListObjectsV2Result result = s3Client.listObjectsV2(file);
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            s3Client.deleteObject(bucketName, objectSummary.getKey()); // 삭제
        }

        // 파일 이름 중복 방지를 위해 UUID 사용
        String fileName = userId + "_" + UUID.randomUUID() + "_" + newImage.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(newImage.getSize());
        metadata.setContentType(newImage.getContentType());

        try {
            // S3에 파일 업로드
            s3Client.putObject(bucketName, fileName, newImage.getInputStream(), metadata);
        } catch (Exception e) {
            throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
        }

        User newuser = userRepository.findById(userId).orElse(null);
        newuser.setUpdateProfileImage(fileName);
        userRepository.save(newuser);

        return s3Client.getUrl(bucketName, fileName).toString();
    }

    // 이미지 조회 - 누구나 조회 가능
    @Transactional(readOnly = true)
    public String getUserProfileImages(UUID userId) {
        ListObjectsV2Request file = findImage(userId);
        // 사용자에게 이미 등록된 이미지가 있는지 확인
        User user = userRepository.findById(userId).orElse(null);
        if (user.getProfileImage().equals(defaultProfileImageUrl) || user.getProfileImage().equals("NONE")) {
            return defaultProfileImageUrl; // 기본이미지
        }
        ListObjectsV2Result result = s3Client.listObjectsV2(file);
        return s3Client.getUrl(bucketName, result.getObjectSummaries().get(0).getKey())
                .toString();
    }

    // 이미지 삭제
    public void deleteUserProfileImage(UserDTO userDto, UUID userId) {
        isValidUser(userDto, userId);
        User newUser = userRepository.findById(userId).orElse(null);
        // 사용자에게 이미 등록된 이미지가 있는지 확인
        ListObjectsV2Request file = findImage(userId);
        if (file == null) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED);
        }

        ListObjectsV2Result result = s3Client.listObjectsV2(file);
        for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
            try {
                s3Client.deleteObject(bucketName, objectSummary.getKey());
            } catch (SdkClientException e) {
                throw new BaseException(ExceptionEnum.DELETE_FAILED);
            }
        }
        newUser.setDeleteProfileImage();
        userRepository.save(newUser);
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

    // validateFile 에 사용
    private boolean isSupportedContentType(String contentType) {
        return List.of("image/jpeg", "image/png").contains(contentType);
    }

    // 유저 아이디와 유저토큰이 맞는지 검증
    public void isValidUser(UserDTO userDto, UUID userId) throws BaseException {
        userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));
        if (!userDto.getUserId().equals(userId)) {
            throw new BaseException(ExceptionEnum.PERMISSION_DENIED_ROLE);
        }
    }

    // 사용자에게 이미 등록된 이미지가 있는지 확인
    public ListObjectsV2Request findImage(UUID userId) {
        return new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(userId + "_");
    }
}
