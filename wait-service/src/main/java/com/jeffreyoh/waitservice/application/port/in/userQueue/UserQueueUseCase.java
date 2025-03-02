package com.jeffreyoh.waitservice.application.port.in.userQueue;

import reactor.core.publisher.Mono;

public interface UserQueueUseCase {

    Mono<Long> registerWaitQueue(String queue, Long userId);

}
