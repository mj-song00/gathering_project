package com.sparta.gathering.domain.hashtag.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "hashtag")
@SQLDelete(sql = "UPDATE `hashtag` SET deleted_at = NOW() WHERE id = ?")
public class HashTag extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String hashTagName;

    @ColumnDefault("NULL")
    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id")
    private Gather gather;

    private HashTag(String hashTagName, Gather gather) {
        this.hashTagName = hashTagName;
        this.gather = gather;
    }

    public static HashTag from(String hashTagName, Gather gather) {
        return new HashTag(hashTagName, gather);
    }

    public static HashTag of(String hashTagName, Gather gather) {
        return new HashTag(hashTagName, gather);
    }

    public void updateDeleteAt() {
        this.deletedAt = LocalDateTime.now();
    }

}