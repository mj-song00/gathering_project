package com.sparta.gathering.domain.gather.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.domain.gather.dto.response.GatherResponse;
import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.sparta.gathering.domain.gather.entity.QGather.gather;
import static com.sparta.gathering.domain.hashtag.entity.QHashTag.hashTag;

@Repository
@RequiredArgsConstructor
public class GatherCustomRepositoryImpl implements GatherCustomRepository {
    private final JPAQueryFactory q;

    @Override
    public GatherResponse findByIdWithBoardAndSchedule(Long gatherId) {
        Gather result = q.selectFrom(gather)
                .leftJoin(gather.scheduleList).fetchJoin() // schedule 리스트 가져오기
                .where(gather.id.eq(gatherId))
                .fetchOne();
        return new GatherResponse(result);
    }

    @Override
    public Page<Gather> findByKeywords(Pageable pageable, List<String> hashTagName){
        List<Gather> result = q.selectFrom(gather)
                .leftJoin(gather.hashTagList, hashTag).fetchJoin()
                .where(hashtagCondition(hashTagName)) // 동일한 메서드로 적용
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리
        Long count = q.select(gather.count())
                .from(gather)
                .where(gather.hashTagList.any().hashTagName.in(hashTagName))  // 동일한 메서드로 적용
                .fetchOne();


        if (count== null) count = 0L;

        return new PageImpl<>(result, pageable, count);
    }
    private BooleanExpression hashtagCondition(List<String> hashTagNames) {
        return hashTagNames == null ? null : hashTag.hashTagName.in(hashTagNames);
    }
}
