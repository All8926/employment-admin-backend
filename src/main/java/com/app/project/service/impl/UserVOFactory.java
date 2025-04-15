package com.app.project.service.impl;

import com.app.project.common.ErrorCode;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.entity.Department;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.entity.Student;
import com.app.project.model.entity.Teacher;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.EnterpriseVO;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.TeacherVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.EnterpriseService;
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
    private EnterpriseService enterpriseService;

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
        if(UserRoleEnum.ENTERPRISE.getValue().equals(role)){
            return getEnterpriseDetails(userId);
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
        studentVO = StudentVO.objToVo(student);
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

    private EnterpriseVO getEnterpriseDetails(Long userId) {
        // 从企业表中查询企业信息并返回 EnterpriseVO
        Enterprise enterprise = enterpriseService.getById(userId);
        ThrowUtils.throwIf(enterprise == null, ErrorCode.NOT_FOUND_ERROR);
        EnterpriseVO enterpriseVO = new EnterpriseVO();
        BeanUtils.copyProperties(enterprise, enterpriseVO);
        return  enterpriseVO;
    }


}
