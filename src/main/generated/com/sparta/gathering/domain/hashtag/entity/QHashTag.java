package com.sparta.gathering.domain.hashtag.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QHashTag is a Querydsl query type for HashTag
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QHashTag extends EntityPathBase<HashTag> {

    private static final long serialVersionUID = -1224372480L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QHashTag hashTag = new QHashTag("hashTag");

    public final com.sparta.gathering.common.entity.QTimestamped _super = new com.sparta.gathering.common.entity.QTimestamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final com.sparta.gathering.domain.gather.entity.QGather gather;

    public final StringPath hashTagName = createString("hashTagName");

    public final ComparablePath<java.util.UUID> id = createComparable("id", java.util.UUID.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QHashTag(String variable) {
        this(HashTag.class, forVariable(variable), INITS);
    }

    public QHashTag(Path<? extends HashTag> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QHashTag(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QHashTag(PathMetadata metadata, PathInits inits) {
        this(HashTag.class, metadata, inits);
    }

    public QHashTag(Class<? extends HashTag> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.gather = inits.isInitialized("gather") ? new com.sparta.gathering.domain.gather.entity.QGather(forProperty("gather")) : null;
    }

}

