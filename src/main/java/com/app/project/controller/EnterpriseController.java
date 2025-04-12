package com.app.project.controller;

import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.enterprise.EnterpriseAddRequest;
import com.app.project.model.dto.enterprise.EnterpriseEditRequest;
import com.app.project.model.dto.enterprise.EnterpriseQueryRequest;
import com.app.project.model.dto.enterprise.EnterpriseUpdateRequest;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.vo.EnterpriseVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.EnterpriseService;
import com.app.project.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 企业信息接口
 *
 * @author
 * @from
 */
@Api(tags = "企业信息管理")
@RestController
@RequestMapping("/enterprise")
@Slf4j
public class EnterpriseController {

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 注册企业信息
     *
     * @param enterpriseAddRequest
     * @param request
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册企业信息")
    public BaseResponse<Boolean> addEnterprise(@Valid @RequestBody EnterpriseAddRequest enterpriseAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(enterpriseAddRequest == null, ErrorCode.PARAMS_ERROR);

        boolean result = enterpriseService.register(enterpriseAddRequest);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 删除企业信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteEnterprise(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Enterprise oldEnterprise = enterpriseService.getById(id);
        ThrowUtils.throwIf(oldEnterprise == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = enterpriseService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新企业信息
     *
     * @param enterpriseUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRoles = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Boolean> updateEnterprise(@RequestBody EnterpriseUpdateRequest enterpriseUpdateRequest) {
        if (enterpriseUpdateRequest == null || enterpriseUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseUpdateRequest, enterprise); 
        
        // 判断是否存在
        long id = enterpriseUpdateRequest.getId();
        Enterprise oldEnterprise = enterpriseService.getById(id);
        ThrowUtils.throwIf(oldEnterprise == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = enterpriseService.updateById(enterprise);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

 

    /**
     * 分页获取企业信息列表（封装类）
     *
     * @param enterpriseQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取企业信息列表（封装类）")
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRoles = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE})
    public BaseResponse<Page<EnterpriseVO>> listEnterpriseVOByPage(@RequestBody EnterpriseQueryRequest enterpriseQueryRequest,
                                                               HttpServletRequest request) {
        long current = enterpriseQueryRequest.getCurrent();
        long size = enterpriseQueryRequest.getPageSize();
          UserVO loginUser = userService.getLoginUser(request);

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取查询条件
          QueryWrapper<Enterprise> queryWrapper = enterpriseService.getQueryWrapper(enterpriseQueryRequest, loginUser);

        // 查询数据库
        Page<Enterprise> enterprisePage = enterpriseService.page(new Page<>(current, size),queryWrapper);
        // 获取封装类
        return ResultUtils.success(enterpriseService.getEnterpriseVOPage(enterprisePage, request));
    }


    /**
     * 编辑企业信息（给用户使用）
     *
     * @param enterpriseEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editEnterprise(@RequestBody EnterpriseEditRequest enterpriseEditRequest, HttpServletRequest request) {
        if (enterpriseEditRequest == null || enterpriseEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseEditRequest, enterprise);

        // 判断是否存在
        long id = enterpriseEditRequest.getId();
        Enterprise oldEnterprise = enterpriseService.getById(id);
        ThrowUtils.throwIf(oldEnterprise == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人可编辑
        if (!oldEnterprise.getId().equals(id)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = enterpriseService.updateById(enterprise);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
