package com.app.project.controller;


import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.auditLog.AuditLogQueryRequest;
import com.app.project.model.entity.AuditLog;
import com.app.project.service.AuditLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/audit")
@Api(tags = "审核记录")
public class AuditLogController {

    @Resource
    private AuditLogService auditLogService;


    @ApiOperation("记录列表")
    @PostMapping("/list/page")
    @AuthCheck(mustRoles = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public  BaseResponse<Page<AuditLog>> listContractByPage(@RequestBody AuditLogQueryRequest auditLogQueryRequest,
                                                          HttpServletRequest request) {
        long current = auditLogQueryRequest.getCurrent();
        long size = auditLogQueryRequest.getPageSize();

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取查询条件
        QueryWrapper<AuditLog> queryWrapper = auditLogService.getQueryWrapper(auditLogQueryRequest);

        // 查询数据库
        Page<AuditLog> contractPage = auditLogService.page(new Page<>(current, size),queryWrapper);

        return ResultUtils.success(contractPage);
    }

}
