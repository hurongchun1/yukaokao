package com.hrc.yukaokao.model.dto.aiRequest;

import lombok.Data;

import java.io.Serializable;

/**
 * @FileName: AiGenerateQuestionRequest
 * @Description:
 * @Author: hrc
 * @CreateTime: 2025/6/19 17:02
 * @Version: 1.0.0
 */
@Data
public class AiGenerateQuestionRequest implements Serializable {
    private static final long serialVersionUID = -8033616226568009993L;
    /**
     * 应用id
     */
    private Long appId;
    /**
     * 答题数量
     */
    private int questionNumber = 10;
    /**
     * 选项数量
     */
    private int optionNumber = 2;
}
