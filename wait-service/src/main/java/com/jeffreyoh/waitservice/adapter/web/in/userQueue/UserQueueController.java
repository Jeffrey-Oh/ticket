package com.jeffreyoh.waitservice.adapter.web.in.userQueue;

import com.jeffreyoh.waitservice.adapter.web.dto.AllowUserResponse;
import com.jeffreyoh.waitservice.adapter.web.dto.AllowedUserResponse;
import com.jeffreyoh.waitservice.adapter.web.dto.RankNumberResponse;
import com.jeffreyoh.waitservice.adapter.web.dto.RegisterUserResponse;
import com.jeffreyoh.waitservice.application.port.in.userQueue.UserQueueUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/allowed")
    public Mono<AllowedUserResponse> isAllowedUser(
        @RequestParam(name = "queue", defaultValue = "default") String queue,
        @RequestParam(name = "userId") Long userId
    ) {
        return userQueueUseCase.isAllowed(queue, userId)
            .map(AllowedUserResponse::new);
    }

    @PostMapping("/allow")
    public Mono<AllowUserResponse> allowUser(
        @RequestParam(name = "queue", defaultValue = "default") String queue,
        @RequestParam(name = "count") Long count
    ) {
        return userQueueUseCase.allowUser(queue, count)
            .map(allowed -> new AllowUserResponse(count, allowed ));
    }

    @GetMapping("/rank")
    public Mono<RankNumberResponse> getRankUser(
        @RequestParam(name = "queue", defaultValue = "default") String queue,
        @RequestParam(name = "userId") Long userId
    ) {
        return userQueueUseCase.getRank(queue, userId)
            .map(RankNumberResponse::new);
    }

}
