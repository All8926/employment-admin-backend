package com.app.project.model.dto.student;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Student;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 编辑学生信息请求
 *
 * @author
 * @from
 */
@Data
public class StudentEditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;


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
     * 部门id
     */
    private Long deptId;

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
     * 毕业时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
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
     * 用户简介
     */
    @Size(max = 500, message = "简介不能超过500个字符")
    private String userProfile;


    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     *
     * @param studentEditRequest
     * @return
     */
    public static Student dtoToObj(StudentEditRequest studentEditRequest) {
        if (studentEditRequest == null) {
            return null;
        }
        Student student = new Student();
        BeanUtils.copyProperties(studentEditRequest, student);
        List<String> graduationGoes = studentEditRequest.getGraduationGoes();
        student.setGraduationGoes(JSONUtil.toJsonStr(graduationGoes));
        return student;
    }

    /**
     * 对象转包装类
     *
     * @param student
     * @return
     */
    public static StudentEditRequest objToDto(Student student) {
        if (student == null) {
            return null;
        }
        StudentEditRequest studentEditRequest = new StudentEditRequest();
        BeanUtils.copyProperties(student, studentEditRequest);
        studentEditRequest.setGraduationGoes(JSONUtil.toList(student.getGraduationGoes(), String.class));
        return studentEditRequest;
    }
}