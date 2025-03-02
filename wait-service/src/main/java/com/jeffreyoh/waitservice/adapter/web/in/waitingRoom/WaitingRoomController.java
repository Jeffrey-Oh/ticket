package com.jeffreyoh.waitservice.adapter.web.in.waitingRoom;

import com.jeffreyoh.waitservice.application.port.in.userQueue.UserQueueUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequiredArgsConstructor
public class WaitingRoomController {

    private final UserQueueUseCase userQueueUseCase;

    @GetMapping("/waiting-room")
    public Mono<Rendering> waitingRoomPage(
        @RequestParam(name = "queue", defaultValue = "default") String queue,
        @RequestParam(name = "userId") Long userId,
        @RequestParam(name = "redirectUrl") String redirectUrl
    ) {
        return userQueueUseCase.isAllowed(queue, userId)
            .filter(allowed -> allowed) // 허용 가능한지 체크
            .flatMap(allowed -> Mono.just(Rendering.redirectTo(redirectUrl).build())) // 허용 가능하면 이동
            .switchIfEmpty( // 아직 허용되지 않을 경우 대기열 등록
                userQueueUseCase.registerWaitQueue(queue, userId)
                    .onErrorResume(e -> userQueueUseCase.getRank(queue, userId))
                    .map(rank -> Rendering.view("waiting-room.html")
                        .modelAttribute("number", rank)
                        .modelAttribute("userId", userId)
                        .modelAttribute("queue", queue)
                        .build()
                    )
            );
    }

}
