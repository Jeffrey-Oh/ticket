package com.jeffreyoh.waitservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class WaitServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WaitServiceApplication.class, args);
    }

}
