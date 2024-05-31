package com.da.dati.scoring;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.da.dati.manager.AiManager;
import com.da.dati.model.dto.question.QuestionAnswerDTO;
import com.da.dati.model.dto.question.QuestionContentDTO;
import com.da.dati.model.entity.App;
import com.da.dati.model.entity.Question;
import com.da.dati.model.entity.UserAnswer;
import com.da.dati.service.QuestionService;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
    // 分布式锁的 key
    private static final String AI_ANSWER_LOCK = "AI_ANSWER_LOCK";
    /**
     * AI 评分结果本地缓存
     */
    private final Cache<String, String> answerCacheMap =
            Caffeine.newBuilder().initialCapacity(1024)
                    // 缓存 5 分钟移除
                    .expireAfterAccess(5L, TimeUnit.MINUTES)
                    .build();
    @Resource
    private QuestionService questionService;
    @Resource
    private AiManager aiManager;
    @Resource
    private RedissonClient redissonClient;

    @Override
    public UserAnswer doScore(List<String> choices, App app) throws Exception {
        Long appId = app.getId();
        String choicesJsonStr = JSONUtil.toJsonStr(choices);
        String cacheKey = buildCacheKey(appId, choicesJsonStr);
        String resultJson = answerCacheMap.getIfPresent(cacheKey);
        // 如果有缓存（题目相同，并且用户的答案相同则命中缓存）
        if(StringUtils.isNotBlank(resultJson)){
            UserAnswer userAnswer = JSONUtil.toBean(resultJson, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choicesJsonStr);
            userAnswer.setResultPicture("https://img1.baidu.com/it/u=1073031278,915011163&fm=253&fmt=auto&app=138&f=JPEG?w=750&h=500");
            return userAnswer;
        }

        // 定义锁
        RLock lock = redissonClient.getLock(AI_ANSWER_LOCK + cacheKey);
        try{
            // 竞争锁
            boolean res = lock.tryLock(3, 15, TimeUnit.SECONDS);
            // 没抢到锁，强行返回
            if(!res){
                return null;
            }
            // 抢到锁，执行后续业务逻辑

            // 根据 appId 查询到题目
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
            // 封装 Prompt
            StringBuilder userMessage = new StringBuilder();
            userMessage.append(app.getAppName()).append("\n");
            userMessage.append(app.getAppDesc()).append("\n");
            userMessage.append(JSONUtil.toJsonStr(questionAnswerDTOList));
            // 调用ai
            String result = aiManager.doAsyncRequest(GENERATE_QUESTION_SYSTEM_MESSAGE, userMessage.toString());
            // 截取需要的 Json 信息
            int startIndex = result.indexOf("{");
            int endIndex = result.indexOf("}");
            result = result.substring(startIndex, endIndex + 1);
            // 设置缓存，保存格式格式：{ "appId + 用户答案" : "AI 测评结果" }
            answerCacheMap.put(cacheKey,result);
            // 构建返回值，填充对象属性
            UserAnswer userAnswer = JSONUtil.toBean(result, UserAnswer.class);
            userAnswer.setAppId(appId);
            userAnswer.setAppType(app.getAppType());
            userAnswer.setScoringStrategy(app.getScoringStrategy());
            userAnswer.setChoices(choicesJsonStr);
            userAnswer.setResultPicture("https://img1.baidu.com/it/u=1073031278,915011163&fm=253&fmt=auto&app=138&f=JPEG?w=750&h=500");
            return userAnswer;
        }finally {
            if(lock != null && lock.isLocked()){
                if(lock.isHeldByCurrentThread()){
                    lock.unlock();
                }
            }
        }
    }

    /**
     * 构建缓存 key （app id + 答案作为 key），
     * 如果多个用户做的题目相同并且给出的答案也相同，
     * 那么按照逻辑给出的 AI 评分结果也应该相同，那么就不需要再去问 AI，直接走缓存）
     * @param appId
     * @param choicesJsonStr
     * @return
     */
    private String buildCacheKey(Long appId, String choicesJsonStr){
        return DigestUtil.md5Hex(appId + choicesJsonStr);
    }


}
