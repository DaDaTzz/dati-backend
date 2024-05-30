package com.da.dati.manager;

import com.da.dati.common.ErrorCode;
import com.da.dati.exception.BusinessException;
import com.zhipu.oapi.ClientV4;
import com.zhipu.oapi.Constants;
import com.zhipu.oapi.service.v4.model.*;
import io.reactivex.Flowable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * Ai 能力
 */
@Component
public class AiManager {

    @Resource
    private ClientV4 clientV4;


    /**
     * 通用异步请求
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doAsyncRequest(String systemMessage, String userMessage){
        return doRequest(systemMessage,userMessage, Boolean.FALSE);
    }

    /**
     * 通用同步请求
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public String doSyncRequest(String systemMessage, String userMessage){
        return doRequest(systemMessage,userMessage, Boolean.TRUE);
    }


    /**
     * 通用请求（简化消息传递）
     * @param systemMessage
     * @param userMessage
     * @param stream
     * @return
     */
    public String doRequest(String systemMessage, String userMessage, Boolean stream){
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(userChatMessage);
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(systemChatMessage);
        return doRequest(chatMessageList, stream);
    }

    /**
     * 通用请求
     * @param messages
     * @param stream
     * @return
     */
    public String doRequest(List<ChatMessage> messages, Boolean stream){
        // 构建请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(stream)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try{
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getData().getChoices().get(0).toString();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }


    /**
     * 通用流式请求
     * @param messages
     * @return
     */
    public Flowable<ModelData> doStreamRequest(List<ChatMessage> messages){
        // 构建请求
        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(Constants.ModelChatGLM4)
                .stream(Boolean.TRUE)
                .invokeMethod(Constants.invokeMethod)
                .messages(messages)
                .build();
        try{
            ModelApiResponse invokeModelApiResp = clientV4.invokeModelApi(chatCompletionRequest);
            return invokeModelApiResp.getFlowable();
        }catch (Exception e){
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, e.getMessage());
        }
    }


    /**
     * 通用流式请求（简化消息传递）
     * @param systemMessage
     * @param userMessage
     * @return
     */
    public Flowable<ModelData> doStreamRequest(String systemMessage, String userMessage){
        List<ChatMessage> chatMessageList = new ArrayList<>();
        ChatMessage userChatMessage = new ChatMessage(ChatMessageRole.SYSTEM.value(), systemMessage);
        chatMessageList.add(userChatMessage);
        ChatMessage systemChatMessage = new ChatMessage(ChatMessageRole.USER.value(), userMessage);
        chatMessageList.add(systemChatMessage);
        return doStreamRequest(chatMessageList);
    }

}
