package com.hrc.yukaokao;

import com.hrc.yukaokao.common.PageRequest;
import com.hrc.yukaokao.controller.QuestionController;
import com.hrc.yukaokao.model.dto.aiRequest.AiGenerateQuestionRequest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @FileName: ExecutorTest
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/23 15:19
 * @Version: 1.0.0
 */
@SpringBootTest
public class ExecutorTest {
    @Resource
    private QuestionController questionController;

    @Test
    public void aiGenerateStreamQuestionTest() throws InterruptedException {
        AiGenerateQuestionRequest aiGenerateQuestionRequest = new AiGenerateQuestionRequest();
        aiGenerateQuestionRequest.setAppId(3L);
        aiGenerateQuestionRequest.setQuestionNumber(2);
        aiGenerateQuestionRequest.setOptionNumber(3);
        //普通用户
        questionController.aiGenerateStreamQuestionTest(aiGenerateQuestionRequest,false);
        //普通用户
        questionController.aiGenerateStreamQuestionTest(aiGenerateQuestionRequest,false);
        //普通用户
        questionController.aiGenerateStreamQuestionTest(aiGenerateQuestionRequest,false);
        //会员用户
        questionController.aiGenerateStreamQuestionTest(aiGenerateQuestionRequest,true);

        Thread.sleep(10000000000L);
    }
}
