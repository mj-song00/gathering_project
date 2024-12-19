package com.sparta.gathering.domain.file.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.file.dto.response.FileResponse;
import com.sparta.gathering.domain.file.service.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "File Upload", description = "파일 업로드 / 송민지")
@RestController
@RequestMapping("/api/file")
@RequiredArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "image upload", description = "multipart 형식으로 image를 받습니다." + "기본 크기는 1M 입니다")
    @PostMapping("/image/{gatherId}")
    public ResponseEntity<ApiResponse<String>> imageUpload(
            @RequestPart(value = "image", required = false) MultipartFile file,
            @PathVariable Long gatherId
    ) {
        fileService.saveImage(file,gatherId);
        ApiResponse<String> response = ApiResponse.successWithOutData(
                ApiResponseEnum.IMAGE_UPLOAD_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @Operation(summary = "image를 가져옵니다.", description = "image를 가져 옵니다.")
    @GetMapping("/image/{gatherId}")
    public ResponseEntity<ApiResponse<List<FileResponse>>> getImage(
            @PathVariable Long gatherId
    ){
        List<FileResponse> response = fileService.getImage(gatherId);
        return ResponseEntity.ok(ApiResponse.successWithData(response,ApiResponseEnum.IMAGE_GET_SUCCESS));
    }
}
