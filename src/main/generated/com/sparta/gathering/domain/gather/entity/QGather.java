package com.sparta.gathering.domain.gather.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QGather is a Querydsl query type for Gather
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QGather extends EntityPathBase<Gather> {

    private static final long serialVersionUID = 1132422960L;

    public static final QGather gather = new QGather("gather");

    public final com.sparta.gathering.common.entity.QTimestamped _super = new com.sparta.gathering.common.entity.QTimestamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public QGather(String variable) {
        super(Gather.class, forVariable(variable));
    }

    public QGather(Path<? extends Gather> path) {
        super(path.getType(), path.getMetadata());
    }

    public QGather(PathMetadata metadata) {
        super(Gather.class, metadata);
    }

}

