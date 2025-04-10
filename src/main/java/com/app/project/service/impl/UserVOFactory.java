package com.app.project.service.impl;

import com.app.project.common.ErrorCode;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.student.StudentEditRequest;
import com.app.project.model.entity.Department;
import com.app.project.model.entity.Student;
import com.app.project.model.entity.Teacher;
import com.app.project.model.enums.UserRoleEnum;
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
        if(UserRoleEnum.STUDENT.getValue().equals(role)){
            return getStudentDetails(userId);
        }
        if(UserRoleEnum.TEACHER.getValue().equals(role)){
            return getTeacherDetails(userId);
        }
        if(UserRoleEnum.ADMIN.getValue().equals(role)){
            return getTeacherDetails(userId);
        }
        throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
    }



    private StudentVO getStudentDetails(Long userId) {
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

    private Boolean editStudent(StudentEditRequest studentEditRequest){
        long id = studentEditRequest.getId();
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Student oldStudent = studentService.getById(id);
        ThrowUtils.throwIf(oldStudent == null, ErrorCode.NOT_FOUND_ERROR,"用户不存在");
        final Student student = new Student();
        BeanUtils.copyProperties(studentEditRequest, student);
          boolean update = studentService.updateById(student);
          ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR);
          return true;
    }

}
