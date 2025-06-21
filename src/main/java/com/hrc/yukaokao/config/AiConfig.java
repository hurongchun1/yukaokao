package com.hrc.yukaokao.config;

import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @FileName: AiConfig
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/19 11:40
 * @Version: 1.0.0
 */
@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    private String apiKey;
    @Bean
    public ClientV4 clientV4(){
        return  new ClientV4.Builder(apiKey).build();
    }
}
