package com.sparta.gathering.domain.gather.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static com.sparta.gathering.domain.gather.entity.QGather.gather;

@Repository
@RequiredArgsConstructor
public class GatherCustomRepositoryImpl implements GatherCustomRepository {
    private final JPAQueryFactory q;

    @Override
    public GatherResponse findByIdWithBoardAndSchedule(Long gatherId) {
        Gather result = q.selectFrom(gather)
                .leftJoin(gather.hashTagList).fetchJoin() // HashTag 리스트 가져오기
                .where(gather.id.eq(gatherId))
                .fetchOne();
        return new GatherResponse(result);
    }
}
