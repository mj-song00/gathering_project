package com.sparta.gathering.common.config.batch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Set;

@Slf4j
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class RedisRankingJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final RedisTemplate<String, Object> redisTemplate;

    @Bean
    public Job redisRankingJob() {
        return new JobBuilder("redisRankingJob", jobRepository)
                .start(redisRankingReader())
                .build();
    }

    @Bean
    public Step redisRankingReader() {
        return new StepBuilder("redisRankingReader", jobRepository)
                .tasklet((contribution, cunkContext)->{
                    try {
                        Set<ZSetOperations.TypedTuple<Object>> top5 = redisTemplate.opsForZSet().reverseRangeWithScores("city", 0, 4);

                        if (top5 != null && !top5.isEmpty()) {
                            // 기존 top5Ranking에서 데이터가 있다면 삭제
                            redisTemplate.delete("top5Ranking");

                            // 새 데이터를 top5Ranking에 추가
                            top5.forEach(tuple -> redisTemplate.opsForZSet()
                                    .add("top5Ranking", tuple.getValue().toString(), tuple.getScore()));
                        } else {
                            // top5가 null이거나 비어있으면 그냥 새로운 데이터만 저장
                            top5.forEach(tuple -> redisTemplate.opsForZSet()
                                    .add("top5Ranking", tuple.getValue().toString(), tuple.getScore()));
                        }

                        // 3. 기존 랭킹 데이터 삭제
                        redisTemplate.opsForZSet().removeRange("city", 0, -1);

                        return RepeatStatus.FINISHED;
                    } catch (Exception e) {
                        log.error("Redis error occurred while executing redisRankingReader: {}", e.getMessage(), e);
                        throw e;  // 예외를 던져서 배치 실패를 처리하도록 함
                    }
                }, transactionManager)
                .build();
    }
}
