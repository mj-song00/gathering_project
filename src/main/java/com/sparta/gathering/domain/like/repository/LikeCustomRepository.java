package com.sparta.gathering.domain.like.repository;

public interface LikeCustomRepository {

    boolean existByMemberIdAndGatherId(Long memberId, Long gatherId);

    void deleteLike(Long memberId, Long gatherId);
}
