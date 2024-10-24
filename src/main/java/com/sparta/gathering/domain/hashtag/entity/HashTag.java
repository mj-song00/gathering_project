package com.sparta.gathering.domain.hashtag.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import com.sparta.gathering.domain.hashtag.dto.request.HashTagReq;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "hashtag")
@Where(clause = "deleted_at IS NULL")
@SQLDelete(sql = "UPDATE `hashtag` SET deleted_at = NOW() WHERE id = ?")
public class HashTag extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    private String hashTagName;

    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id")
    private Gather gather;

    private HashTag(String hashTagName,Gather gather) {
        this.hashTagName = hashTagName;
        this.gather = gather;
    }

    public static HashTag from(HashTagReq hashTagReq,Gather gather) {
        return new HashTag(hashTagReq.getHashTagName(), gather );
    }

    public void updateDeleteAt() {
        this.deletedAt = LocalDateTime.now();
    }

}