package com.sparta.gathering.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

//@Configuration
public class ElasticSearchConfig extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.host}")
    private String host;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo("localhost:9200") // HTTPS 주소
                .withBasicAuth("elastic", "team21") // 인증 정보 추가
                .build();
    }
}