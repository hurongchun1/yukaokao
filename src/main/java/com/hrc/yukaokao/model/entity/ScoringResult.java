package com.hrc.yukaokao.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * 评分结果
 *
 * @TableName scoring_result
 */
@TableName(value = "scoring_result")
@Data
public class ScoringResult {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 结果名称，如物流师
     */
    private String resultname;

    /**
     * 结果描述
     */
    private String resultdesc;

    /**
     * 结果图片
     */
    private String resultpicture;

    /**
     * 结果属性集合 JSON，如 [I,S,T,J]
     */
    private String resultprop;

    /**
     * 结果得分范围，如 80，表示 80及以上的分数命中此结果
     */
    private Integer resultscorerange;

    /**
     * 应用 id
     */
    private Long appid;

    /**
     * 创建用户 id
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