package com.sparta.gathering.domain.member.service;

import com.sparta.gathering.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MeberServiceImpl implements MemberService {
    private final MemberRepository memberRepository;


    public void createMember(){

    }
}
