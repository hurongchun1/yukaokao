package com.hrc.yukaokao;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hrc.yukaokao.model.entity.UserAnswer;
import com.hrc.yukaokao.service.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @FileName: ShardinSphereTest
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/22 13:07
 * @Version: 1.0.0
 */
@SpringBootTest
public class ShardingSphereTest {
    @Resource
    private UserAnswerService userAnswerService;

    @Test
    public void shardingSphereTest01() {
        UserAnswer userAnswer1 = new UserAnswer();

        userAnswer1.setAppId(1L);
        userAnswer1.setUserid(1L);
        userAnswer1.setChoices("1");
        userAnswerService.save(userAnswer1);

        UserAnswer userAnswer2 = new UserAnswer();
        userAnswer2.setAppId(2L);
        userAnswer2.setUserid(1L);
        userAnswer2.setChoices("2");
        userAnswerService.save(userAnswer2);

        UserAnswer userAnswerOne = userAnswerService.getOne(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 1L));
        System.out.println(JSONUtil.toJsonStr(userAnswerOne));

        UserAnswer userAnswerTwo = userAnswerService.getOne(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 2L));
        System.out.println(JSONUtil.toJsonStr(userAnswerTwo));
    }
}
