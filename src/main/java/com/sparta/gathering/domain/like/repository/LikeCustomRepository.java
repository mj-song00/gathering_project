package com.sparta.gathering.domain.like.repository;

import com.sparta.gathering.domain.like.entity.Like;

public interface LikeCustomRepository {

    Like findByMemberIdAndGatherId(Long memberId, Long gatherId);

    void deleteLike(Long memberId, Long gatherId);
}
