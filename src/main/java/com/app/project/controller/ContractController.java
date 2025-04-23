package com.app.project.controller;


import com.app.project.annotation.AuthCheck;
import com.app.project.common.AuditRequest;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.model.dto.contract.ContractAddRequest;
import com.app.project.model.dto.contract.ContractEditRequest;
import com.app.project.model.dto.contract.ContractQueryRequest;
import com.app.project.model.entity.Contract;
import com.app.project.model.vo.ContractVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.ContractService;
import com.app.project.service.UserService;
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
import javax.validation.Valid;

@RestController
@RequestMapping("/contract")
@Api(tags = "合同管理")
public class ContractController {

    @Resource
    private ContractService contractService;

    @Resource
    private UserService userService;


    @AuthCheck(mustRole = UserConstant.ENTERPRISE_ROLE)
    @ApiOperation("新增合同")
    @PostMapping("/add")
    public BaseResponse<Boolean> addContract(@Valid @RequestBody ContractAddRequest contractAddRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);

        boolean result = contractService.addContract(contractAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.ENTERPRISE_ROLE)
    @ApiOperation("修改合同")
    @PostMapping("/update")
    public  BaseResponse<Boolean> editContract(@Valid @RequestBody ContractEditRequest contractEditRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = contractService.editContract(contractEditRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.ENTERPRISE_ROLE)
    @ApiOperation("删除合同")
    @PostMapping("/delete")
    public  BaseResponse<Boolean> deleteContract(@Valid @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = contractService.removeContractById(deleteRequest, loginUser);
        return ResultUtils.success(result);
    }

    @ApiOperation("分页查询合同")
    @PostMapping("/list/page")
    public  BaseResponse<Page<ContractVO>> listContractByPage(@RequestBody ContractQueryRequest contractQueryRequest,
                                                          HttpServletRequest request) {
        long current = contractQueryRequest.getCurrent();
        long size = contractQueryRequest.getPageSize();
        UserVO loginUser = userService.getLoginUser(request);

        // 获取查询条件
        QueryWrapper<Contract> queryWrapper = contractService.getQueryWrapper(contractQueryRequest, loginUser);

        // 查询数据库
        Page<Contract> contractPage = contractService.page(new Page<>(current, size),queryWrapper);
        // 获取封装类
        return ResultUtils.success(contractService.getContractVOPage(contractPage, request));
    }

    @AuthCheck(mustRoles = {UserConstant.STUDENT_ROLE, UserConstant.TEACHER_ROLE})
    @ApiOperation("审核合同")
    @PostMapping("/audit")
    public BaseResponse<Boolean> auditContract(@RequestBody AuditRequest auditRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = contractService.auditContract(auditRequest, loginUser);
        return ResultUtils.success(result);
    }
}
