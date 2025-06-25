package com.hrc.yukaokao.scoring;

import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.UserAnswer;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @FileName: ScoringStrategy
 * @Description: 评分策略模式接口
 * @Author: hrc
 * @CreateTime: 2025/6/17 21:42
 * @Version: 1.0.0
 */

public interface ScoringStrategy {
    /**
     * 得分结果
     * @return
     */
    UserAnswer doScore(List<String> choices, App app) throws InterruptedException;
}
