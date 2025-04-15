package com.app.project.model.enums;

import org.apache.commons.lang3.ObjectUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 审核对象枚举
 *
 * @author 
 * @from 
 */
public enum AuditTargetTypeEnum {

    CERTIFICATION("企业资质", "certification"),
    CONTRACT("合同", "contract"),
    REGISTER("注册账号", "register");


    private final String text;

    private final String value;

    AuditTargetTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 获取值列表
     *
     * @return
     */
    public static List<String> getValues() {
        return Arrays.stream(values()).map(item -> item.value).collect(Collectors.toList());
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value
     * @return
     */
    public static AuditTargetTypeEnum getEnumByValue(String value) {
        if (ObjectUtils.isEmpty(value)) {
            return null;
        }
        for (AuditTargetTypeEnum anEnum : AuditTargetTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getValue() {
        return value;
    }

    public String getText() {
        return text;
    }
}
