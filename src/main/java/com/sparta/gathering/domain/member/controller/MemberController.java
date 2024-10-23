package com.sparta.gathering.domain.member.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @PostMapping()
    public ResponseEntity<ApiResponse<Void>> createGather() {
        memberService.createMember();
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
