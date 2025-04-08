package com.app.project.model.dto.teacher;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑教师信息请求
 *
 * @author
 * @from
 */
@Data
public class TeacherEditRequest implements Serializable {

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
     * 用户简介
     */
    private String userProfile;


    private static final long serialVersionUID = 1L;
}