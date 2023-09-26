package com.example.uploadserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class UploadServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(UploadServerApplication.class, args);
    }

}
