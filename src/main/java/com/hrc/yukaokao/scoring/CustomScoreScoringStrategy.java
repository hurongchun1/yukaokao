package com.hrc.yukaokao.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hrc.yukaokao.common.ErrorCode;
import com.hrc.yukaokao.exception.ThrowUtils;
import com.hrc.yukaokao.model.dto.questionRequest.QuestionContentDTO;
import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.Question;
import com.hrc.yukaokao.model.entity.ScoringResult;
import com.hrc.yukaokao.model.entity.UserAnswer;
import com.hrc.yukaokao.model.vo.QuestionVO;
import com.hrc.yukaokao.service.QuestionService;
import com.hrc.yukaokao.service.ScoringResultService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @FileName: CustomScoreScoringStrategy
 * @Description: 自定义得分类策略
 * @Author: hrc
 * @CreateTime: 2025/6/17 21:46
 * @Version: 1.0.0
 */
@ScoringStrategyConfig(applyType = 0,scoringStrategy = 0)
public class CustomScoreScoringStrategy implements ScoringStrategy {
    @Resource
    private QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;
    @Override
    public UserAnswer doScore(List<String> choices, App app) {
//        1）根据 id 查询到题目和题目结果信息（按分数降序排序）
        Long appId = app.getId();
        // 获取题目
        Question question = questionService.getOne(Wrappers
                .lambdaQuery(Question.class).eq(Question::getAppId, appId));
        // 获取题目结果信息，按照分数降序排序
        List<ScoringResult> scoringResultList = scoringResultService
                .list(Wrappers.lambdaQuery(ScoringResult.class)
                        .eq(ScoringResult::getAppId, appId).orderByDesc(ScoringResult::getResultScoreRange));
        //题目列表的 String格式
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContents = questionVO.getQuestionContent();
//        2）统计用户的总得分
        int totalScore = 0;
        //检索答题的数量，与题目数量是否一致，不一致则报错
        ThrowUtils.throwIf(questionContents.size()!=choices.size(), ErrorCode.PARAMS_ERROR,"题目和用户答案数量不一致");
        int flag = 0;
        // 遍历题目列表，这里进去就是每一道题目
        for (QuestionContentDTO questionContentDTO : questionContents) {
            // 遍历答案列表
            //这里是因为第一个题 option 的答案与第一个题目choices 的第一个元素进行匹配，而且这里的答案在同一题目里，不能改变
            String answer = choices.get(flag++);
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                // 如果答案和选项的key匹配
                if (option.getKey().equals(answer)) {
                    int score = Optional.of(option.getScore()).orElse(0);
                    totalScore += score;
                }
            }
        }
//        3）遍历得分结果，找到第一个用户分数大于得分范围的结果，作为最终结果，由于这里的得分是倒序排序的，所以找到后break即可
        ScoringResult maxScoringResult = scoringResultList.get(0);
        for (ScoringResult scoringResult : scoringResultList) {
            if(totalScore >= scoringResult.getResultScoreRange()){
                maxScoringResult = scoringResult;
                break;
            }
        }
//        4）构造返回值，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoringResult.getId());
        userAnswer.setResultName(maxScoringResult.getResultName());
        userAnswer.setResultDesc(maxScoringResult.getResultDesc());
        userAnswer.setResultPicture(maxScoringResult.getResultPicture());
        userAnswer.setResultScore(totalScore);
        return userAnswer;
    }
}
