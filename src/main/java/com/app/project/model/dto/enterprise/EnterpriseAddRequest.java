package com.app.project.model.dto.enterprise;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 创建学生信息请求
 *
 * @author
 * @from
 */
@Data
public class EnterpriseAddRequest implements Serializable {


    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    @Size(max = 10, min = 4, message = "账号长度限制4-10位")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    @Size(max = 16, min = 4, message = "密码长度限制4-16位")
    private String userPassword;

    /**
     * 姓名
     */
    private String userName;


    /**
     * 性别 0-男 1-女 2-未知
     */
    private Integer gender;

    /**
     * 企业名称
     */
    @NotBlank(message = "企业名称不能为空")
    private String enterpriseName;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 用户简介
     */
    @Size(max = 500, message = "简介不能超过500个字符")
    private String userProfile;


    /**
     * 头像
     */
    private String userAvatar;


    /**
     * 职务
     */
    private String job;


    /**
     * 统一社会信用代码
     */
    @NotBlank(message = "统一社会信用代码不能为空")
    private String licenseNum;

    /**
     * 办公地址
     */
    @NotBlank(message = "办公地址不能为空")
    private String address;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 所属行业
     */
    private String industry;



    private static final long serialVersionUID = 1L;
}