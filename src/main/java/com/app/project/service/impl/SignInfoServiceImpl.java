package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.SignInfoMapper;
import com.app.project.model.dto.signInfo.SignInfoAddRequest;
import com.app.project.model.dto.signInfo.SignInfoEditRequest;
import com.app.project.model.dto.signInfo.SignInfoQueryRequest;
import com.app.project.model.entity.Contract;
import com.app.project.model.entity.SignInfo;
import com.app.project.model.entity.Student;
import com.app.project.model.enums.AuditResultEnum;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.ContractVO;
import com.app.project.model.vo.SignInfoVO;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.ContractService;
import com.app.project.service.DepartmentService;
import com.app.project.service.SignInfoService;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【sign_info(签约信息表)】的数据库操作Service实现
 * @createDate 2025-04-23 09:25:28
 */
@Service
public class SignInfoServiceImpl extends ServiceImpl<SignInfoMapper, SignInfo>
        implements SignInfoService {


    @Resource
    private DepartmentService departmentService;

    @Resource
    private StudentService studentService;

    @Resource
    private ContractService contractService;

    @Override
    public boolean addSignInfo(SignInfoAddRequest signInfoAddRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        long userId = loginUser.getId();
        // 合同是否存在且本人
        long contractId = signInfoAddRequest.getContractId();
        Contract contract = contractService.getById(contractId);
        ThrowUtils.throwIf(contract == null, ErrorCode.NOT_FOUND_ERROR, "合同不存在");
        ThrowUtils.throwIf(userId != contract.getStudentId(), ErrorCode.NO_AUTH_ERROR, "合同非本人");

        // 合同是否已有签约信息
        QueryWrapper<SignInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contractId", contractId);
        long count = this.count(queryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.OPERATION_ERROR, "合同已有签约信息");

        // 如果学生就业状态为未就业则改变
        Student student = studentService.getById(contract.getStudentId());
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        if (Objects.equals(student.getIsEmployed(), AuditResultEnum.REJECTED.getValue())) {
            student.setIsEmployed(1);
            studentService.updateById(student);
        }

        SignInfo signInfo = new SignInfo();
        BeanUtils.copyProperties(signInfoAddRequest, signInfo);
        // 设置学生id
        signInfo.setStudentId(userId);
        boolean result = this.save(signInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean editSignInfo(SignInfoEditRequest signInfoEditRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 只能修改自己的签约信息
        long userId = loginUser.getId();
        long signInfoId = signInfoEditRequest.getId();
        SignInfo oldSignInfo = this.getById(signInfoId);
        ThrowUtils.throwIf(oldSignInfo == null, ErrorCode.NOT_FOUND_ERROR, "签约信息不存在");
        ThrowUtils.throwIf(userId != oldSignInfo.getStudentId(), ErrorCode.NO_AUTH_ERROR, "无权限");

        SignInfo signInfo = new SignInfo();
        BeanUtils.copyProperties(signInfoEditRequest, signInfo);
        boolean result = this.updateById(signInfo);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean removeSignInfoById(DeleteRequest deleteRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 只能修改自己的签约信息
        long userId = loginUser.getId();
        long signInfoId = deleteRequest.getId();
        SignInfo oldSignInfo = this.getById(signInfoId);
        ThrowUtils.throwIf(oldSignInfo == null, ErrorCode.NOT_FOUND_ERROR, "签约信息不存在");
        ThrowUtils.throwIf(userId != oldSignInfo.getStudentId(), ErrorCode.NO_AUTH_ERROR);

        // 如果学生就业状态为已就业并且删除的是最后一条，则改变就业状态
        Student student = studentService.getById(oldSignInfo.getStudentId());
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR, "学生不存在");
        // 查询已有签约信息条数
        QueryWrapper<SignInfo> signInfoQueryWrapper = new QueryWrapper<>();
        signInfoQueryWrapper.eq("studentId", student.getId());
        long count = this.count(signInfoQueryWrapper);

        if (Objects.equals(student.getIsEmployed(), AuditResultEnum.RESOLVED.getValue()) && count <= 1) {
            student.setIsEmployed(0);
            studentService.updateById(student);
        }

        // 删除签约信息
        boolean result = this.removeById(signInfoId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    /**
     * 获取查询条件
     *
     * @param signInfoQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<SignInfo> getQueryWrapper(SignInfoQueryRequest signInfoQueryRequest, UserVO loginUser) {
        QueryWrapper<SignInfo> queryWrapper = new QueryWrapper<>();
        if (signInfoQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = signInfoQueryRequest.getId();
        String post = signInfoQueryRequest.getPost();
        String salary = signInfoQueryRequest.getSalary();
        String studentName = signInfoQueryRequest.getStudentName();
        String sortOrder = signInfoQueryRequest.getSortOrder();
        String sortField = signInfoQueryRequest.getSortField();

        // 根据学生姓名模糊查询
        if (StringUtils.isNotBlank(studentName)) {
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.like("userName", studentName);
            Set<Long> studentIds = studentService.list(studentQueryWrapper).stream().map(Student::getId).collect(Collectors.toSet());
            if (studentIds.size() > 0) {
                queryWrapper.in("studentId", studentIds);
            } else {
                queryWrapper.eq("studentId", -1);
            }
        }

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(post), "post", post);
        queryWrapper.like(StringUtils.isNotBlank(salary), "salary", salary);


        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);

        String userRole = loginUser.getUserRole();
        long userId = loginUser.getId();

        // 学生只能查询自己的
        if (UserRoleEnum.STUDENT.getValue().equals(userRole)) {
            queryWrapper.eq("studentId", userId);
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }


    @Override
    public Page<SignInfoVO> getSignInfoVOPage(Page<SignInfo> signInfoPage, HttpServletRequest request) {
        List<SignInfo> signInfoList = signInfoPage.getRecords();
        Page<SignInfoVO> signInfoVOPage = new Page<>(signInfoPage.getCurrent(), signInfoPage.getSize(), signInfoPage.getTotal());

        if (CollUtil.isEmpty(signInfoList)) {
            return signInfoVOPage;
        }

        // 1.取出所有学生id和合同id
        Set<Long> studentIds = signInfoList.stream().map(SignInfo::getStudentId).collect(Collectors.toSet());
        Set<Long> contractIds = signInfoList.stream().map(SignInfo::getContractId).collect(Collectors.toSet());

        // 2.转成map映射
        Map<Long, Contract> contractMap = Optional.ofNullable(contractService.listByIds(contractIds)).orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap(Contract::getId, Function.identity()));

        Map<Long, Student> studentMap = Optional.ofNullable(studentService.listByIds(studentIds)).orElse(Collections.emptyList())
                .stream().collect(Collectors.toMap(Student::getId, Function.identity()));

        // 3.填充信息
        List<SignInfoVO> signInfoVOList = signInfoList.stream().map(signInfo -> {
            SignInfoVO vo = new SignInfoVO();
            BeanUtils.copyProperties(signInfo, vo);

            Contract contract = contractMap.get(signInfo.getContractId());
            if (contract != null) {
                ContractVO contractVO = new ContractVO();
                BeanUtils.copyProperties(contract, contractVO);
                vo.setContract(contractVO);
            }

            Student student = studentMap.get(signInfo.getStudentId());
            if (student != null) {
                StudentVO studentVO = new StudentVO();
                BeanUtils.copyProperties(student, studentVO);
                vo.setStudent(studentVO);
            }

            return vo;
        }).collect(Collectors.toList());

        signInfoVOPage.setRecords(signInfoVOList);
        return signInfoVOPage;
    }

}




