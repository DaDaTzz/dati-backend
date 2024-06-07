package com.da.dati;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.da.dati.manager.AiManager;
import com.da.dati.mapper.UserAnswerMapper;
import com.da.dati.model.dto.statistic.AppAnswerCountDTO;
import com.da.dati.model.entity.UserAnswer;
import com.da.dati.service.UserAnswerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * 主类测试
 *
 
 
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private AiManager aiManager;
    @Resource
    private UserAnswerMapper userAnswerMapper;



    @Test
    void contextLoads() {
        System.out.println(aiManager.doAsyncRequest("你是一个Java程序员", "请用Java代码写一段冒泡排序算法"));
    }

    @Resource
    private UserAnswerService userAnswerService;
    @Test
    void test() {
        UserAnswer userAnswer1 = new UserAnswer();
        userAnswer1.setAppId(1L);
        userAnswer1.setUserId(1L);
        userAnswer1.setChoices("1");
        userAnswerService.save(userAnswer1);

        UserAnswer userAnswer2 = new UserAnswer();
        userAnswer2.setAppId(2L);
        userAnswer2.setUserId(1L);
        userAnswer2.setChoices("2");
        userAnswerService.save(userAnswer2);

        UserAnswer userAnswerOne = userAnswerService.getOne(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 1L));
        System.out.println(JSONUtil.toJsonStr(userAnswerOne));

        UserAnswer userAnswerTwo = userAnswerService.getOne(Wrappers.lambdaQuery(UserAnswer.class).eq(UserAnswer::getAppId, 2L));
        System.out.println(JSONUtil.toJsonStr(userAnswerTwo));

    }

    @Test
    void test1(){
        List<AppAnswerCountDTO> appAnswerCountDTOS = userAnswerMapper.doAppAnswerCount();
        System.out.println(appAnswerCountDTOS);
        System.out.println(userAnswerMapper.doAppAnswerResultCount(1L));
    }
}
