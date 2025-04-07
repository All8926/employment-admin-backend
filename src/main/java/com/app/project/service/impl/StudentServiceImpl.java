package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.StudentMapper;
import com.app.project.model.dto.student.StudentAddRequest;
import com.app.project.model.dto.student.StudentQueryRequest;
import com.app.project.model.entity.Student;
import com.app.project.model.entity.User;
import com.app.project.model.enums.RegisterStatusEnum;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.StudentService;

import com.app.project.service.UserService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
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
    private UserService userService;

    /**
     * 校验数据
     *
     * @param student
     * @param add     对创建的数据进行校验
     */
    @Override
    public void validStudent(Student student, boolean add) {

    }

    /**
     * 获取查询条件
     *
     * @param studentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Student> getQueryWrapper(StudentQueryRequest studentQueryRequest) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (studentQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = studentQueryRequest.getId();
        String userName = studentQueryRequest.getUserName();
        String student_number = studentQueryRequest.getStudent_number();
        String email = studentQueryRequest.getEmail();
        String phone = studentQueryRequest.getPhone();
        Integer gender = studentQueryRequest.getGender();
        Integer status = studentQueryRequest.getStatus();
        String userAccount = studentQueryRequest.getUserAccount();
        String sortOrder = studentQueryRequest.getSortOrder();
        String sortField = studentQueryRequest.getSortField();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(student_number), "student_number", student_number);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(gender), "gender", gender);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
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
        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除

        // 1. 关联查询学院信息


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

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
//        Set<Long> userIdSet = studentList.stream().map(Student::getUserId).collect(Collectors.toSet());
//        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
//                .collect(Collectors.groupingBy(User::getId));
//
//        // 填充信息
//        studentVOList.forEach(studentVO -> {
//            Long userId = studentVO.getUserId();
//            User user = null;
//            if (userIdUserListMap.containsKey(userId)) {
//                user = userIdUserListMap.get(userId).get(0);
//            }
//            studentVO.setUser(userService.getUserVO(user));
//            studentVO.setHasThumb(studentIdHasThumbMap.getOrDefault(studentVO.getId(), false));
//            studentVO.setHasFavour(studentIdHasFavourMap.getOrDefault(studentVO.getId(), false));
//        });
        // endregion

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




