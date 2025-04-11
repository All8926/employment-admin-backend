package com.app.project.model.dto.enterprise;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * 更新学生信息请求
 *
 */
@Data
public class EnterpriseUpdateRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


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
    private String enterpriseName;

    /**
     * 职位
     */
    private String job;


    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    /**
     * 用户简介
     */
    @Size(max = 500, message = "简介不能超过500个字符")
    private String userProfile;

    /**
     * 用户角色：admin/student/teacher/enterprise
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}