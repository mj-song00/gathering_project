package com.sparta.gathering.domain.gather.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.common.contributor.CustomFunction;
import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.gathering.domain.gather.entity.QGather.gather;
import static com.sparta.gathering.domain.hashtag.entity.QHashTag.hashTag;

@Repository
@RequiredArgsConstructor
public class GatherCustomRepositoryImpl implements GatherCustomRepository {
    private final JPAQueryFactory q;

    @Override
    public Optional<Gather> findByIdWithBoardAndSchedule(Long gatherId) {
        Gather result = q.selectFrom(gather)
                .leftJoin(gather.scheduleList).fetchJoin() // schedule 리스트 가져오기
                .where(gather.id.eq(gatherId).and(gather.deletedAt.isNull()))
                .fetchOne();
        return Optional.ofNullable(result);
    }

    @Override
    public Page<Gather> findByKeywords(Pageable pageable, List<String> hashTagName) {
        List<Gather> result = q.selectFrom(gather)
                .leftJoin(gather.hashTagList, hashTag).fetchJoin()
                .where(hashtagCondition(hashTagName).and(gather.deletedAt.isNull())) // 동일한 메서드로 적용
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // count 쿼리
        Long count = q.select(gather.count())
                .from(gather)
                .where(gather.hashTagList.any().hashTagName.in(hashTagName))  // hashtag list 전체 에서 해시태그name안에 HashTagName이 있는지 확인
                .fetchOne();


        if (count == null) count = 0L;

        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Page<Gather> findByTitle(Pageable pageable, String title) {
        List<Gather> result = q.selectFrom(gather)
                .leftJoin(gather.hashTagList, hashTag).fetchJoin()
                .where(
                        CustomFunction.match(gather.title, title)
                        , (gather.deletedAt.isNull())

                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = q.select(gather.count())
                .from(gather)
                .where(
                        gather.title.contains(title)
                                .and(gather.deletedAt.isNull())
                )
                .fetchOne();
        if (count == null) count = 0L;
        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Page<Gather> findByCategoryWithHashTags(Pageable pageable, Long categoryId) {
        List<Gather> result = q.selectFrom(gather)
                .leftJoin(gather.hashTagList, hashTag).fetchJoin()
                .leftJoin(gather.category).fetchJoin()
                .where(
                        gather.category.id.eq(categoryId)
                                .and(gather.deletedAt.isNull())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long count = q.select(gather.count())
                .from(gather)
                .where(
                        gather.category.id.eq(categoryId)
                                .and(gather.deletedAt.isNull())
                )
                .fetchOne();

        if (count == null) count = 0L;

        return new PageImpl<>(result, pageable, count);
    }

    private BooleanExpression hashtagCondition(List<String> hashTagNames) {
        return hashTagNames == null ? null : hashTag.hashTagName.in(hashTagNames);
    }
}
