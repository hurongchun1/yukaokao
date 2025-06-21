package com.hrc.yukaokao.scoring;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hrc.yukaokao.common.ErrorCode;
import com.hrc.yukaokao.exception.ThrowUtils;
import com.hrc.yukaokao.manager.AiManager;
import com.hrc.yukaokao.model.dto.questionAnswer.QuestionAnswerDTO;
import com.hrc.yukaokao.model.dto.questionRequest.QuestionContentDTO;
import com.hrc.yukaokao.model.entity.App;
import com.hrc.yukaokao.model.entity.Question;
import com.hrc.yukaokao.model.entity.ScoringResult;
import com.hrc.yukaokao.model.entity.UserAnswer;
import com.hrc.yukaokao.model.enums.ApplyTypeEnum;
import com.hrc.yukaokao.model.vo.QuestionVO;
import com.hrc.yukaokao.service.QuestionService;
import com.hrc.yukaokao.service.ScoringResultService;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.caffeine.CaffeineCache;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @FileName: CoustomTestScoringStrategy
 * @Description: AI 测评
 * @Author: hrc
 * @CreateTime: 2025/6/20 21:45
 * @Version: 1.0.0
 */
@ScoringStrategyConfig(applyType = 1,scoringStrategy = 1)
public class AiTestScoringStrategy implements ScoringStrategy {
    @Resource
    private  QuestionService questionService;
    @Resource
    private AiManager aiManager;

    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder()
                    //初始化容量
                    .initialCapacity(1024)
                    //缓存5分钟移除
                    .expireAfterAccess(5L,TimeUnit.MINUTES).build();

    @Resource
    private RedissonClient redissonClient;
    /**
     * redisson的key前缀
     */
    public static final String PREFIX_LOCK = "AI:ANSWER:LOCK:";


    //系统的AI prompt
    public static final String SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表：格式为 [{\"title\": \"题目\",\"answer\": \"用户回答\"}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1. 要求：需要给出一个明确的评价结果，包括评价名称（尽量简短）和评价描述（尽量详细，大于 200 字）\n" +
            "2. 严格按照下面的 json 格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\": \"评价名称\", \"resultDesc\": \"评价描述\"}\n" +
            "```\n" +
            "3. 返回格式必须为 JSON 对象\n";

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
        QuestionVO questionVO = QuestionVO.objToVo(question);
        List<QuestionContentDTO> questionContent = questionVO.getQuestionContent();
        String userMessage = getAiTestScoringUserMessage(app, questionContent, choices);
        //通过 caffeine 查看是否有结果
        String choicesStr = JSONUtil.toJsonStr(choices);
        String caffeineKey = buildCacheKey(appId, choicesStr);
        String userAnswerData = answerCacheMap.getIfPresent(caffeineKey);
        if(StrUtil.isNotBlank(userAnswerData)){
            UserAnswer userAnswer = JSONUtil.toBean(userAnswerData, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choicesStr);
            return userAnswer;
        }

        RLock lock = redissonClient.getLock(PREFIX_LOCK + appId);
        try{
            if (!lock.isLocked()) {
                return  null;
            }
            //通过ai 写入userMessagePrompt 和 systemMessagePrompt
            String jsonUserAnswer = aiManager.doSyncStableRequest(SYSTEM_MESSAGE, userMessage);
            //结果进行处理
            int start = jsonUserAnswer.indexOf('{');
            int end = jsonUserAnswer.lastIndexOf('}');
            String userAnswerMessage = jsonUserAnswer.substring(start,end+1);
            //存储数据到 caffeine缓存中
            answerCacheMap.put(caffeineKey,userAnswerMessage);

            //4. 构造所有的最高结果，填充答案对象的属性
            UserAnswer userAnswer = JSONUtil.toBean(userAnswerMessage, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choicesStr);
            return userAnswer;
        }finally {
            if(lock !=null && lock.isLocked() && lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }
    }

    /**
     * 生成caffeine的key
     * @param appId
     * @param choicesStr
     * @return
     */
    private String buildCacheKey(Long appId,String choicesStr){
        return DigestUtil.md5Hex(appId+":"+choicesStr);
    }

    /**
     * 生成用户的prompt
     * @param app 应用
     * @param questionContents 选项题目
     * @param choices 用户选项
     * @return 返回用户的prompt
     */
    private String getAiTestScoringUserMessage(App app,List<QuestionContentDTO> questionContents,List<String> choices){
        StringBuffer userMessage = new StringBuffer();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        List<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();
        //循环整合题目标题和对应用户答案
        for (int i = 0; i < questionContents.size(); i++) {
            String title = questionContents.get(i).getTitle();
            String choice = choices.get(i);
            questionAnswerDTOList.add(new QuestionAnswerDTO(title,choice));
        }
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        return userMessage.toString();
    }


}
