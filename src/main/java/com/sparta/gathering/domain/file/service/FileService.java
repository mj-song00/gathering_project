package com.sparta.gathering.domain.file.service;


import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.file.dto.response.FileResponse;
import com.sparta.gathering.domain.file.entity.File;
import com.sparta.gathering.domain.file.repository.FileRepository;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public String saveImage(MultipartFile file, Long gatherId, AuthenticatedUser authenticatedUser) {
        validateManager(gatherId, authenticatedUser);
        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        final Path path = Paths.get("upload", fileName);

        try {
            Files.copy(file.getInputStream(), path);
            Gather gather = gatherRepository.findById(gatherId).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
            File image = new File(file.getOriginalFilename(), fileName, path.toString(), gather);
            fileRepository.save(image);

            Resource resource = new FileSystemResource(path.toFile());
            return resource.getFile().getPath();
        } catch (IOException e) {
            throw new BaseException(ExceptionEnum.UPLOAD_FAILED);
        }
    }

    public List<FileResponse> getImage(Long gatherId) {
        List<File> files = fileRepository.findByGatherId(gatherId);
        if (files.isEmpty()) {
            throw new BaseException(ExceptionEnum.FILE_NOT_FOUND);
        }

        // File 리스트를 FileResponse 리스트로 변환
        return files.stream()
                .map(file -> new FileResponse(file)) // 각 파일을 FileResponse로 변환
                .collect(Collectors.toList());
    }

    public void deleteImage(Long fileId, AuthenticatedUser authenticatedUser) {
        validateManager(fileId, authenticatedUser);
        System.out.println("fileId: " + fileId);
        File image = fileRepository.findById(fileId).orElseThrow(() -> new BaseException(ExceptionEnum.IMAGE_NOT_FOUND));

        fileRepository.delete(image);
    }

    // Manager 권한 확인
    private void validateManager(Long id, AuthenticatedUser authenticatedUser) {

        UUID managerId = memberRepository.findManagerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.MANAGER_NOT_FOUND));

        if (!managerId.equals(authenticatedUser.getUserId()) && authenticatedUser.getAuthorities().stream()
                .noneMatch(authority -> authority.getAuthority().equals(UserRole.ROLE_ADMIN.toString()))) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }
    }
}
