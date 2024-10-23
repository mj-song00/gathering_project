package com.sparta.gathering.domain.member.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    //role - member 유저 생성 - 수락대기
    @PostMapping("/user/{userId}/gather/{gatherId}")
    public ResponseEntity<ApiResponse<Void>> createGather(
            @PathVariable UUID userId,
            @PathVariable long gatherId
    ) {
        memberService.createMember(userId, gatherId);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.CREATE_SUCCESS);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //멤버조회
    @GetMapping("/{gatherId}")
    public ResponseEntity<List<Member>> getMembers(
            @PathVariable long gatherId,
            @RequestParam(defaultValue ="1") int page

    ){
        Pageable pageable = PageRequest.of(page-1, 10);
        List<Member> memberList = memberService.getMembers(pageable, gatherId);
        return ResponseEntity.ok(memberList);
    }

    //멤버 가입승인

    //멤버 가입거절

    //멤버 탈퇴
}
