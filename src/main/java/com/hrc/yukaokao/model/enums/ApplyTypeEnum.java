package com.hrc.yukaokao.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * App 应用类型枚举
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public enum ApplyTypeEnum {

    // 得分类
    SCORE("得分类", 0),
    // 测评类
    TEST("测评类", 1);

    // 枚举的文本
    private final String text;

    // 枚举的值
    private final int value;

    // 构造方法
    ApplyTypeEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        // 将枚举的值转换为列表
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ApplyTypeEnum getEnumByValue(int value) {
        // 如果值为空，返回 null
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        // 遍历枚举，找到对应的值
        for (ApplyTypeEnum anEnum : ApplyTypeEnum.values()) {
            if (anEnum.value == value) {
                return anEnum;
            }
        }
        // 如果没有找到对应的值，返回 null
        return null;
    }

    // 获取枚举的值
    public Integer getValue() {
        return value;
    }

    // 获取枚举的文本
    public String getText() {
        return text;
    }
}
