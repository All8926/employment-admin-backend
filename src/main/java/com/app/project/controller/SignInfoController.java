package com.app.project.controller;


import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.signInfo.SignInfoAddRequest;
import com.app.project.model.dto.signInfo.SignInfoEditRequest;
import com.app.project.model.dto.signInfo.SignInfoQueryRequest;
import com.app.project.model.entity.SignInfo;
import com.app.project.model.vo.SignInfoVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.SignInfoService;
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
@RequestMapping("/signInfo")
@Api(tags = "签约信息管理")
public class SignInfoController {

    @Resource
    private SignInfoService signInfoService;

    @Resource
    private UserService userService;


    @AuthCheck(mustRole = UserConstant.STUDENT_ROLE)
    @ApiOperation("新增签约信息")
    @PostMapping("/add")
    public BaseResponse<Boolean> addSignInfo(@Valid @RequestBody SignInfoAddRequest signInfoAddRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);

        boolean result = signInfoService.addSignInfo(signInfoAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.STUDENT_ROLE)
    @ApiOperation("修改签约信息")
    @PostMapping("/update")
    public  BaseResponse<Boolean> editSignInfo(@Valid @RequestBody SignInfoEditRequest signInfoEditRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = signInfoService.editSignInfo(signInfoEditRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.STUDENT_ROLE)
    @ApiOperation("删除签约信息")
    @PostMapping("/delete")
    public  BaseResponse<Boolean> deleteSignInfo(@Valid @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = signInfoService.removeSignInfoById(deleteRequest, loginUser);
        return ResultUtils.success(result);
    }

    @ApiOperation("分页查询签约信息")
    @PostMapping("/list/page")
    @AuthCheck(mustRoles = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE, UserConstant.STUDENT_ROLE})
    public  BaseResponse<Page<SignInfoVO>> listSignInfoByPage(@RequestBody SignInfoQueryRequest signInfoQueryRequest,
                                                              HttpServletRequest request) {
        long current = signInfoQueryRequest.getCurrent();
        long size = signInfoQueryRequest.getPageSize();
        UserVO loginUser = userService.getLoginUser(request);

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取查询条件
        QueryWrapper<SignInfo> queryWrapper = signInfoService.getQueryWrapper(signInfoQueryRequest, loginUser);

        // 查询数据库
        Page<SignInfo> signInfoPage = signInfoService.page(new Page<>(current, size),queryWrapper);
        // 获取封装类
        return ResultUtils.success(signInfoService.getSignInfoVOPage(signInfoPage, request));
    }
}
