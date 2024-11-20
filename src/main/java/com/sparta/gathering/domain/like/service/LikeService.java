package com.sparta.gathering.domain.like.service;

import com.sparta.gathering.domain.gather.entity.Gather;

public interface LikeService {

    void addLike(Long memberId, Long gatherId);

    int countLikeByGather(Gather gather);
}
