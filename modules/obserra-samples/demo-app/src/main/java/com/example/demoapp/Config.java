package com.example.demoapp;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class Config implements SchedulingConfigurer {

    @Bean(name = "taskExecutor")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor exec = new ThreadPoolTaskExecutor();
        exec.setCorePoolSize(5);
        exec.setMaxPoolSize(20);          // ← this is your executor.pool.max
        exec.setQueueCapacity(500);
        exec.setThreadNamePrefix("my-exec-");
        exec.initialize();
        return exec;
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar registrar) {
        if (registrar.getScheduler() instanceof ThreadPoolExecutor executor) {
            executor.setMaximumPoolSize(10);
        }
    }
}
