package com.app.project.model.dto.user;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用户登录请求
 *
 * @author 
 * @from 
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    private String userPassword;

    @ApiParam(value = "student-学生 teacher-老师 enterprise-企业")
    @NotBlank(message = "用户角色不能为空")
    private String userRole;
}
