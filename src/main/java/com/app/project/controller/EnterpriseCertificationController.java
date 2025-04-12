package com.app.project.controller;


import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationAddRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationEditRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationQueryRequest;
import com.app.project.model.entity.EnterpriseCertification;
import com.app.project.model.vo.EnterpriseCertificationVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.EnterpriseCertificationService;
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
@RequestMapping("/enterprise/certification")
@Api(tags = "企业资质管理")
public class EnterpriseCertificationController {

    @Resource
    private EnterpriseCertificationService enterpriseCertificationService;

    @Resource
    private UserService userService;


    @AuthCheck(mustRole = UserConstant.ENTERPRISE_ROLE)
    @ApiOperation("新增企业资质")
    @PostMapping("/add")
    public BaseResponse<Boolean> addEnterpriseCertification(@Valid @RequestBody EnterpriseCertificationAddRequest enterpriseCertificationAddRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);

        boolean result = enterpriseCertificationService.addEnterpriseCertification(enterpriseCertificationAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.ENTERPRISE_ROLE)
    @ApiOperation("修改企业资质")
    @PostMapping("/update")
    public  BaseResponse<Boolean> editEnterpriseCertification(@Valid @RequestBody EnterpriseCertificationEditRequest enterpriseCertificationEditRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = enterpriseCertificationService.editEnterpriseCertification(enterpriseCertificationEditRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.ENTERPRISE_ROLE)
    @ApiOperation("删除企业资质")
    @PostMapping("/delete")
    public  BaseResponse<Boolean> deleteEnterpriseCertification(@Valid @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = enterpriseCertificationService.removeEnterpriseCertificationById(deleteRequest, loginUser);
        return ResultUtils.success(result);
    }

    @ApiOperation("分页查询企业资质")
    @PostMapping("/list/page")
    @AuthCheck(mustRoles = {UserConstant.ADMIN_ROLE, UserConstant.ENTERPRISE_ROLE, UserConstant.TEACHER_ROLE})
    public  BaseResponse<Page<EnterpriseCertificationVO>> listEnterpriseCertificationByPage(@RequestBody EnterpriseCertificationQueryRequest enterpriseCertificationQueryRequest,
                                                          HttpServletRequest request) {
        long current = enterpriseCertificationQueryRequest.getCurrent();
        long size = enterpriseCertificationQueryRequest.getPageSize();
        UserVO loginUser = userService.getLoginUser(request);

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取查询条件
        QueryWrapper<EnterpriseCertification> queryWrapper = enterpriseCertificationService.getQueryWrapper(enterpriseCertificationQueryRequest, loginUser);

        // 查询数据库
        Page<EnterpriseCertification> enterpriseCertificationPage = enterpriseCertificationService.page(new Page<>(current, size),queryWrapper);
        // 获取封装类
        return ResultUtils.success(enterpriseCertificationService.getEnterpriseCertificationVOPage(enterpriseCertificationPage, request));
    }
}
