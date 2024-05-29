package com.da.dati.scoring;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.da.dati.manager.AiManager;
import com.da.dati.model.dto.question.QuestionAnswerDTO;
import com.da.dati.model.dto.question.QuestionContentDTO;
import com.da.dati.model.entity.App;
import com.da.dati.model.entity.Question;
import com.da.dati.model.entity.UserAnswer;
import com.da.dati.service.QuestionService;
import com.da.dati.service.UserAnswerService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * AI 测评类应用评分策略
 */
@ScoringStrategyConfig(appType = 1, scoringStrategy = 1)
public class AiTestScoringStrategy implements ScoringStrategy {

    private static final String GENERATE_QUESTION_SYSTEM_MESSAGE = "你是一位严谨的判题专家，我会给你如下信息：\n" +
            "```\n" +
            "应用名称，\n" +
            "【【【应用描述】】】，\n" +
            "题目和用户回答的列表:格式为[{“标题”：“题目”，“应答”：“用户回答”}]\n" +
            "```\n" +
            "\n" +
            "请你根据上述信息，按照以下步骤来对用户进行评价：\n" +
            "1.要求:需要给出一个明确的评价结果，包括评价名称(尽量简短)和评价描述(尽量详细，大于200字)\n" +
            "2.严格按照下面的iso N格式输出评价名称和评价描述\n" +
            "```\n" +
            "{\"resultName\"：\"评价名称\",\"resultDesc\":\"评价描述\"}\n" +
            "```\n" +
            "3.返回格式必须为JSON对象";

    @Resource
    private QuestionService questionService;

    @Resource
    private UserAnswerService userAnswerService;

    @Resource
    private AiManager aiManager;


    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        // 1. 根据 appId 查询到题目
        Question question = questionService.getOne(
                Wrappers.lambdaQuery(Question.class).eq(Question::getAppId, appId)
        );


        List<QuestionContentDTO> questionContentDTOList = JSONUtil.toList(question.getQuestionContent(), QuestionContentDTO.class);
        ArrayList<QuestionAnswerDTO> questionAnswerDTOList = new ArrayList<>();

        for (int i = 0; i < questionContentDTOList.size(); i++) {
            QuestionContentDTO questionContentDTO = questionContentDTOList.get(i);
            QuestionAnswerDTO questionAnswerDTO = new QuestionAnswerDTO();
            questionAnswerDTO.setTitle(questionContentDTO.getTitle());
            List<QuestionContentDTO.Option> optionList = questionContentDTO.getOptions();
            for (int j = 0; j < optionList.size(); j++) {
                QuestionContentDTO.Option option = optionList.get(j);
                if(option.getKey().equals(choices.get(i))){
                    questionAnswerDTO.setUserAnswer(option.getValue());
                    break;
                }
            }
            questionAnswerDTOList.add(questionAnswerDTO);
        }
        StringBuilder userMessage = new StringBuilder();
        userMessage.append(app.getAppName()).append("\n");
        userMessage.append(app.getAppDesc()).append("\n");
        userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
        // 调用ai
        String result = aiManager.doAsyncRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage.toString());
        int startIndex = result.indexOf("{");
        int endIndex = result.indexOf("}");
        result = result.substring(startIndex, endIndex + 1);
        UserAnswer userAnswer = JSONUtil.toBean(result, UserAnswer.class);
        userAnswer.setAppId(appId);
        userAnswer.setAppType(app.getAppType());
        userAnswer.setScoringStrategy(app.getScoringStrategy());
        userAnswer.setChoices(JSONUtil.toJsonStr(choices));
        userAnswer.setResultPicture("https://img1.baidu.com/it/u=1073031278,915011163&fm=253&fmt=auto&app=138&f=JPEG?w=750&h=500");
        return userAnswer;
    }
}
