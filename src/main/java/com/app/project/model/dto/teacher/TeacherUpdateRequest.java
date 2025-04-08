package com.app.project.model.dto.teacher;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 更新教师信息请求
 *
 */
@Data
public class TeacherUpdateRequest implements Serializable {

    /**
     * id
     */
    @NotNull
    private Long id;


    /**
     * 姓名
     */
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
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

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