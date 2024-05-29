package com.da.dati.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 生成题目请求类
 */
@Data
public class AiGeneratorQuestionRequest implements Serializable {

    /**
     * 应用 id
     */
    private String appId;

    /**
     * 题目数
     */
    int questionNumber = 10;

    /**
     * 选项数
     */
    int optionNumber = 2;


    private static final long serialVersionUID = 1L;

}
