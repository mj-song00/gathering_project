package com.sparta.gathering.domain.member.controller;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.member.dto.response.MemberListResponse;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "멤버 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    //role - member 유저 생성 - 수락대기
    @Operation(summary = "소모임  가입 신청", description = "해당 소모임 가입을 신청합니다." +
            "신청자는 manager가 승인/거절 전까지 pendding상태를 유지합니다.")
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
    @Operation(summary = "소모임  멤버 조회", description = "해당 소모임의 멤버를 조회합니다.")
    @GetMapping("/{gatherId}")
    public ApiResponse<MemberListResponse> getMembers(
            @PathVariable long gatherId,
            @RequestParam(defaultValue = "1") int page
    ) {
        Pageable pageable = PageRequest.of(page - 1, 10);
        Page<Member> memberList = memberService.getMembers(pageable, gatherId);
        MemberListResponse response = new MemberListResponse(
                memberList.getContent(),
                memberList.getNumber(),
                memberList.getTotalPages(),
                memberList.getTotalElements()
        );
        return ApiResponse.successWithData(response, ApiResponseEnum.GET_SUCCESS);
    }

    //멤버 가입승인 - 소모임 manager만
    @Operation(summary = "소모임 가입 승인", description = "해당 소모임 가입을 승인합니다." +
            "승인은 해당 모임의 manager만 가능합니다. 승인시 pendding 상태에서 guest상태로 변경됩니다.")
    @PatchMapping("/{memberId}/gather/{gatherId}")
    public ResponseEntity<ApiResponse<Void>> approval(
            @PathVariable long memberId,
            @PathVariable long gatherId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        memberService.approval(memberId, gatherId, authenticatedUser);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.APPROVAL_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //멤버 가입거절-소모임 manager만
    @Operation(summary = "소모임 가입 거절", description = "해당 소모임 가입을 거절합니다." +
            "거절은 해당 모임의 manager만 가능합니다. 거절 시 pendding상태에서 refusal상태로 변경됩니다.")
    @PatchMapping("/{memberId}/gather/{gatherId}/refusal")
    public ResponseEntity<ApiResponse<Void>> refusal(
            @PathVariable long memberId,
            @PathVariable long gatherId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        memberService.refusal(memberId, gatherId, authenticatedUser);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.REFUSAL_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    //멤버 탈퇴
    @Operation(summary = "소모임 탈퇴", description = "해당 소모임 탈퇴합니다.")
    @PatchMapping("/{memberId}/withdrawal")
    public ResponseEntity<ApiResponse<Void>> withdrawal(
            @PathVariable long memberId,
            @AuthenticationPrincipal AuthenticatedUser authenticatedUser
    ) {
        memberService.withdrawal(memberId, authenticatedUser);
        ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.WITHDRAWAL_SUCCESS);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
