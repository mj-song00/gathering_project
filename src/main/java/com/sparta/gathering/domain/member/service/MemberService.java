package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface MemberService {
    void createMember(UUID userId, long gatherId);

    Page<Member> getMembers(Pageable pageable, long gatherId);

    void approval(long memberId, long gatherId, User user);

    void refusal(long memberId, long gatherId, User user);

    void withdrawal(long memberId, User user);
}
