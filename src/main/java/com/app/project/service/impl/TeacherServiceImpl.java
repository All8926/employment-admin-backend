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
import com.app.project.model.entity.Teacher;
import com.app.project.model.entity.User;
import com.app.project.model.vo.TeacherVO;
import com.app.project.model.vo.TeacherVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.StudentService;
import com.app.project.service.TeacherService;

import com.app.project.service.UserService;
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
import java.util.*;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【teacher(教师)】的数据库操作Service实现
* @createDate 2025-04-08 19:01:36
*/
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher>
    implements TeacherService {

//    @Resource
//    private UserService userService;

    @Resource
    private DepartmentService departmentService;

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xxx";

    /**
     * 校验数据
     *
     * @param teacher
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validTeacher(Teacher teacher, boolean add) {
    
    }

    @Override
    public Boolean addTeacher(TeacherAddRequest teacherAddRequest) {
        String userAccount = teacherAddRequest.getUserAccount();
        String userPassword = teacherAddRequest.getUserPassword();
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<Teacher> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            Teacher teacher = new Teacher();
            BeanUtils.copyProperties(teacherAddRequest, teacher);
            teacher.setUserPassword(encryptPassword);
            boolean saveResult = this.save(teacher);
            if (!saveResult) {
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
        Long deptId = teacherQueryRequest.getDeptId();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(teacherNumber), "teacherNumber", teacherNumber);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(deptId), "deptId", deptId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(gender), "gender", gender);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取教师信息封装
     *
     * @param teacher
     * @param request
     * @return
     */
    @Override
    public TeacherVO getTeacherVO(Teacher teacher, HttpServletRequest request) {
        // 对象转封装类
        TeacherVO teacherVO = new TeacherVO();
        BeanUtils.copyProperties(teacher, teacherVO);

        // 1. 关联查询部门信息
        long deptId = teacher.getDeptId();
        if(deptId > 0){
            Department dept = departmentService.getById(deptId);
            if(dept !=null){
                teacherVO.setDeptName(dept.getName());
            }
        }

        // endregion

        return teacherVO;
    }

    /**
     * 递归获取所有下级部门 ID
     * @param parentId
     * @param allDepartments
     * @param result
     */
    private void collectSubDepartmentIds(Long parentId, List<Department> allDepartments, Set<Long> result) {
        for (Department dept : allDepartments) {
            if (parentId.equals(dept.getParentId())) {
                result.add(dept.getId());
                collectSubDepartmentIds(dept.getId(), allDepartments, result); // 递归找下级
            }
        }
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




