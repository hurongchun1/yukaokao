package com.hrc.yukaokao.scoring;

import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.UserAnswer;

import java.util.List;

/**
 * @FileName: CustomScoreScoringStrategy
 * @Description: 自定义得分
 * @Author: hrc
 * @CreateTime: 2025/6/17 21:46
 * @Version: 1.0.0
 */
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Override
    public UserAnswer doScore(List<String> choices, App app) {

        return null;
    }
}
