package com.app.project.model.vo;

import lombok.Data;

/**
 * 教师信息视图
 *
 * @author
 * @from
 */
@Data
public class TeacherVO extends UserVO {
    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 部门名称
     */
    private String deptName;

    /**
     * 编号
     */
    private String teacherNumber;

    /**
     * 职位
     */
    private String job;

    /**
     * 学历
     */
    private String qualification;

}
