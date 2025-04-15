package com.app.project.model.dto.user;

import com.app.project.common.AuditRequest;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 用户审核请求
 *
 * @author
 * @from
 */
@Data
public class UserAuditRequest extends AuditRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户角色
     */
    @NotBlank(message = "用户角色不能为空")
    private String userRole;

    /**
     * 用户账号
     */
    @NotBlank(message = "用户账号不能为空")
    private String userAccount;
}
