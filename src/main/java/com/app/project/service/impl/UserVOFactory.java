package com.app.project.service.impl;

import com.app.project.common.ErrorCode;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.entity.Department;
import com.app.project.model.entity.Student;
import com.app.project.model.entity.Teacher;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.TeacherVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.StudentService;
import com.app.project.service.TeacherService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 工厂类，根据用户角色返回不同的 VO 对象
 */
@Component
public class UserVOFactory {

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private DepartmentService departmentService;

    public UserVO getUserDetails(Long userId, String role) {
        switch (role) {
            case "student":
                // 查询学生信息
                return getStudentDetails(userId);
            case "teacher":
                // 查询教师信息
                return getTeacherDetails(userId);
            case "admin":
                // 查询管理信息
                return getTeacherDetails(userId);
            default:
                throw new IllegalArgumentException("Unknown role: " + role);
        }
    }

    private   StudentVO getStudentDetails(Long userId) {
        // 从学生表中查询学生信息并返回 StudentVO
        Student student = studentService.getById(userId);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR);
        StudentVO studentVO = new StudentVO();
        Long deptId = student.getDeptId();
        if(deptId != null){
            Department department = departmentService.getById(deptId);
            if(department != null){
                studentVO.setDeptName(department.getName());
            }
        }
        BeanUtils.copyProperties(student, studentVO);
        return studentVO;
    }

    private TeacherVO getTeacherDetails(Long userId) {
        // 从教师表中查询教师信息并返回 TeacherVO
        // 示例：
        Teacher teacher = teacherService.getById(userId);
        ThrowUtils.throwIf(teacher == null, ErrorCode.NOT_FOUND_ERROR);
        TeacherVO teacherVO = new TeacherVO();
        Long deptId = teacher.getDeptId();
        if(deptId != null){
            Department department = departmentService.getById(deptId);
            if(department != null){
                teacherVO.setDeptName(department.getName());
            }
        }
        BeanUtils.copyProperties(teacher, teacherVO);
        return teacherVO;
    }

}
