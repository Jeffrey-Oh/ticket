package com.jeffreyoh.webservice;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.Arrays;
import java.util.Optional;

@Controller
@SpringBootApplication
public class WebServiceApplication {

    RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        SpringApplication.run(WebServiceApplication.class, args);
    }

    @GetMapping("/")
    public String index(
        @RequestParam(name = "queue", defaultValue = "default") String queue,
        @RequestParam(name = "userId") Long userId,
        HttpServletRequest request
    ) {
        Cookie[] cookies = request.getCookies();
        String cookieName = "user-queue-%s-token".formatted(queue);

        String token = "";
        if (cookies != null) {
            Optional<Cookie> cookie = Arrays.stream(cookies).filter(i -> i.getName().equalsIgnoreCase(cookieName)).findFirst();
            token = cookie.orElse(new Cookie(cookieName, "")).getValue();
        }

        final URI uri = UriComponentsBuilder
            .fromUriString("http://localhost:9010")
            .path("/api/v1/queue/allowed")
            .queryParam("queue", queue)
            .queryParam("userId", userId)
            .queryParam("token", token)
            .encode()
            .build()
            .toUri();

        ResponseEntity<AllowedUserResponse> response = restTemplate.getForEntity(uri, AllowedUserResponse.class);
        if (response.getBody() == null || !response.getBody().allowed()) {
            // 대기 웹페이지로 리다이렉트
            return "redirect:http://localhost:9010/waiting-room?userId=%d&redirectUrl=%s".formatted(
                userId, "http://localhost:9000?userId=%d".formatted(userId)
            );
        }

        // 허용 상태라면 해당 페이지를 진입
        return "index";
    }

    public record AllowedUserResponse(Boolean allowed) {

    }

}
