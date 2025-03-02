package com.jeffreyoh.waitservice.application.port.in.userQueue;

import reactor.core.publisher.Mono;

import java.security.NoSuchAlgorithmException;

public interface UserQueueUseCase {

    // 대기열 등록 API
    Mono<Long> registerWaitQueue(String queue, Long userId);

    // count 만큼 진입을 허용
    Mono<Long> allowUser(String queue, Long count);

    // 진입이 가능한 상태인지 조회
    Mono<Boolean> isAllowed(String queue, Long userId);

    // 진입이 가능한 상태인지 token 으로 검증
    Mono<Boolean> isAllowedByToken(String queue, Long userId, String token);

    // 대기열 번호 조회
    Mono<Long> getRank(String queue, Long userId);

    // token 생성
    Mono<String> generateToken(String queue, Long userId);

}
