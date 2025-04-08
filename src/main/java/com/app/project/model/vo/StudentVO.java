package com.app.project.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 学生信息视图
 *
 * @author
 * @from
 */
@Data
public class StudentVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;


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
     * 学号
     */
    private String studentNumber;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 部门名字
     */
    private String deptName;

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

}
