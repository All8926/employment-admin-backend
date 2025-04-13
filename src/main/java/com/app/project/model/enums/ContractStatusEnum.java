package com.app.project.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 合同状态枚举
 *
 * @author 
 * @from 
 */
public enum ContractStatusEnum {

    STUDENT_PENDING("待学生审核", 0),
    TEACHER_PENDING("待老师审核", 1),
    RESOLVED("已完成", 2),
    STUDENT_REJECTED("学生拒绝", 3),
    TEACHER_REJECTED("老师拒绝", 4);

    private final String text;

    private final Integer value;

    ContractStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<Integer> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static ContractStatusEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (ContractStatusEnum anEnum : ContractStatusEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public Integer getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
