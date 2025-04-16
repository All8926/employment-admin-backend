package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.TeacherMapper;
import com.app.project.model.dto.teacher.TeacherAddRequest;
import com.app.project.model.dto.teacher.TeacherQueryRequest;
import com.app.project.model.entity.Department;
import com.app.project.model.entity.Teacher;
import com.app.project.model.enums.RegisterStatusEnum;
import com.app.project.model.vo.TeacherVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.TeacherService;
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
* @description 针对表【teacher(教师)】的数据库操作Service实现
* @createDate 2025-04-08 19:01:36
*/
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher>
    implements TeacherService {


    @Resource
    private DepartmentService departmentService;

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xxx";


    @Override
    public Boolean registerTeacher(TeacherAddRequest teacherAddRequest) {
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherAddRequest, teacher);
        String userAccount = teacher.getUserAccount();

        // 单机锁，账号不能重复
        synchronized (userAccount.intern()) {
            // 1. 校验账号是否存在
            QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
            teacherQueryWrapper.eq("userAccount", userAccount);
            Teacher teacherOne = this.getOne(teacherQueryWrapper);

            // 检测编号是否已注册
            teacherQueryWrapper = new QueryWrapper<>();
            teacherQueryWrapper.eq("teacherNumber", teacher.getTeacherNumber());
            teacherQueryWrapper.eq("status",RegisterStatusEnum.RESOLVED.getValue());
            Teacher teacherByNumber = this.getOne(teacherQueryWrapper);
            ThrowUtils.throwIf(teacherByNumber != null, ErrorCode.PARAMS_ERROR, "编号已存在");

            // 2. 存在 -> 校验账号状态
            if (teacherOne != null) {
                int studentOneStatus = teacherOne.getStatus();
//                if (studentOneStatus == RegisterStatusEnum.REJECTED.getValue()) {
//                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被拒绝");
//                }
                if (studentOneStatus == RegisterStatusEnum.PENDING.getValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号正在审核中");
                }
                if (studentOneStatus == RegisterStatusEnum.RESOLVED.getValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
                }


            }

            // 3.加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + teacher.getUserPassword()).getBytes());
            teacher.setUserPassword(encryptPassword);

            // 4.操作数据库
            boolean result;
            // 账号已存在则更新数据，否则就插入新数据
            if(teacherOne != null){
                teacher.setStatus(RegisterStatusEnum.PENDING.getValue());
                teacher.setId(teacherOne.getId());
                result = this.updateById(teacher);
            }else{
                result = this.save(teacher);
            }
            if (!result) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return true;
        }
    }
    /**
     * 获取查询条件
     *
     * @param teacherQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Teacher> getQueryWrapper(TeacherQueryRequest teacherQueryRequest) {
        QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
        if (teacherQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = teacherQueryRequest.getId();
        String userName = teacherQueryRequest.getUserName();
        String teacherNumber = teacherQueryRequest.getTeacherNumber();
        String email = teacherQueryRequest.getEmail();
        String phone = teacherQueryRequest.getPhone();
        Integer gender = teacherQueryRequest.getGender();
        Integer status = teacherQueryRequest.getStatus();
        String userAccount = teacherQueryRequest.getUserAccount();
        String sortOrder = teacherQueryRequest.getSortOrder();
        String sortField = teacherQueryRequest.getSortField();
//        Long deptId = teacherQueryRequest.getDeptId();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(teacherNumber), "teacherNumber", teacherNumber);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
//        queryWrapper.eq(ObjectUtils.isNotEmpty(deptId), "deptId", deptId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(gender), "gender", gender);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);

        // 排除超级管理员
        queryWrapper.ne("userAccount", "admin");

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    /**
     * 分页获取教师信息封装
     *
     * @param teacherPage
     * @param request
     * @return
     */
    @Override
    public Page<TeacherVO> getTeacherVOPage(Page<Teacher> teacherPage, HttpServletRequest request) {
        List<Teacher> teacherList = teacherPage.getRecords();
        Page<TeacherVO> teacherVOPage = new Page<>(teacherPage.getCurrent(), teacherPage.getSize(), teacherPage.getTotal());
        if (CollUtil.isEmpty(teacherList)) {
            return teacherVOPage;
        }
        // Teacher => TeacherVO
        List<TeacherVO> teacherVOList = teacherList.stream().map(teacher -> {
            TeacherVO teacherVO = new TeacherVO();
            BeanUtils.copyProperties(teacher, teacherVO);
            return teacherVO;
        }).collect(Collectors.toList());

        // 1.取出所有部门id
        final Set<Long> deptIds = teacherList.stream().map(Teacher::getDeptId).collect(Collectors.toSet());
        // 2.批量查询部门信息 返回 deptId -> Department
        Map<Long, List<Department>> deptIdDepartmentListMap = departmentService.listByIds(deptIds).stream().collect(Collectors.groupingBy(Department::getId));
        // 3.填充部门信息
        teacherVOList.forEach(teacherVO -> {
            List<Department> departments = deptIdDepartmentListMap.get(teacherVO.getDeptId());
            if (CollUtil.isNotEmpty(departments) && departments.size() != 0) {
                teacherVO.setDeptName(departments.get(0).getName());
            }
        });

        teacherVOPage.setRecords(teacherVOList);
        return teacherVOPage;
    }
}




