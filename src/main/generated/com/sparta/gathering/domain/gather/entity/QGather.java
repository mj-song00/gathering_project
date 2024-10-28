package com.sparta.gathering.domain.gather.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QGather is a Querydsl query type for Gather
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGather extends EntityPathBase<Gather> {

    private static final long serialVersionUID = 1132422960L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QGather gather = new QGather("gather");

    public final com.sparta.gathering.common.entity.QTimestamped _super = new com.sparta.gathering.common.entity.QTimestamped(this);

    public final ListPath<com.sparta.gathering.domain.board.entity.Board, com.sparta.gathering.domain.board.entity.QBoard> boardList = this.<com.sparta.gathering.domain.board.entity.Board, com.sparta.gathering.domain.board.entity.QBoard>createList("boardList", com.sparta.gathering.domain.board.entity.Board.class, com.sparta.gathering.domain.board.entity.QBoard.class, PathInits.DIRECT2);

    public final com.sparta.gathering.domain.category.entity.QCategory category;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final ListPath<com.sparta.gathering.domain.schedule.entity.Schedule, com.sparta.gathering.domain.schedule.entity.QSchedule> scheduleList = this.<com.sparta.gathering.domain.schedule.entity.Schedule, com.sparta.gathering.domain.schedule.entity.QSchedule>createList("scheduleList", com.sparta.gathering.domain.schedule.entity.Schedule.class, com.sparta.gathering.domain.schedule.entity.QSchedule.class, PathInits.DIRECT2);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QGather(String variable) {
        this(Gather.class, forVariable(variable), INITS);
    }

    public QGather(Path<? extends Gather> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QGather(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QGather(PathMetadata metadata, PathInits inits) {
        this(Gather.class, metadata, inits);
    }

    public QGather(Class<? extends Gather> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.category = inits.isInitialized("category") ? new com.sparta.gathering.domain.category.entity.QCategory(forProperty("category"), inits.get("category")) : null;
    }

}

