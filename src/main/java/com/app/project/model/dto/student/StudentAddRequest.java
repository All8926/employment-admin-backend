package com.app.project.model.dto.student;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

/**
 * 创建学生信息请求
 *
 * @author
 * @from
 */
@Data
public class StudentAddRequest implements Serializable {


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
     * 学号
     */
    @NotBlank(message = "学号不能为空")
    @Size(max = 20, min = 6, message = "学号长度限制6-20位")
    private String student_number;

    /**
     * 学院id
     */
    @NotNull(message = "学院不能为空")
    private Long departmentId;

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