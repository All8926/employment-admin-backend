package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.AuditRequest;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.ContractMapper;
import com.app.project.model.dto.contract.ContractAddRequest;
import com.app.project.model.dto.contract.ContractEditRequest;
import com.app.project.model.dto.contract.ContractQueryRequest;
import com.app.project.model.entity.AuditLog;
import com.app.project.model.entity.Contract;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.entity.Student;
import com.app.project.model.enums.AuditResultEnum;
import com.app.project.model.enums.AuditTargetTypeEnum;
import com.app.project.model.enums.ContractStatusEnum;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.ContractVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.*;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Administrator
 * @description 针对表【contract(合同)】的数据库操作Service实现
 * @createDate 2025-04-12 20:34:09
 */
@Service
public class ContractServiceImpl extends ServiceImpl<ContractMapper, Contract>
        implements ContractService {

    @Resource
    private StudentService studentService;

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private AuditLogService auditLogService;

    @Override
    public boolean addContract(ContractAddRequest contractAddRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 1.查询学生是否存在
        String studentName = contractAddRequest.getStudentName();
        String studentAccount = contractAddRequest.getStudentAccount();
        QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
        studentQueryWrapper.eq("userName", studentName);
        studentQueryWrapper.eq("userAccount", studentAccount);
        Student student = studentService.getOne(studentQueryWrapper);
        ThrowUtils.throwIf(student == null, ErrorCode.PARAMS_ERROR, "学生不存在");

        Contract contract = new Contract();
        BeanUtils.copyProperties(contractAddRequest, contract);
        // 2.设置学生信息
        contract.setStudentId(student.getId());
        contract.setStudentName(studentName);

        // 3.设置企业信息
        Enterprise enterprise = enterpriseService.getById(loginUser.getId());
        ThrowUtils.throwIf(enterprise == null, ErrorCode.PARAMS_ERROR, "企业不存在");
        contract.setEnterpriseName(enterprise.getEnterpriseName());
        contract.setEnterpriseId(enterprise.getId());

        // 4.插入数据库
        boolean result = this.save(contract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean editContract(ContractEditRequest contractEditRequest, UserVO loginUser) {
        // 只能修改自己的简历
        long userId = loginUser.getId();
        long contractId = contractEditRequest.getId();
        Contract oldContract = this.getById(contractId);
        ThrowUtils.throwIf(oldContract == null, ErrorCode.NOT_FOUND_ERROR, "合同不存在");
        ThrowUtils.throwIf(userId != oldContract.getEnterpriseId(), ErrorCode.NO_AUTH_ERROR, "无权限");

        // 只能修改学生还未审核的合同
        Integer status = oldContract.getStatus();
        ThrowUtils.throwIf(!ContractStatusEnum.STUDENT_PENDING.getValue().equals(status), ErrorCode.NO_AUTH_ERROR, "合同已审核，不能修改");

        Contract contract = new Contract();
        BeanUtils.copyProperties(contractEditRequest, contract);
        boolean result = this.updateById(contract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean removeContractById(DeleteRequest deleteRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 只能修改自己的简历
        long userId = loginUser.getId();
        long contractId = deleteRequest.getId();
        Contract oldContract = this.getById(contractId);
        ThrowUtils.throwIf(oldContract == null, ErrorCode.NOT_FOUND_ERROR, "合同不存在");
        ThrowUtils.throwIf(userId != oldContract.getEnterpriseId(), ErrorCode.NO_AUTH_ERROR);

        boolean result = this.removeById(contractId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public QueryWrapper<Contract> getQueryWrapper(ContractQueryRequest contractQueryRequest, UserVO loginUser) {
        QueryWrapper<Contract> queryWrapper = new QueryWrapper<>();
        if (contractQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = contractQueryRequest.getId();
        String fileName = contractQueryRequest.getFileName();
        String enterpriseName = contractQueryRequest.getEnterpriseName();
        String studentName = contractQueryRequest.getStudentName();
        String teacherName = contractQueryRequest.getTeacherName();
        Integer status = contractQueryRequest.getStatus();
        Date signDate = contractQueryRequest.getSignDate();
        String sortOrder = contractQueryRequest.getSortOrder();
        String sortField = contractQueryRequest.getSortField();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(fileName), "fileName", fileName);
        queryWrapper.like(StringUtils.isNotBlank(enterpriseName), "enterpriseName", enterpriseName);
        queryWrapper.like(StringUtils.isNotBlank(studentName), "studentName", studentName);
        queryWrapper.like(StringUtils.isNotBlank(teacherName), "teacherName", teacherName);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);

        HashSet<Integer> statusSet = new HashSet<>();
        // 查询审核中
        if (status == ContractStatusEnum.STUDENT_PENDING.getValue() || status == ContractStatusEnum.TEACHER_PENDING.getValue()) {
            statusSet.add(ContractStatusEnum.STUDENT_PENDING.getValue());
            statusSet.add(ContractStatusEnum.TEACHER_PENDING.getValue());
            queryWrapper.in("status", statusSet);
        }
        // 查询已拒绝
        if (status == ContractStatusEnum.STUDENT_REJECTED.getValue() || status == ContractStatusEnum.TEACHER_REJECTED.getValue()) {
            statusSet.add(ContractStatusEnum.STUDENT_REJECTED.getValue());
            statusSet.add(ContractStatusEnum.TEACHER_REJECTED.getValue());
            queryWrapper.in("status", statusSet);
        }
        // 查询已通过
        if (status == ContractStatusEnum.RESOLVED.getValue()) {
            queryWrapper.eq("status", ContractStatusEnum.RESOLVED.getValue());
        }

        // 查询signDate之前的
        queryWrapper.le(ObjectUtils.isNotEmpty(signDate), "signDate", signDate);


        String userRole = loginUser.getUserRole();
        long userId = loginUser.getId();


        // 企业只能查询自己的
        if (UserRoleEnum.ENTERPRISE.getValue().equals(userRole)) {
            queryWrapper.eq("enterpriseId", userId);
        }
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
    public Page<ContractVO> getContractVOPage(Page<Contract> contractPage, HttpServletRequest request) {
        List<Contract> contractList = contractPage.getRecords();
        Page<ContractVO> contractVOPage = new Page<>(contractPage.getCurrent(), contractPage.getSize(), contractPage.getTotal());
        if (CollUtil.isEmpty(contractList)) {
            return contractVOPage;
        }
        // Contract => ContractVO
        List<ContractVO> contractVOList = contractList.stream().map(contract -> {
            ContractVO contractVO = new ContractVO();
            BeanUtils.copyProperties(contract, contractVO);
            return contractVO;
        }).collect(Collectors.toList());


        contractVOPage.setRecords(contractVOList);
        return contractVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditContract(AuditRequest auditRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        int requestStatus = auditRequest.getStatus();
        List<Integer> values = AuditResultEnum.getValues();
        ThrowUtils.throwIf(!values.contains(requestStatus), ErrorCode.PARAMS_ERROR, "状态值错误");
        // 1.查询合同
        Contract contract = this.getById(auditRequest.getId());
        ThrowUtils.throwIf(contract == null, ErrorCode.NOT_FOUND_ERROR, "合同不存在");

        // 2.审核校验
        String userRole = loginUser.getUserRole();
        int resolvedValue = AuditResultEnum.RESOLVED.getValue();
        int rejectedValue = AuditResultEnum.REJECTED.getValue();
        String studentRoleValue = UserRoleEnum.STUDENT.getValue();
        String teacherRoleValue = UserRoleEnum.TEACHER.getValue();

        // 学生只能审核自己的
        long userId = loginUser.getId();
        ThrowUtils.throwIf(studentRoleValue.equals(userRole) && contract.getStudentId() != userId, ErrorCode.NO_AUTH_ERROR);
        // 学生只能审核状态为0的
        ThrowUtils.throwIf(studentRoleValue.equals(userRole) && !ContractStatusEnum.STUDENT_PENDING.getValue().equals(contract.getStatus()), ErrorCode.NO_AUTH_ERROR);
        // 老师只能审核状态为1的
        ThrowUtils.throwIf(teacherRoleValue.equals(userRole) && !ContractStatusEnum.TEACHER_PENDING.getValue().equals(contract.getStatus()), ErrorCode.NO_AUTH_ERROR);
        // 拒绝的话需要填写拒绝原因
        String rejectReason = auditRequest.getRejectReason();
        ThrowUtils.throwIf(requestStatus == rejectedValue && StringUtils.isBlank(rejectReason), ErrorCode.PARAMS_ERROR, "拒绝原因不能为空");

        // 根据不同角色更新合同状态
        if (studentRoleValue.equals(userRole)) {
            if (requestStatus == rejectedValue) {
                contract.setStatus(ContractStatusEnum.STUDENT_REJECTED.getValue());
                contract.setRejectReason(rejectReason);
            }
            if (requestStatus == resolvedValue) {
                contract.setStatus(ContractStatusEnum.TEACHER_PENDING.getValue());
            }
        } else if (teacherRoleValue.equals(userRole)) {
            if (requestStatus == rejectedValue) {
                contract.setStatus(ContractStatusEnum.TEACHER_REJECTED.getValue());
                contract.setRejectReason(rejectReason);
            }
            if (requestStatus == resolvedValue) {
                contract.setStatus(ContractStatusEnum.RESOLVED.getValue());
            }
            // 设置教师信息
            contract.setTeacherId(userId);
            contract.setTeacherName(loginUser.getUserName());
        } else {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 添加审核记录
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(loginUser.getId());
        auditLog.setUserName(loginUser.getUserName());
        auditLog.setTargetType(AuditTargetTypeEnum.CONTRACT.getValue());
        auditLog.setTargetId(contract.getId());
        auditLog.setTargetName(contract.getFileName());
        auditLog.setStatus(requestStatus);
        auditLog.setRejectReason(rejectReason);
        auditLogService.addAuditLog(auditLog);

        // 3.操作数据库
        boolean result = this.updateById(contract);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




