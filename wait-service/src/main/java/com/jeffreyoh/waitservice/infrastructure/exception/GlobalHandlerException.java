package com.jeffreyoh.waitservice.infrastructure.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@RestControllerAdvice
public class GlobalHandlerException {

    @ExceptionHandler(ApplicationException.class)
    public Mono<ResponseEntity<ServerExceptionResponse>> applicationExceptionHandler(ApplicationException e) {
        return Mono.just(
            ResponseEntity
                .status(e.getHttpStatus())
                .body(new ServerExceptionResponse(e.getCode(), e.getReason()))
        );
    }

    public record ServerExceptionResponse(String code, String reason) {

    }

}
