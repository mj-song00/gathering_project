package com.sparta.gathering.domain.hashtag.repository;

import com.sparta.gathering.domain.hashtag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    List<HashTag> findByGatherIdAndDeletedAtIsNull(Long gatherId);

    List<HashTag> findByGatherIdAndHashTagNameIn(Long gatherId, List<String> hashTagName);

}