package com.jeffreyoh.waitservice.application.port.in.userQueue;

import reactor.core.publisher.Mono;

public interface UserQueueUseCase {

    // 대기열 등록 API
    Mono<Long> registerWaitQueue(String queue, Long userId);

    // count 만큼 진입을 허용
    Mono<Long> allowUser(String queue, Long count);

    // 진입이 가능한 상태인지 조회
    Mono<Boolean> isAllowed(String queue, Long userId);

}
