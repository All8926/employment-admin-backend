package com.app.project.model.dto.teacher;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 创建教师信息请求
 *
 * @author
 * @from
 */
@Data
public class TeacherAddRequest implements Serializable {

    /**
     * 账号
     */
    @NotBlank
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank
    private String userPassword;

    /**
     * 姓名
     */
    @NotBlank
    private String userName;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 性别 0-男 1-女 2-未知
     */
    private Integer gender;

    /**
     * 编号
     */
    private String teacherNumber;

    /**
     * 职务
     */
    private String job;

    /**
     * 学历
     */
    private String qualification;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 所属部门ID
     */
    private Long deptId;


    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：admin/student/teacher/enterprise
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}