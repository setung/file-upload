package com.example.uploadserver;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {

    @Bean(name = "threadPoolTaskExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(1); // 기본 스레드 수
        taskExecutor.setMaxPoolSize(1); // 최대 스레드 수
        //taskExecutor.setQueueCapacity(100); // Queue 사이즈, default: Int.MAX
        taskExecutor.setThreadNamePrefix("Executor-");
        return taskExecutor;
    }

}

