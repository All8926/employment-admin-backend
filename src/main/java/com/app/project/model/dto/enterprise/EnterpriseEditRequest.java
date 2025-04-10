package com.app.project.model.dto.enterprise;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 编辑学生信息请求
 *
 * @author
 * @from
 */
@Data
public class EnterpriseEditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 性别 0-男 1-女 2-未知
     */
    private Integer gender;

    /**
     * 企业名称
     */
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


    private static final long serialVersionUID = 1L;
}