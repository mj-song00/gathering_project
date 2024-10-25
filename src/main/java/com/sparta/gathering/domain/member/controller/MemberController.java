package com.sparta.gathering.domain.member.controller;

import com.sparta.gathering.common.response.ApiResponse;
import com.sparta.gathering.common.response.ApiResponseEnum;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.service.MemberService;
import com.sparta.gathering.domain.user.entity.User;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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
      @RequestParam(defaultValue = "1") int page

  ) {
    Pageable pageable = PageRequest.of(page - 1, 10);
    List<Member> memberList = memberService.getMembers(pageable, gatherId);
    return ResponseEntity.ok(memberList);
  }

  //멤버 가입승인 - 소모임 manager만
  @PatchMapping("/{memberId}/{gatherId}")
  public ResponseEntity<ApiResponse<Void>> approval(
      @PathVariable long memberId,
      @PathVariable long gatherId,
      @AuthenticationPrincipal User user
  ) {
    memberService.approval(memberId, gatherId, user);
    ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.APPROVAL_SUCCESS);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  //멤버 가입거절-소모임 manager만
  @PatchMapping("/{memberId}/{gatherId}/refusal")
  public ResponseEntity<ApiResponse<Void>> refusal(
      @PathVariable long memberId,
      @PathVariable long gatherId,
      @AuthenticationPrincipal User user
  ) {
    memberService.refusal(memberId, gatherId, user);
    ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.REFUSAL_SUCCESS);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  //멤버 탈퇴
  @PatchMapping("/{memberId}/withdrawal")
  public ResponseEntity<ApiResponse<Void>> withdrawal(
      @PathVariable long memberId,
      @AuthenticationPrincipal User user
  ) {
    memberService.withdrawal(memberId, user);
    ApiResponse<Void> response = ApiResponse.successWithOutData(ApiResponseEnum.WITHDRAWAL_SUCCESS);
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }
}
