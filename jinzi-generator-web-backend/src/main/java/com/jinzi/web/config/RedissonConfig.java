package com.jinzi.web.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson 配置
 */
@Configuration
@Data
@Slf4j
public class RedissonConfig {

    @Bean
    public RedissonClient redissonClient() {
        // 1. 创建配置
        Config config = new Config();
        String redisAddress ="redis://124.223.222.249:6379";
        config.useSingleServer().setAddress(redisAddress).setDatabase(1).setPassword("123456");
        // 2. 创建实例
        return Redisson.create(config);
    }
}
