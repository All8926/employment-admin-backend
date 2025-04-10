package com.app.project.controller;

import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.teacher.TeacherAddRequest;
import com.app.project.model.dto.teacher.TeacherEditRequest;
import com.app.project.model.dto.teacher.TeacherQueryRequest;
import com.app.project.model.dto.teacher.TeacherUpdateRequest;
import com.app.project.model.entity.Teacher;
import com.app.project.model.vo.TeacherVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.TeacherService;
import com.app.project.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * 教师信息接口
 *
 * @author
 * @from
 */
@Api(tags = "教师信息接口")
@RestController
@RequestMapping("/teacher")
@Slf4j
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建教师信息
     *
     * @param teacherAddRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "创建教师信息")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addTeacher(@Valid @RequestBody TeacherAddRequest teacherAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(teacherAddRequest == null, ErrorCode.PARAMS_ERROR);
        Boolean result = teacherService.addTeacher(teacherAddRequest);
        return ResultUtils.success(result);
    }

    /**
     * 删除教师信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "删除教师信息")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> deleteTeacher(@Valid @RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long id = deleteRequest.getId();
        // 判断是否存在
        Teacher oldTeacher = teacherService.getById(id);
        ThrowUtils.throwIf(oldTeacher == null, ErrorCode.NOT_FOUND_ERROR);

        // 操作数据库
        boolean result = teacherService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新教师信息（仅管理员可用）
     *
     * @param teacherUpdateRequest
     * @return
     */
    @ApiOperation(value = "更新教师信息")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateTeacher(@Valid @RequestBody TeacherUpdateRequest teacherUpdateRequest) {
        if (teacherUpdateRequest == null || teacherUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherUpdateRequest, teacher);

        // 判断是否存在
        long id = teacherUpdateRequest.getId();
        Teacher oldTeacher = teacherService.getById(id);
        ThrowUtils.throwIf(oldTeacher == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = teacherService.updateById(teacher);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取教师信息（封装类）
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取教师信息（封装类）")
    @GetMapping("/get/vo")
    public BaseResponse<TeacherVO> getTeacherVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Teacher teacher = teacherService.getById(id);
        ThrowUtils.throwIf(teacher == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(teacherService.getTeacherVO(teacher, request));
    }

    /**
     * 分页获取教师信息列表（仅管理员可用）
     *
     * @param teacherQueryRequest
     * @return
     */
    @ApiOperation(value = "分页获取教师信息列表（仅管理员可用）")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Teacher>> listTeacherByPage(@RequestBody TeacherQueryRequest teacherQueryRequest) {
        long current = teacherQueryRequest.getCurrent();
        long size = teacherQueryRequest.getPageSize();
        // 查询数据库
        Page<Teacher> teacherPage = teacherService.page(new Page<>(current, size),
                teacherService.getQueryWrapper(teacherQueryRequest));
        return ResultUtils.success(teacherPage);
    }

    /**
     * 分页获取教师信息列表（封装类）
     *
     * @param teacherQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    @ApiOperation(value = "分页获取教师信息列表（封装类）")
    public BaseResponse<Page<TeacherVO>> listTeacherVOByPage(@RequestBody TeacherQueryRequest teacherQueryRequest,
                                                               HttpServletRequest request) {
        long current = teacherQueryRequest.getCurrent();
        long size = teacherQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Teacher> teacherPage = teacherService.page(new Page<>(current, size),
                teacherService.getQueryWrapper(teacherQueryRequest));
        // 获取封装类
        return ResultUtils.success(teacherService.getTeacherVOPage(teacherPage, request));
    }



    /**
     * 编辑教师信息（给用户使用）
     *
     * @param teacherEditRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "编辑教师信息（给用户使用）")
    @PostMapping("/edit")
    public BaseResponse<Boolean> editTeacher(@RequestBody TeacherEditRequest teacherEditRequest, HttpServletRequest request) {
        if (teacherEditRequest == null || teacherEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Teacher teacher = new Teacher();
        BeanUtils.copyProperties(teacherEditRequest, teacher);

        UserVO loginUser = userService.getLoginUser(request);

        // 判断是否存在
        long id = teacherEditRequest.getId();
        Teacher oldTeacher = teacherService.getById(id);
        ThrowUtils.throwIf(oldTeacher == null, ErrorCode.NOT_FOUND_ERROR);

        // 仅本人可编辑
        if (!oldTeacher.getId().equals(loginUser.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = teacherService.updateById(teacher);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
