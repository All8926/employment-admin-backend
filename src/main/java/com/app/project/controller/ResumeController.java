package com.app.project.controller;


import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.resume.ResumeAddRequest;
import com.app.project.model.dto.resume.ResumeEditRequest;
import com.app.project.model.dto.resume.ResumeQueryRequest;
import com.app.project.model.entity.Resume;
import com.app.project.model.vo.ResumeVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.ResumeService;
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
@RequestMapping("/resume")
@Api(tags = "简历管理")
public class ResumeController {

    @Resource
    private ResumeService resumeService;

    @Resource
    private UserService userService;


    @AuthCheck(mustRole = UserConstant.STUDENT_ROLE)
    @ApiOperation("新增简历")
    @PostMapping("/add")
    public BaseResponse<Boolean> addResume(@Valid @RequestBody ResumeAddRequest resumeAddRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);

        boolean result = resumeService.addResume(resumeAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.STUDENT_ROLE)
    @ApiOperation("修改简历")
    @PostMapping("/update")
    public  BaseResponse<Boolean> editResume(@Valid @RequestBody ResumeEditRequest resumeEditRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = resumeService.editResume(resumeEditRequest, loginUser);
        return ResultUtils.success(result);
    }

    @AuthCheck(mustRole = UserConstant.STUDENT_ROLE)
    @ApiOperation("删除简历")
    @PostMapping("/delete")
    public  BaseResponse<Boolean> deleteResume(@Valid @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        UserVO loginUser = userService.getLoginUser(request);
        boolean result = resumeService.removeResumeById(deleteRequest, loginUser);
        return ResultUtils.success(result);
    }

    @ApiOperation("分页查询简历")
    @PostMapping("/list/page")
    @AuthCheck(mustRoles = {UserConstant.ADMIN_ROLE, UserConstant.TEACHER_ROLE, UserConstant.STUDENT_ROLE})
    public  BaseResponse<Page<ResumeVO>> listResumeByPage(@RequestBody ResumeQueryRequest resumeQueryRequest,
                                                          HttpServletRequest request) {
        long current = resumeQueryRequest.getCurrent();
        long size = resumeQueryRequest.getPageSize();
        UserVO loginUser = userService.getLoginUser(request);

        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 获取查询条件
        QueryWrapper<Resume> queryWrapper = resumeService.getQueryWrapper(resumeQueryRequest, loginUser);

        // 查询数据库
        Page<Resume> resumePage = resumeService.page(new Page<>(current, size),queryWrapper);
        // 获取封装类
        return ResultUtils.success(resumeService.getResumeVOPage(resumePage, request));
    }
}
