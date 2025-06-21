package com.hrc.yukaokao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.ChatCompletionRequest;
import com.zhipu.oapi.service.v4.model.ChatMessage;
import com.zhipu.oapi.service.v4.model.ChatMessageRole;
import com.zhipu.oapi.service.v4.model.ModelApiResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: ZhiPuAiTest
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/19 10:40
 * @Version: 1.0.0
 */
@SpringBootTest
public class ZhiPuAiTest {
    @Resource
    private ClientV4 clientV4;
    /**
     * 同步调用
     */
    @Test
    public   void testInvokeTest() {
        //初始化客户端
        ClientV4 client = new ClientV4.Builder("53042fc4424c4627b4570f8406abbbb3.wUlOzqC2azW4tmdL").build();
        //构造请求
        List<ChatMessage> messages = new ArrayList<>();
        ChatMessage chatMessage = new ChatMessage(ChatMessageRole.USER.value(), "给一句高考加油的flag");
        messages.add(chatMessage);
//        String requestId = String.format("", System.currentTimeMillis());
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.FALSE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
//                .requestId(requestId)
                .build();
        //调用
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        //获取第一条消息，因为 AI 默认是提供一个 对话的，所以直接取 0即可
        System.out.println("model output:" + invokeModelApiResp.getData().getChoices().get(0));

    }

}
