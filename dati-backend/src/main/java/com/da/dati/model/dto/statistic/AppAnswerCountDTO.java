package com.da.dati.model.dto.statistic;

import lombok.Data;

/**
 * App 用户提交答案数统计
 */
@Data
public class AppAnswerCountDTO {
    private Long appId;
    private Long answerCount;
}
