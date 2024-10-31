package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.common.config.jwt.AuthenticatedUser;
import com.sparta.gathering.domain.member.entity.Member;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberService {

    void createMember(UUID userId, long gatherId);

    Page<Member> getMembers(Pageable pageable, long gatherId);

    void approval(long memberId, long gatherId, AuthenticatedUser authenticatedUser);

    void refusal(long memberId, long gatherId, AuthenticatedUser authenticatedUser);

    void withdrawal(long memberId, AuthenticatedUser authenticatedUser);
}
