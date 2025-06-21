package com.hrc.yukaokao.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @FileName: CoustomTestScoringStrategy
 * @Description: 自定义测评
 * @Author: hrc
 * @CreateTime: 2025/6/17 21:45
 * @Version: 1.0.0
 */
@ScoringStrategyConfig(applyType = 1,scoringStrategy = 0)
public class CoustomTestScoringStrategy implements ScoringStrategy {
    @Resource
    private  QuestionService questionService;
    @Resource
    private ScoringResultService scoringResultService;

    /**
     * 根据用户的选项，得出测评分数
     *
     * @param choices：用户测评的选项
     * @param app：这些题目的应用
     * @return 用户的测评结果
     */
    @Override
    public UserAnswer doScore(List<String> choices, App app) {
        //1. 根据id查询到题目信息，和题目结果
        Long appId = app.getId();
        // 获取题目
        Question question = questionService.getOne(Wrappers
                .lambdaQuery(Question.class).eq(Question::getAppId, appId));
        // 评分结果信息，评价 MBTI人格测试，这道应用题目的结果一共有16个，所以评分结果信息会有多个，以列表的形式存储
        List<ScoringResult> scoringResultList = scoringResultService
                .list(Wrappers.lambdaQuery(ScoringResult.class).eq(ScoringResult::getAppId, appId));
        //题目列表的 String格式
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContents = questionVO.getQuestionContent();

        //2. 判断一下用户的每个属性的个数，比如 I为多少，E为多少
        Map<String, Integer> optionCount = new HashMap<>();
        //检索答题的数量，与题目数量是否一致，不一致则报错
        ThrowUtils.throwIf(questionContents.size()!=choices.size(), ErrorCode.PARAMS_ERROR,"题目和用户答案数量不一致");

        // 遍历题目列表
        for (QuestionContentDTO questionContentDTO : questionContents) {
            // 遍历答案列表
            //这里是因为第一个题 option 的答案与第一个题目choices 的第一个元素进行匹配
            int flag = 0;
            for (QuestionContentDTO.Option option : questionContentDTO.getOptions()) {
                String answer = choices.get(flag++);
                // 如果答案和选项的key匹配
                if (option.getKey().equals(answer)) {
                    // 获取选项的result属性
                    String result = option.getResult();
                    // 如果result属性不在optionCount中，初始化为0
                    optionCount.putIfAbsent(result, 0);
                    // 在optionCount中增加计数
                    optionCount.put(result, optionCount.get(result) + 1);
                }
            }
        }
        //3. 遍历每种评分结果，计算哪个结果的得分更高
        // 返回最高分数和最高分数对应的评分结果
        int maxScore = 0;
        ScoringResult maxScoreResult = scoringResultList.get(0);

        // 遍历评分结果列表
        for (ScoringResult result : scoringResultList) {
            String resultProp = result.getResultProp();
            List<String> resultprop = JSONUtil.toList(resultProp, String.class);
            // 计算当前评分结果的分数 ,
            // prop：代表着评分分数的元素（["I","S","T","J"]中的其中一个元素）,optionCount：[I:10，E:5，"S":7,"N":6]
            //第一次的时候，prop为 "I"，然后 "I"有10个，"S"有7个，最后这几个元素相加得出分数为 17.
            int score = resultprop.stream()
                    .mapToInt(prop -> optionCount.getOrDefault(prop, 0)).sum();

            //遍历每个评分结果来，计算哪个结果分数更高
            if (score > maxScore) {
                maxScore = score;
                maxScoreResult = result;
            }
        }


        //4. 构造所有的最高结果，填充答案对象的属性
        UserAnswer userAnswer = new UserAnswer();
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultId(maxScoreResult.getId());
        userAnswer.setResultName(maxScoreResult.getResultName());
        userAnswer.setResultDesc(maxScoreResult.getResultDesc());
        userAnswer.setResultPicture(maxScoreResult.getResultPicture());

        return userAnswer;
    }
}
