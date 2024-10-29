package com.sparta.gathering.domain.hashtag.repository;

import com.sparta.gathering.domain.hashtag.entity.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HashTagRepository extends JpaRepository<HashTag, UUID> {

    Optional<HashTag> findByHashTagName(String hashTagName);

    Optional<HashTag> findByGatherId(Long gatherId);

    Optional<HashTag> findByHashTagNameIn(List<String> hashTagName);
}