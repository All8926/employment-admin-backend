package com.app.project.controller;

import com.app.project.common.BaseResponse;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.config.WxOpenConfig;
import com.app.project.exception.BusinessException;
import com.app.project.model.dto.user.UserLoginRequest;
import com.app.project.model.vo.UserVO;
import com.app.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 用户接口
 *
 * @author 
 * @from 
 */
@Api(tags = "用户通用接口")
@RestController
@RequestMapping("/user")
@Slf4j
@Validated
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private WxOpenConfig wxOpenConfig;

    // region 登录相关



    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public BaseResponse<UserVO> userLogin(@Valid  @RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        UserVO userVO = userService.userLogin(userLoginRequest, request);
        return ResultUtils.success(userVO);
    }



    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "退出登录")
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取用户信息")
    @GetMapping("/get/info")
    public BaseResponse<UserVO> getLoginUser(HttpServletRequest request) {
        UserVO userVO = userService.getLoginUser(request);

        return ResultUtils.success(userVO);
    }

}
