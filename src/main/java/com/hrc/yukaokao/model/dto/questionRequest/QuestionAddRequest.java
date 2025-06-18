package com.hrc.yukaokao.model.dto.questionRequest;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 创建题目表请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionAddRequest implements Serializable {


    /**
     * 题目内容（json格式），对应可以多道题目的
     */
    private List<QuestionContentDTO> questioncontent;

    /**
     * 应用 id
     */
    private Long appid;


    private static final long serialVersionUID = 1L;
}
