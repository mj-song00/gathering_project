package com.sparta.gathering.domain.hashtag.repository;

import com.sparta.gathering.domain.hashtag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HashTagRepository extends JpaRepository<HashTag, Long> {

    Optional<HashTag> findByHashTagNameAndDeletedAtIsNull(String hashTagName);

    Optional<HashTag> findByHashTagName(String hashTagName);
//
//    List<HashTag> findByGatherIdAndHashTagNameIn(Long gatherId, List<String> hashTagName);

}