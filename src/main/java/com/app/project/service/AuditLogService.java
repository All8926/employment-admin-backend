package com.app.project.service;


import com.app.project.model.dto.auditLog.AuditLogQueryRequest;
import com.app.project.model.entity.AuditLog;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Administrator
* @description 针对表【audit_log(审核记录)】的数据库操作Service
* @createDate 2025-04-14 09:11:49
*/
public interface AuditLogService extends IService<AuditLog> {

    /**
     * 新增审核记录
     */
    Boolean addAuditLog(AuditLog auditLog);


    /**
     * 获取查询条件
     * @param auditLogQueryRequest
     * @return
     */
    QueryWrapper<AuditLog> getQueryWrapper(AuditLogQueryRequest auditLogQueryRequest);

}
