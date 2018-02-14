package com.github.damianmcdonald.springlongpolling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringLongPollingApplication {


    public static void main(String[] args) {
        SpringApplication.run(SpringLongPollingApplication.class, args);
    }
}
