package com.sparta.gathering.domain.gather.repository;

import com.sparta.gathering.domain.gather.entity.GatherDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GatherElasticRepository extends ElasticsearchRepository<GatherDocument, Long> {

    Page<GatherDocument> findByTitle( Pageable pageable, String title);
}

