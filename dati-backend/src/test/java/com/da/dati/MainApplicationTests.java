package com.da.dati;

import com.da.dati.manager.AiManager;
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
 * 主类测试
 *
 
 
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private AiManager aiManager;



    @Test
    void contextLoads() {
        System.out.println(aiManager.doAsyncRequest("你是一个Java程序员", "请用Java代码写一段冒泡排序算法"));
    }

}
