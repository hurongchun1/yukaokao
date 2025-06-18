package com.hrc.yukaokao.model.dto.appRequest;

import com.hrc.yukaokao.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询应用表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppQueryRequest extends PageRequest implements Serializable {

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

    /**
     * 审核状态：0-待审核, 1-通过, 2-拒绝
     */
    private Integer reviewstatus;

    /**
     * 审核信息
     */
    private String reviewmessage;

    /**
     * 审核人 id
     */
    private Long reviewerid;


    /**
     * 创建用户 id
     */
    private Long userid;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}