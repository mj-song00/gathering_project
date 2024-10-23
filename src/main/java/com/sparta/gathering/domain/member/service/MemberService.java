package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.member.entity.Member;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MemberService {
    void createMember(UUID userId, long gatherId);

    List<Member> getMembers(Pageable pageable, long gatherId);
}
