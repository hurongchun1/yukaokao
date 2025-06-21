package com.hrc.yukaokao.manager;

import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @FileName: AiManager
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/19 14:23
 * @Version: 1.0.0
 */
@Component
public class AiManager {
    @Resource
    private ClientV4 clientV4;
    /**
     * 稳定的随机数
     */
    private static final float STABLE_TEMPERATURE = 0.05f;
    /**
     * 不稳定的随机数
     */
    private static final float UNSTABLE_TEMPERATURE = 0.99f;

    /**
     * 同步不稳定的请求（AI方法）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncUnStableRequest(String systemMessage,String userMessage){
        return doSyncRequest(systemMessage,userMessage,UNSTABLE_TEMPERATURE);
    }

    /**
     * 同步较稳定的流式请求（AI方法）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public Flowable<ModelData> doSyncStreamStableRequest(String systemMessage,String userMessage){
        return doSyncStreamRequest(systemMessage,userMessage,STABLE_TEMPERATURE);
    }

    /**
     * 同步不稳定的请求（AI方法）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public Flowable<ModelData> doSyncStreamUnStableRequest(String systemMessage,String userMessage){
        return doSyncStreamRequest(systemMessage,userMessage,UNSTABLE_TEMPERATURE);
    }
    /**
     * 同步较稳定的请求（AI方法）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncStableRequest(String systemMessage,String userMessage){
        return doSyncRequest(systemMessage,userMessage,STABLE_TEMPERATURE);
    }

    /**
     * 同步的请求方法（AI方式）
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public String doSyncRequest(String systemMessage,String userMessage,float temperature){
        return doRequest(systemMessage,userMessage,Boolean.FALSE,temperature);
    }

    /**
     * 同步的流式 请求方法（AI方式）
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doSyncStreamRequest(String systemMessage,String userMessage,float temperature){
        return doStreamRequest(systemMessage,userMessage,temperature);
    }


    /**
     * 请求通用的流式方法（对话型的请求AI方法）
     * @param systemMessage
     * @param userMessage
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage,String userMessage,float temperature){
        //构造请求
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.USER.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(systemChatMessage);
        chatMessageList.add(userChatMessage);

        return doStreamRequest(chatMessageList,temperature);
    }

    /**
     * 请求通用方法（对话型的请求AI方法）
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(String systemMessage,String userMessage,boolean stream,float temperature){
        //构造请求
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.USER.value(), systemMessage);
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(systemChatMessage);
        chatMessageList.add(userChatMessage);

        return doRequest(chatMessageList,stream,temperature);
    }

    /**
     * 请求通用方法
     * @param chatMessageList
     * @param stream
     * @param temperature
     * @return
     */
    public String doRequest(List<ChatMessage> chatMessageList, boolean stream,float temperature){
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(chatMessageList)
                .build();
        //调用
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        //获取第一条消息，因为 AI 默认是提供一个 对话的，所以直接取 0即可
        return invokeModelApiResp.getData().getChoices().get(0).getMessage().getContent().toString();
    }

    /**
     * 流失请求通用方法
     * @param chatMessageList
     * @param temperature
     * @return
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> chatMessageList, float temperature){
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .temperature(temperature)
                .invokeMethod(Constants.invokeMethod)
                .messages(chatMessageList)
                .build();
        //调用
        ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
        //获取第一条消息，因为 AI 默认是提供一个 对话的，所以直接取 0即可
        return invokeModelApiResp.getFlowable();
    }
}
