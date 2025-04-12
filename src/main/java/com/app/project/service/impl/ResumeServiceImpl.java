package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.ResumeMapper;
import com.app.project.model.dto.resume.ResumeAddRequest;
import com.app.project.model.dto.resume.ResumeEditRequest;
import com.app.project.model.dto.resume.ResumeQueryRequest;
import com.app.project.model.entity.Resume;
import com.app.project.model.entity.Student;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.ResumeVO;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.ResumeService;
import com.app.project.service.StudentService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【resume(简历)】的数据库操作Service实现
 * @createDate 2025-04-11 20:09:44
 */
@Service
public class ResumeServiceImpl extends ServiceImpl<ResumeMapper, Resume>
        implements ResumeService {

    @Resource
    private DepartmentService departmentService;

    @Resource
    private StudentService studentService;

    @Override
    public boolean addResume(ResumeAddRequest resumeAddRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long userId = loginUser.getId();
        Resume resume = new Resume();
        BeanUtils.copyProperties(resumeAddRequest, resume);
        // 设置创建人用户id
        resume.setUserId(userId);
        boolean result = this.save(resume);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean editResume(ResumeEditRequest resumeEditRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 只能修改自己的简历
        long userId = loginUser.getId();
        long resumeId = resumeEditRequest.getId();
        Resume oldResume = this.getById(resumeId);
        ThrowUtils.throwIf(oldResume == null, ErrorCode.NOT_FOUND_ERROR, "简历不存在");
        ThrowUtils.throwIf(userId != oldResume.getUserId(), ErrorCode.NO_AUTH_ERROR, "无权限");


        Resume resume = new Resume();
        BeanUtils.copyProperties(resumeEditRequest, resume);
        boolean result = this.updateById(resume);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean removeResumeById(DeleteRequest deleteRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 只能修改自己的简历
        long userId = loginUser.getId();
        long resumeId = deleteRequest.getId();
        Resume oldResume = this.getById(resumeId);
        ThrowUtils.throwIf(oldResume == null, ErrorCode.NOT_FOUND_ERROR, "简历不存在");
        ThrowUtils.throwIf(userId != oldResume.getUserId(), ErrorCode.NO_AUTH_ERROR);

        boolean result = this.removeById(resumeId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 获取查询条件
     *
     * @param resumeQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Resume> getQueryWrapper(ResumeQueryRequest resumeQueryRequest, UserVO loginUser) {
        QueryWrapper<Resume> queryWrapper = new QueryWrapper<>();
        if (resumeQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = resumeQueryRequest.getId();
        String fileName = resumeQueryRequest.getFileName();
        String remark = resumeQueryRequest.getRemark();
        Integer isActive = resumeQueryRequest.getIsActive();
        String sortOrder = resumeQueryRequest.getSortOrder();
        String sortField = resumeQueryRequest.getSortField();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(fileName), "fileName", fileName);
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isActive), "isActive", isActive);


        String userRole = loginUser.getUserRole();
        long userId = loginUser.getId();


        // 学生只能查询自己的
        if (UserRoleEnum.STUDENT.getValue().equals(userRole)) {
            queryWrapper.eq("userId", userId);
        }

        // 转成 studentVO 来取deptId
        StudentVO studentVO = new StudentVO();
        BeanUtils.copyProperties(loginUser, studentVO);
        long deptId = studentVO.getDeptId();

        // 老师只能查询本部门的学生
        if (UserRoleEnum.TEACHER.getValue().equals(userRole)) {
            // 1. 获取所有下级部门 ID
            Set<Long> deptIds = departmentService.getAllChildDepartmentIds(deptId);
            // 2. 包含当前部门本身
            deptIds.add(deptId);
            // 3. 查询该部门下的学生id
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<Student>().in("deptId", deptIds);
            List<Long> studentIds = studentService.list(studentQueryWrapper)
                    .stream()
                    .map(Student::getId)
                    .collect(Collectors.toList());
            // 4. 添加查询条件
            if (!studentIds.isEmpty()) {
                queryWrapper.in("userId", studentIds);
            } else {
                // 无学生，直接保证查不到数据
                queryWrapper.eq("userId", -1);
            }
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public Page<ResumeVO> getResumeVOPage(Page<Resume> resumePage, HttpServletRequest request) {
        List<Resume> resumeList = resumePage.getRecords();
        Page<ResumeVO> resumeVOPage = new Page<>(resumePage.getCurrent(), resumePage.getSize(), resumePage.getTotal());
        if (CollUtil.isEmpty(resumeList)) {
            return resumeVOPage;
        }
        // Resume => ResumeVO
        List<ResumeVO> resumeVOList = resumeList.stream().map(resume -> {
            ResumeVO resumeVO = new ResumeVO();
            BeanUtils.copyProperties(resume, resumeVO);
            return resumeVO;
        }).collect(Collectors.toList());

        // 1.取出所有学生id
        final Set<Long> userIds = resumeList.stream().map(Resume::getUserId).collect(Collectors.toSet());
        // 2.批量查询学生信息 返回 studentId -> Student
        Map<Long, List<Student>> userIdDepartmentListMap = studentService.listByIds(userIds).stream().collect(Collectors.groupingBy(Student::getId));
        // 3.填充学生信息
        resumeVOList.forEach(resumeVO -> {
            List<Student> students = userIdDepartmentListMap.get(resumeVO.getUserId());
            if (CollUtil.isNotEmpty(students) && students.size() != 0) {
                resumeVO.setUserName(students.get(0).getUserName());
            }
        });

        resumeVOPage.setRecords(resumeVOList);
        return resumeVOPage;
    }


}




