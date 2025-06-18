package com.hrc.yukaokao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 用户答题记录
 *
 * @TableName user_answer
 */
@TableName(value = "user_answer")
@Data
public class UserAnswer {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 应用 id
     */
    private Long appid;

    /**
     * 应用类型（0-得分类，1-角色测评类）
     */
    private Integer apptype;

    /**
     * 评分策略（0-自定义，1-AI）
     */
    private Integer scoringstrategy;

    /**
     * 用户答案（JSON 数组）
     */
    private String choices;

    /**
     * 评分结果 id
     */
    private Long resultid;

    /**
     * 结果名称，如物流师
     */
    private String resultname;

    /**
     * 结果描述
     */
    private String resultdesc;

    /**
     * 结果图标
     */
    private String resultpicture;

    /**
     * 得分
     */
    private Integer resultscore;

    /**
     * 用户 id
     */
    private Long userid;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isdelete;
}