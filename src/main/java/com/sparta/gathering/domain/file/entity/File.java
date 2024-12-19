package com.sparta.gathering.domain.file.entity;

import com.sparta.gathering.common.entity.Timestamped;
import com.sparta.gathering.domain.gather.entity.Gather;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@NoArgsConstructor
public class File extends Timestamped {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originName; //원본 이름

    private String name;

    private String uri; // 이미지 저장 주소

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gather_id", nullable = false) // gather_id 외래 키
    private Gather gather;

    public File(String originName, String name, String uri, Gather gather) {
        this.originName = originName;
        this.name = name;
        this.uri = uri;
        this.gather = gather;
    }
}
