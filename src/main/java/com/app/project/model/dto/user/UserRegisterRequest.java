package com.app.project.model.dto.user;

import java.io.Serializable;
import lombok.Data;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;

/**
 * 用户注册请求体
 *
 * @author 
 * @from 
 */
@Data
public class UserRegisterRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    @NotBlank(message = "账号不能为空")
    private String userAccount;

    @NotBlank(message = "密码不能为空")
    private String userPassword;

    @NotBlank(message = "确认密码不能为空")
    private String checkPassword;
}
