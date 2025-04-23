package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Student;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;

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
     * 毕业时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "Asia/Shanghai")
    private Date graduationDate;

    /**
     * 毕业去向
     */
    private List<String> graduationGoes;

    /**
     * 无去向原因
     */
    private String notGoesReason;

    /**
     * 是否就业 0-否 1-是
     */
    private Integer isEmployed;


    /**
     * 学号
     */
    private String studentNumber;


    /**
     * 包装类转对象
     *
     * @param studentVO
     * @return
     */
    public static Student voToObj(StudentVO studentVO) {
        if (studentVO == null) {
            return null;
        }
        Student student = new Student();
        BeanUtils.copyProperties(studentVO, student);
        List<String> graduationGoes = studentVO.getGraduationGoes();
        student.setGraduationGoes(JSONUtil.toJsonStr(graduationGoes));
        return student;
    }

    /**
     * 对象转包装类
     *
     * @param student
     * @return
     */
    public static StudentVO objToVo(Student student) {
        if (student == null) {
            return null;
        }
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(student, studentVO);
        studentVO.setGraduationGoes(JSONUtil.toList(student.getGraduationGoes(), String.class));
        return studentVO;
    }

}
