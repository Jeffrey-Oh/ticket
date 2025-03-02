package com.jeffreyoh.waitservice.application.useCase;

import com.jeffreyoh.waitservice.application.port.in.userQueue.UserQueueUseCase;
import com.jeffreyoh.waitservice.infrastructure.exception.ApplicationException;
import com.jeffreyoh.waitservice.infrastructure.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class UserQueueService implements UserQueueUseCase {

    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    private final String USER_QUEUE_WAIT_KEY = "users:queue:%s:wait";

    // 대기열 등록 API
    @Override
    public Mono<Long> registerWaitQueue(final String queue, final Long userId) {
        // redis sortedset
        // - key: userId
        // - value: unix timestamp
        // rank
        long unixTimestamp = Instant.now().getEpochSecond();
        return reactiveRedisTemplate.opsForZSet().add(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString(), unixTimestamp)
            .filter(i -> i) // 재등록 시 결과 값이 false 이므로 true 일 때만 처리한다
            .switchIfEmpty(Mono.error(ErrorCode.QUEUE_ALREADY_REGISTERED_USER.build()))
            .flatMap(i -> reactiveRedisTemplate.opsForZSet().rank(USER_QUEUE_WAIT_KEY.formatted(queue), userId.toString()))
            .map(i -> i >= 0 ? i + 1 : i); // 0부터 시작이라서 번호 부여를 위해 + 1 처리
    }

}
