package io.terminus.demo.dubboprovide.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix="spring.redis")
public class RedisConfig {

    private String host;
    private int port = 6379;
    private int database = 0;
    private String password;

    @Value("${spring.redis.sentinel.nodes}")
    private String nodes;
    @Value("${spring.redis.sentinel.master}")
    private String master;
}