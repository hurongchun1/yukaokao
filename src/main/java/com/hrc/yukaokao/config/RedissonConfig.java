package com.hrc.yukaokao.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @FileName: RedissonConfig
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/21 15:44
 * @Version: 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    /**
     * 主机号
     */
    private String host;
    /**
     * 密码
     */
    private String password;

    /**
     * 端口号
     */
    private int port;
    /**
     * 使用的redis库
     */
    private int database;

    /**
     * redisson客户端
     * @return
     */
    @Bean
    public RedissonClient redisClient(){
        Config config = new Config();
        config.useSingleServer()
                .setAddress("redis://" + host + ":" + port)
                .setDatabase(database).setPassword(password);
        return Redisson.create(config);
    }
}
