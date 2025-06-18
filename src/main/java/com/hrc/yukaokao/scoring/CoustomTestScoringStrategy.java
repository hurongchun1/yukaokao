package com.hrc.yukaokao.scoring;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.Question;
import com.hrc.yukaokao.model.entity.ScoringResult;
import com.hrc.yukaokao.model.entity.UserAnswer;
import com.hrc.yukaokao.service.QuestionService;
import com.hrc.yukaokao.service.ScoringResultService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @FileName: CoustomTestScoringStrategy
 * @Description: 自定义测评
 * @Author: hrc
 * @CreateTime: 2025/6/17 21:45
 * @Version: 1.0.0
 */
@Component
public class CoustomTestScoringStrategy implements ScoringStrategy {
    @Resource
    private  QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;

    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        //根据id查询到题目信息，和题目结果
        Long appId = app.getId();
        // 获取题目
        Question question = questionService.getOne(Wrappers
                .lambdaQuery(Question.class).eq(Question::getAppid, appId));
        // 获取答案
        List<ScoringResult> scoringResultList = scoringResultService
                .list(Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppid, appId));

        //判断一下用户的每个属性的个数，比如 I为多少，E为多少

        //遍历每个评分结果来，计算哪个结果分数更高

        //构造所有的最高结果，填充答案对象的属性


        return null;
    }
}
