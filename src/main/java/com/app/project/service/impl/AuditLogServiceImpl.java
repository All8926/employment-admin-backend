package com.app.project.service.impl;

import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.AuditLogMapper;
import com.app.project.model.dto.auditLog.AuditLogQueryRequest;
import com.app.project.model.entity.AuditLog;
import com.app.project.model.enums.AuditResultEnum;
import com.app.project.service.AuditLogService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Administrator
 * @description 针对表【audit_log(审核记录)】的数据库操作Service实现
 * @createDate 2025-04-14 09:11:49
 */
@Service
public class AuditLogServiceImpl extends ServiceImpl<AuditLogMapper, AuditLog>
        implements AuditLogService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean addAuditLog(AuditLog auditLog) {
        // 校验参数是否存在
        int status = auditLog.getStatus();
        long userId = auditLog.getUserId();
        String userName = auditLog.getUserName();
        long targetId = auditLog.getTargetId();
        String targetType = auditLog.getTargetType();
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userName, targetType), ErrorCode.OPERATION_ERROR);
        ThrowUtils.throwIf(ObjectUtils.anyNull(status, userId, targetId), ErrorCode.OPERATION_ERROR);

        // 校验状态参数是否合法
        List<Integer> values = AuditResultEnum.getValues();
        ThrowUtils.throwIf(!values.contains(status), ErrorCode.OPERATION_ERROR);

        // 添加记录
        boolean result = this.save(auditLog);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }


    @Override
    public QueryWrapper<AuditLog> getQueryWrapper(AuditLogQueryRequest auditLogQueryRequest) {
        QueryWrapper<AuditLog> queryWrapper = new QueryWrapper<>();
        if (auditLogQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = auditLogQueryRequest.getId();
        String userName = auditLogQueryRequest.getUserName();
        String targetName = auditLogQueryRequest.getTargetName();
        String targetType = auditLogQueryRequest.getTargetType();
        Integer status = auditLogQueryRequest.getStatus();
        String sortOrder = auditLogQueryRequest.getSortOrder();
        String sortField = auditLogQueryRequest.getSortField();

        // 模糊查询
        queryWrapper.like(com.alibaba.excel.util.StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(com.alibaba.excel.util.StringUtils.isNotBlank(targetName), "targetName", targetName);

        // 精确查询
        queryWrapper.eq(com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(com.baomidou.mybatisplus.core.toolkit.ObjectUtils.isNotEmpty(targetType), "targetType", targetType);


        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }
}




