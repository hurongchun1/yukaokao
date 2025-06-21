package com.hrc.yukaokao.scoring;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @FileName: ScoringStrategyConfig
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/18 16:15
 * @Version: 1.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface ScoringStrategyConfig {
    /**
     * 应用类型
     * @return
     */
    int applyType();

    /**
     * 评分策略
     * @return
     */
    int scoringStrategy();
}
