package com.hrc.yukaokao.model.dto.scoringResultRequest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.hrc.yukaokao.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询分数结果表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ScoringResultQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
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
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}