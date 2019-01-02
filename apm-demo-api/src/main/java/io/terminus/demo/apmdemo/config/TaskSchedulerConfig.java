package io.terminus.demo.apmdemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author: liuhaoyang
 **/
@Configuration
@EnableAsync
public class TaskSchedulerConfig {

    //@Value("${task.scheduler.poolSize}")
    private int poolSize = 10;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler executor = new ThreadPoolTaskScheduler();
        executor.setPoolSize(poolSize);
        executor.initialize();
        return executor;
    }
}
