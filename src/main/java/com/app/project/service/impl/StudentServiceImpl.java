package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.mapper.StudentMapper;
import com.app.project.model.dto.student.StudentAddRequest;
import com.app.project.model.dto.student.StudentQueryRequest;
import com.app.project.model.entity.Department;
import com.app.project.model.entity.Student;
import com.app.project.model.enums.RegisterStatusEnum;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.StudentService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【student(用户)】的数据库操作Service实现
 * @createDate 2025-04-07 21:07:34
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student>
        implements StudentService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xxx";


    @Resource
    private DepartmentService departmentService;


    /**
     * 获取查询条件
     *
     * @param studentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Student> getQueryWrapper(StudentQueryRequest studentQueryRequest, UserVO loginUser) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (studentQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = studentQueryRequest.getId();
        String userName = studentQueryRequest.getUserName();
        String studentNumber = studentQueryRequest.getStudentNumber();
        String email = studentQueryRequest.getEmail();
        String phone = studentQueryRequest.getPhone();
        Integer gender = studentQueryRequest.getGender();
        Integer status = studentQueryRequest.getStatus();
        String userAccount = studentQueryRequest.getUserAccount();
        String sortOrder = studentQueryRequest.getSortOrder();
        String sortField = studentQueryRequest.getSortField();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(studentNumber), "studentNumber", studentNumber);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
//        queryWrapper.eq(ObjectUtils.isNotEmpty(deptId), "deptId", deptId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(gender), "gender", gender);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);

        // 转成 studentVO 来取deptId
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(loginUser, studentVO);

        String userRole = loginUser.getUserRole();
        long deptId = studentVO.getDeptId();

        // 非管理员只能查询本部门的学生
        if (!UserRoleEnum.ADMIN.getValue().equals(userRole)) {
            // 1. 获取所有下级部门 ID
            Set<Long> deptIds = departmentService.getAllChildDepartmentIds(deptId);
            // 2. 包含当前部门本身
            deptIds.add(deptId);
            // 3. 添加条件
            queryWrapper.in("deptId", deptIds);
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public List<StudentVO> listStudentsByDepartment(Long departmentId) {
        // 1. 查询所有部门
        List<Department> allDepartments = departmentService.list();

        // 2. 递归获取所有下级部门 ID
        Set<Long> deptIds = departmentService.getAllChildDepartmentIds(departmentId);
//        collectSubDepartmentIds(departmentId, allDepartments, deptIds);

        // 包含当前部门本身
        deptIds.add(departmentId);

        // 3. 查询学生表
        QueryWrapper<Student> queryWrapper = new QueryWrapper<Student>();
        queryWrapper.in("deptId", deptIds);
        List<Student> studentList = this.list(queryWrapper);
        return studentList.stream().map(student -> {
            StudentVO studentVO = new StudentVO();
            BeanUtils.copyProperties(student, studentVO);
            return studentVO;
        }).collect(Collectors.toList());
    }


    /**
     * 获取学生信息封装
     *
     * @param student
     * @param request
     * @return
     */
    @Override
    public StudentVO getStudentVO(Student student, HttpServletRequest request) {
        // 对象转封装类
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(student, studentVO);

        // 1. 关联查询部门信息
        long deptId = student.getDeptId();
        if (deptId > 0) {
            Department dept = departmentService.getById(deptId);
            if (dept != null) {
                studentVO.setDeptName(dept.getName());
            }
        }

        // endregion

        return studentVO;
    }

    /**
     * 分页获取学生信息封装
     *
     * @param studentPage
     * @param request
     * @return
     */
    @Override
    public Page<StudentVO> getStudentVOPage(Page<Student> studentPage, HttpServletRequest request) {
        List<Student> studentList = studentPage.getRecords();
        Page<StudentVO> studentVOPage = new Page<>(studentPage.getCurrent(), studentPage.getSize(), studentPage.getTotal());
        if (CollUtil.isEmpty(studentList)) {
            return studentVOPage;
        }
        // Student => StudentVO
        List<StudentVO> studentVOList = studentList.stream().map(student -> {
            StudentVO studentVO = new StudentVO();
            BeanUtils.copyProperties(student, studentVO);
            return studentVO;
        }).collect(Collectors.toList());

        // 1.取出所有部门id
        final Set<Long> deptIds = studentList.stream().map(Student::getDeptId).collect(Collectors.toSet());
        // 2.批量查询部门信息 返回 deptId -> Department
        Map<Long, List<Department>> deptIdDepartmentListMap = departmentService.listByIds(deptIds).stream().collect(Collectors.groupingBy(Department::getId));
        // 3.填充部门信息
        studentVOList.forEach(studentVO -> {
            List<Department> departments = deptIdDepartmentListMap.get(studentVO.getDeptId());
            if (CollUtil.isNotEmpty(departments) && departments.size() != 0) {
                studentVO.setDeptName(departments.get(0).getName());
            }
        });

        studentVOPage.setRecords(studentVOList);
        return studentVOPage;
    }

    @Override
    public boolean register(StudentAddRequest studentAddRequest) {
        Student student = new Student();
        BeanUtils.copyProperties(studentAddRequest, student);
        String userAccount = student.getUserAccount();

        // 单机锁，账号不能重复
        synchronized (userAccount.intern()) {
            // 1. 校验账号是否存在
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("userAccount", userAccount);
            Student studentOne = this.getOne(studentQueryWrapper);

            // 2. 存在 -> 校验账号状态
            if (studentOne != null) {
                int studentOneStatus = studentOne.getStatus();
                if (studentOneStatus == RegisterStatusEnum.REJECTED.getValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被拒绝");
                }
                if (studentOneStatus == RegisterStatusEnum.PENDING.getValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号正在审核中");
                }
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
            }

            // 3.加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + student.getUserPassword()).getBytes());
            student.setUserPassword(encryptPassword);
            boolean saveResult = this.save(student);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return true;
        }

    }

}




