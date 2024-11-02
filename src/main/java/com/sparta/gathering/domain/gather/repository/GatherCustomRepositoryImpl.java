package com.sparta.gathering.domain.gather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.domain.gather.dto.response.GatherListResponseItem;
import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.gathering.domain.gather.entity.QGather.gather;

@Repository
@RequiredArgsConstructor
public class GatherCustomRepositoryImpl implements GatherCustomRepository {
    private final JPAQueryFactory q;

    @Override
    public GatherListResponseItem findByIdWithBoardAndSchedule(Long gatherId) {
        Gather result = q.selectFrom(gather)
                .leftJoin(gather.hashTagList).fetchJoin() // HashTag 리스트 가져오기
                .where(gather.id.eq(gatherId))
                .fetchOne();
        return new GatherListResponseItem(result);
    }
}
