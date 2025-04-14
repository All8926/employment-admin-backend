package com.app.project.model.dto.teacher;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 查询教师信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TeacherQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    @NotNull
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
//    private Long deptId;

    /**
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;



    private static final long serialVersionUID = 1L;
}