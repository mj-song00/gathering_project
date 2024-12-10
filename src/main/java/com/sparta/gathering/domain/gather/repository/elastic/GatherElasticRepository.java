package com.sparta.gathering.domain.gather.repository.elastic;

import com.sparta.gathering.domain.gather.document.GatherDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface GatherElasticRepository extends ElasticsearchRepository<GatherDocument, String> {
    Page<GatherDocument> findByTitleContaining(Pageable pageable, String title);
}
