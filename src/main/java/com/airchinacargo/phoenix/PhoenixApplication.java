package com.airchinacargo.phoenix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ChenYu 2018 03 13
 */
@SpringBootApplication
@EnableScheduling
public class PhoenixApplication {

    public static void main(String[] args) {
        SpringApplication.run(PhoenixApplication.class, args);
    }
}
