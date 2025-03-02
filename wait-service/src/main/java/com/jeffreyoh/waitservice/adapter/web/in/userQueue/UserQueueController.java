package com.jeffreyoh.waitservice.adapter.web.in.userQueue;

import com.jeffreyoh.waitservice.adapter.web.dto.RegisterUserResponse;
import com.jeffreyoh.waitservice.application.port.in.userQueue.UserQueueUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/queue")
public class UserQueueController {

    private final UserQueueUseCase userQueueUseCase;

    @PostMapping
    public Mono<RegisterUserResponse> registerUser(
        @RequestParam(name = "queue", defaultValue = "default") String queue,
        @RequestParam(name = "userId") Long userId
    ) {
        return userQueueUseCase.registerWaitQueue(queue, userId)
            .map(RegisterUserResponse::new);
    }

}
