package com.sparta.gathering.domain.gather.repository;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.gathering.domain.gather.entity.Gather;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.sparta.gathering.domain.gather.entity.QGather.gather;
import static com.sparta.gathering.domain.gatherhashtag.entity.QGatherHashtag.gatherHashtag;
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
                .leftJoin(gather.gatherHashtags, gatherHashtag).fetchJoin() // GatherHashtag 조인
                .leftJoin(gatherHashtag.hashTag, hashTag).fetchJoin()       // HashTag 조인
                .where(
                        hashtagCondition(hashTagName) // HashTag 조건
                                .and(gather.deletedAt.isNull()) // Soft delete 필터링
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        // count 쿼리
        Long count = q.select(gather.count())
                .from(gather)
                .leftJoin(gather.gatherHashtags, gatherHashtag) // Count 쿼리에서도 Join 필요
                .leftJoin(gatherHashtag.hashTag, hashTag)
                .where(
                        hashtagCondition(hashTagName)
                                .and(gather.deletedAt.isNull())
                )
                .fetchOne();


        if (count == null) count = 0L;

        return new PageImpl<>(result, pageable, count);
    }

    @Override
    public Page<Gather> findByTitle(Pageable pageable, String title) {
        List<Gather> result = q.selectFrom(gather)
                .leftJoin(gather.gatherHashtags, gatherHashtag).fetchJoin() // GatherHashtag 조인
                .leftJoin(gatherHashtag.hashTag, hashTag).fetchJoin()       // HashTag 조인
                .leftJoin(gather.map).fetchJoin()
                .where(
                        gather.title.contains(title)
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
                .leftJoin(gather.gatherHashtags, gatherHashtag).fetchJoin()
                .leftJoin(gatherHashtag.hashTag).fetchJoin()
                .leftJoin(gather.category).fetchJoin()
                .leftJoin(gather.map).fetchJoin()
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
        return (hashTagNames != null && !hashTagNames.isEmpty())
                ? hashTag.hashTagName.in(hashTagNames)
                : null;
    }
}
