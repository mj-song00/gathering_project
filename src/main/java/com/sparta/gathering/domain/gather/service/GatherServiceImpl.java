package com.sparta.gathering.domain.gather.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.dto.request.GatherRequest;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.enums.Permission;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import com.sparta.gathering.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GatherServiceImpl implements GatherService{
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;
    //todo : 본인확인 필요!!

    // 모임생성
    public void createGather(GatherRequest request, User user ){
        Gather gather = new Gather(request.getTitle());
        gatherRepository.save(gather);
        Member member = new Member(user, gather,Permission.MANAGER);
        memberRepository.save(member);
    }

    //모임 수정gather
    public void modifyGather(GatherRequest request, long id){
        Gather gather = gatherRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.updateGatherTitle(request.getTitle());
        gatherRepository.save(gather);
    }

    //모임 삭제
    public void deleteGather(long id){
        Gather gather = gatherRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.delete();
        gatherRepository.save(gather);
    }

    //모임 불러오기
    public List<Gather> Gathers(Pageable pageable) {
       return gatherRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
    }
}
