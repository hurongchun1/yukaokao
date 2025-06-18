package com.hrc.yukaokao.model.vo;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.hrc.yukaokao.model.dto.questionRequest.QuestionContentDTO;
import com.hrc.yukaokao.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目表视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Data
public class QuestionVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）
     */
    private List<QuestionContentDTO> questioncontent;

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
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象 对象转 Json字符串
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<QuestionContentDTO> questionContentDTO = questionVO.getQuestioncontent();
        question.setQuestioncontent(JSONUtil.toJsonStr(questionContentDTO));
        return question;
    }

    /**
     * 对象转封装类  Json字符串转成 对象
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        String questioncontent = question.getQuestioncontent();
        if(StrUtil.isNotBlank(questioncontent) ){
            questionVO.setQuestioncontent(JSONUtil.toList(questioncontent, QuestionContentDTO.class));
        }
        return questionVO;
    }
}
