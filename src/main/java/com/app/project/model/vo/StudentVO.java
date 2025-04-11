package com.app.project.model.vo;

import lombok.Data;

/**
 * 学生信息视图
 *
 * @author
 * @from
 */
@Data
public class StudentVO extends UserVO {

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 学号
     */
    private String studentNumber;


}
