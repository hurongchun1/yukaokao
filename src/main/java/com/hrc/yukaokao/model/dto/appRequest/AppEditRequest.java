package com.hrc.yukaokao.model.dto.appRequest;

import lombok.Data;

import java.io.Serializable;

/**
 * 编辑应用表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class AppEditRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 应用名
     */
    private String appname;

    /**
     * 应用描述
     */
    private String appdesc;

    /**
     * 应用图标
     */
    private String appicon;

    /**
     * 应用类型（0-得分类，1-测评类）
     */
    private Integer apptype;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringstrategy;


    private static final long serialVersionUID = 1L;
}