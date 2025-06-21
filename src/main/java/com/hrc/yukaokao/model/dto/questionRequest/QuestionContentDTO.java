package com.hrc.yukaokao.model.dto.questionRequest;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDTO {
    /**
     * 题目选项列表
     */
    private List<Option> options;
    /**
     * 题目标题
     */
    private String title;

    /**
     * 题目选项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Option {
        //结果
        private String result;
        //分数
        private int score;
        //答案
        private String value;
        //题目的选项
        private String key;
    }
}
