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
import com.sparta.gathering.domain.user.enums.UserRole;
import com.sparta.gathering.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GatherServiceImpl implements GatherService{
    private final UserRepository userRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;

    // 모임생성
    public void createGather(GatherRequest request, User user ){
        Gather gather = new Gather(request.getTitle());
        gatherRepository.save(gather);
        Member member = new Member(user, gather,Permission.MANAGER);
        memberRepository.save(member);
    }

    //모임 수정 gather
    public void modifyGather(GatherRequest request, long id, User user){

        UUID ownerId = memberRepository.findOwnerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!ownerId.equals(user.getId())) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

        Gather gather = gatherRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        gather.updateGatherTitle(request.getTitle());
        gatherRepository.save(gather);
    }

    //모임 삭제
    public void deleteGather(long id, User user){
        UUID ownerId = memberRepository.findOwnerIdByGatherId(id)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));

        if (!ownerId.equals(user.getId()) || user.getUserRole() != UserRole.ROLE_ADMIN ) {
            throw new BaseException(ExceptionEnum.UNAUTHORIZED_ACTION);
        }

        Gather gather = gatherRepository.findById(id).orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));
        gather.delete();
        gatherRepository.save(gather);
    }

    //모임 불러오기
    public List<Gather> Gathers(Pageable pageable) {
       return gatherRepository.findByDeletedAtIsNullOrderByCreatedAtDesc(pageable);
    }
}
