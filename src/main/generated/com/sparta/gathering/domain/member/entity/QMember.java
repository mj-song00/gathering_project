package com.sparta.gathering.domain.member.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -1368539602L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMember member = new QMember("member1");

    public final com.sparta.gathering.common.entity.QTimestamped _super = new com.sparta.gathering.common.entity.QTimestamped(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final DateTimePath<java.time.LocalDateTime> deletedAt = createDateTime("deletedAt", java.time.LocalDateTime.class);

    public final com.sparta.gathering.domain.gather.entity.QGather gather;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final EnumPath<com.sparta.gathering.domain.member.enums.Permission> permission = createEnum("permission", com.sparta.gathering.domain.member.enums.Permission.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedAt = _super.updatedAt;

    public final com.sparta.gathering.domain.user.entity.QUser user;

    public QMember(String variable) {
        this(Member.class, forVariable(variable), INITS);
    }

    public QMember(Path<? extends Member> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMember(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMember(PathMetadata metadata, PathInits inits) {
        this(Member.class, metadata, inits);
    }

    public QMember(Class<? extends Member> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.gather = inits.isInitialized("gather") ? new com.sparta.gathering.domain.gather.entity.QGather(forProperty("gather"), inits.get("gather")) : null;
        this.user = inits.isInitialized("user") ? new com.sparta.gathering.domain.user.entity.QUser(forProperty("user")) : null;
    }

}

