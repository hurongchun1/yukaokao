package com.hrc.yukaokao.model.dto.questionAnswer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @FileName: QuestionAnswerDTO
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/20 11:34
 * @Version: 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerDTO {
    /**
     * 题目标题
     */
    private String title;
    /**
     * 用户答案
     */
    private String userAnswer;
}
