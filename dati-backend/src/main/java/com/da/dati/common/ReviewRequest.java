package com.da.dati.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 审核请求
 *
 * @author <a href="https://github.com/lida">程序员鱼皮</a>
 * @from <a href="https://da.icu">编程导航知识星球</a>
 */
@Data
public class ReviewRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 状态 0-待审核 1-审核通过 2-审核不通过
     */
    private Integer reviewStatus;

    /**
     * 审核信息
     */
    private String reviewMessage;

    private static final long serialVersionUID = 1L;
}