package com.sparta.gathering.domain.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.domain.like.entity.QLike;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.gathering.domain.like.entity.QLike.like;


@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository {
    private final JPAQueryFactory q;

    @Override
    public boolean existByMemberIdAndGatherId(Long memberId, Long gatherId) {
        return q.selectFrom(like)
                .where(like.member.id.eq(memberId)
                        .and(like.gather.id.eq(gatherId)))
                .fetchOne() != null;
    }

    @Override
    public void deleteLike(Long memberId, Long gatherId) {
        QLike like = QLike.like;
        q.delete(like)
                .where(like.member.id.eq(memberId)
                        .and(like.gather.id.eq(gatherId))
                )
                .execute();

    }
}
