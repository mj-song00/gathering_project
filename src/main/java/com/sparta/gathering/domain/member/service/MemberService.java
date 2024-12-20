package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.member.dto.response.MemberInfoResponse;
import com.sparta.gathering.domain.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MemberService {

    void createMember(UUID userId, long gatherId,AuthenticatedUser authenticatedUser);

    Page<Member> getMembers(Pageable pageable, long gatherId);

    void approval(long memberId, long gatherId, AuthenticatedUser authenticatedUser);

    void refusal(long memberId, long gatherId, AuthenticatedUser authenticatedUser);

    void withdrawal(long memberId, AuthenticatedUser authenticatedUser);

    List<MemberInfoResponse> getMyId(AuthenticatedUser authenticatedUser);
}
