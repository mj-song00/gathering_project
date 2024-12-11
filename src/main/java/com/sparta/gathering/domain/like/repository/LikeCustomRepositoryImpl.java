package com.sparta.gathering.domain.like.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.domain.like.entity.Like;
import com.sparta.gathering.domain.like.entity.QLike;
import jakarta.persistence.LockModeType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.gathering.domain.like.entity.QLike.like;


@Repository
@RequiredArgsConstructor
public class LikeCustomRepositoryImpl implements LikeCustomRepository {
    private final JPAQueryFactory q;

    @Override
    public Like findByMemberIdAndGatherId(Long memberId, Long gatherId) {
        return q.selectFrom(like)
                .where(like.member.id.eq(memberId)
                        .and(like.gather.id.eq(gatherId)))
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();
    }

    @Override
    public void deleteLike(Long memberId, Long gatherId) {
        QLike like = QLike.like;

        // 비관적 락 적용
        q.selectFrom(like)
                .where(
                        like.member.id.eq(memberId)
                                .and(like.gather.id.eq(gatherId))
                )
                .setLockMode(LockModeType.PESSIMISTIC_WRITE)
                .fetchOne();

        // 데이터 삭제
        q.delete(like)
                .where(
                        like.member.id.eq(memberId)
                                .and(like.gather.id.eq(gatherId))
                )
                .execute();
    }
}
