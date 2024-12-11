package com.sparta.gathering.domain.like.service;

import com.sparta.gathering.common.exception.BaseException;
import com.sparta.gathering.common.exception.ExceptionEnum;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.gather.repository.GatherRepository;
import com.sparta.gathering.domain.like.entity.Like;
import com.sparta.gathering.domain.like.repository.LikeRepository;
import com.sparta.gathering.domain.member.entity.Member;
import com.sparta.gathering.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LikeServiceImpl implements LikeService {

    private final LikeRepository likeRepository;
    private final GatherRepository gatherRepository;
    private final MemberRepository memberRepository;

    @Transactional
    @Override
    public void addLike(Long memberId, Long gatherId) {
        // 모임 조회
        Gather gather = gatherRepository.findById(gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.GATHER_NOT_FOUND));

        //member가 gather에 소속되어있는지 확인
        Member member = memberRepository.findByIdAndGatherId(memberId, gatherId)
                .orElseThrow(() -> new BaseException(ExceptionEnum.USER_NOT_FOUND));


        Like alreadyLiked = likeRepository.findByMemberIdAndGatherId(memberId, gatherId);

        if (alreadyLiked != null) {
            likeRepository.deleteLike(memberId, gatherId);
            gather.disLike();
        } else {
            Like like = new Like(gather, member);
            gather.like();
            likeRepository.save(like);
        }
    }

    @Override
    public int countLikeByGather(Gather gather) {
        return likeRepository.countByGather(gather);
    }


}
