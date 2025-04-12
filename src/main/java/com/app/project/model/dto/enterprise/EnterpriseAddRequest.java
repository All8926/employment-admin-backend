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
    @NotBlank(message = "姓名不能为空")
    @Size(max = 20, min = 2, message = "姓名限制2-20个字符")
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
    @Size(max = 12, message = "手机号不能超过12位")
    private String phone;

    /**
     * 邮箱
     */
    @Size(max = 20, message = "邮箱长度不能超过20位")
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
    private String licenseNum;

    /**
     * 办公地址
     */
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