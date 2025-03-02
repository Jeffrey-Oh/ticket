package com.jeffreyoh.waitservice.application.useCase;

import com.jeffreyoh.waitservice.EmbeddedRedis;
import com.jeffreyoh.waitservice.application.port.in.userQueue.UserQueueUseCase;
import com.jeffreyoh.waitservice.infrastructure.exception.ApplicationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.test.context.ActiveProfiles;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(EmbeddedRedis.class)
@ActiveProfiles("test")
class UserQueueServiceTest {

    @Autowired
    private ReactiveRedisTemplate<String, String> reactiveRedisTemplate;

    @Autowired
    private UserQueueUseCase userQueueUseCase;

    @BeforeEach
    public void beforeEach() {
        ReactiveRedisConnection reactiveRedisConnection = reactiveRedisTemplate.getConnectionFactory().getReactiveConnection();
        reactiveRedisConnection.serverCommands().flushAll().subscribe();
    }

    @Test
    void registerWaitQueue() {
        StepVerifier.create(userQueueUseCase.registerWaitQueue("default", 100L))
            .expectNext(1L)
            .verifyComplete();

        StepVerifier.create(userQueueUseCase.registerWaitQueue("default", 101L))
            .expectNext(2L)
            .verifyComplete();

        StepVerifier.create(userQueueUseCase.registerWaitQueue("default", 102L))
            .expectNext(3L)
            .verifyComplete();
    }

    @Test
    void alreadyRegisterWaitQueue() {
        StepVerifier.create(userQueueUseCase.registerWaitQueue("default", 100L))
            .expectNext(1L)
            .verifyComplete();

        StepVerifier.create(userQueueUseCase.registerWaitQueue("default", 100L))
            .expectError(ApplicationException.class)
            .verify();
    }

    @Test
    void emptyAllowUser() {
        StepVerifier.create(userQueueUseCase.allowUser("default", 3L))
            .expectNext(0L)
            .verifyComplete();
    }

    @Test
    void allowUser() {
        StepVerifier.create(
            userQueueUseCase.registerWaitQueue("default", 100L)
                .then(userQueueUseCase.registerWaitQueue("default", 101L))
                .then(userQueueUseCase.registerWaitQueue("default", 102L))
                .then(userQueueUseCase.allowUser("default", 2L))
            )
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    void allowUser2() {
        StepVerifier.create(
                userQueueUseCase.registerWaitQueue("default", 100L)
                    .then(userQueueUseCase.registerWaitQueue("default", 101L))
                    .then(userQueueUseCase.registerWaitQueue("default", 102L))
                    .then(userQueueUseCase.allowUser("default", 5L))
            )
            .expectNext(3L)
            .verifyComplete();
    }

    @Test
    void allowUserAfterRegisterWaitQueue() {
        StepVerifier.create(
                userQueueUseCase.registerWaitQueue("default", 100L)
                    .then(userQueueUseCase.registerWaitQueue("default", 101L))
                    .then(userQueueUseCase.registerWaitQueue("default", 102L))
                    .then(userQueueUseCase.allowUser("default", 3L))
                    .then(userQueueUseCase.registerWaitQueue("default", 200L))
            )
            .expectNext(1L)
            .verifyComplete();
    }

    @Test
    void isNotAllowed() {
        StepVerifier.create(userQueueUseCase.isAllowed("default", 100L))
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void isNotAllowed2() {
        StepVerifier.create(
                userQueueUseCase.registerWaitQueue("default", 100L)
                    .then(userQueueUseCase.allowUser("default", 1L))
                    .then(userQueueUseCase.isAllowed("default", 101L))
            )
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void isAllowed() {
        StepVerifier.create(
                userQueueUseCase.registerWaitQueue("default", 100L)
                    .then(userQueueUseCase.allowUser("default", 1L))
                    .then(userQueueUseCase.isAllowed("default", 100L))
            )
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void getRank() {
        StepVerifier.create(
            userQueueUseCase.registerWaitQueue("default", 100L)
                .then(userQueueUseCase.getRank("default", 100L))
            )
            .expectNext(1L)
            .verifyComplete();

        StepVerifier.create(
                userQueueUseCase.registerWaitQueue("default", 101L)
                    .then(userQueueUseCase.getRank("default", 101L))
            )
            .expectNext(2L)
            .verifyComplete();
    }

    @Test
    void emptyRank() {
        StepVerifier.create(userQueueUseCase.getRank("default", 100L))
            .expectNext(-1L)
            .verifyComplete();
    }

    @Test
    void isNotAllowedByToken() {
        StepVerifier.create(userQueueUseCase.isAllowedByToken("default", 100L, ""))
            .expectNext(false)
            .verifyComplete();
    }

    @Test
    void isAllowedByToken() {
        StepVerifier.create(userQueueUseCase.isAllowedByToken("default", 100L, "d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8"))
            .expectNext(true)
            .verifyComplete();
    }

    @Test
    void generateToken() {
        StepVerifier.create(userQueueUseCase.generateToken("default", 100L))
            .expectNext("d333a5d4eb24f3f5cdd767d79b8c01aad3cd73d3537c70dec430455d37afe4b8")
            .verifyComplete();
    }
}