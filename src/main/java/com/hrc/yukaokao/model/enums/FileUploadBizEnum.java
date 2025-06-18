package com.hrc.yukaokao.model.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ObjectUtils;

/**
 * 文件上传业务类型枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum FileUploadBizEnum {

    // 用户头像
    USER_AVATAR("用户头像", "user_avatar"),
    // 应用图标
    APP_ICON("应用图标", "app_icon"),
    //评分结果图片
    SCORING_RESULT_PICTURE("评分结果图片", "scoring_result_picture");


    private final String text;

    private final String value;

    // 构造方法
    FileUploadBizEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        // 使用 Java 8 Stream API 将枚举的 value 转换为 List
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static FileUploadBizEnum getEnumByValue(String value) {
        // 如果 value 为空，则返回 null
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        // 遍历枚举，找到与 value 相等的枚举
        for (FileUploadBizEnum anEnum : FileUploadBizEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        // 如果没有找到，则返回 null
        return null;
    }

    // 获取枚举的 value
    public String getValue() {
        return value;
    }

    // 获取枚举的 text
    public String getText() {
        return text;
    }
}
