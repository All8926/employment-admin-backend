package com.app.project.controller;

import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.student.StudentAddRequest;
import com.app.project.model.dto.student.StudentEditRequest;
import com.app.project.model.dto.student.StudentQueryRequest;
import com.app.project.model.dto.student.StudentUpdateRequest;
import com.app.project.model.entity.Student;
import com.app.project.model.vo.LoginUserVO;
import com.app.project.model.vo.StudentVO;
import com.app.project.service.StudentService;
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
 * 学生信息接口
 *
 * @author
 * @from
 */
@Api(tags = "学生管理")
@RestController
@RequestMapping("/student")
@Slf4j
public class StudentController {

    @Resource
    private StudentService studentService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 注册学生信息
     *
     * @param studentAddRequest
     * @param request
     * @return
     */
    @PostMapping("/register")
    @ApiOperation(value = "注册学生信息")
    public BaseResponse<Boolean> addStudent(@Valid @RequestBody StudentAddRequest studentAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(studentAddRequest == null, ErrorCode.PARAMS_ERROR);

        boolean result = studentService.register(studentAddRequest);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 删除学生信息
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteStudent(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Student oldStudent = studentService.getById(id);
        ThrowUtils.throwIf(oldStudent == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
//        if (!oldStudent.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        // 操作数据库
        boolean result = studentService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新学生信息（仅管理员可用）
     *
     * @param studentUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateStudent(@RequestBody StudentUpdateRequest studentUpdateRequest) {
        if (studentUpdateRequest == null || studentUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Student student = new Student();
        BeanUtils.copyProperties(studentUpdateRequest, student);
        // 数据校验
        studentService.validStudent(student, false);
        // 判断是否存在
        long id = studentUpdateRequest.getId();
        Student oldStudent = studentService.getById(id);
        ThrowUtils.throwIf(oldStudent == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = studentService.updateById(student);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取学生信息（封装类）
     *
     * @param id
     * @return
     */
    @ApiOperation(value = "根据 id 获取学生信息（封装类）")
    @GetMapping("/get/vo")
    public BaseResponse<StudentVO> getStudentVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Student student = studentService.getById(id);
        ThrowUtils.throwIf(student == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(studentService.getStudentVO(student, request));
    }

    /**
     * 分页获取学生信息列表（仅管理员可用）
     *
     * @param studentQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Student>> listStudentByPage(@RequestBody StudentQueryRequest studentQueryRequest) {
        long current = studentQueryRequest.getCurrent();
        long size = studentQueryRequest.getPageSize();
        // 查询数据库
        Page<Student> studentPage = studentService.page(new Page<>(current, size),
                studentService.getQueryWrapper(studentQueryRequest));
        return ResultUtils.success(studentPage);
    }

    /**
     * 分页获取学生信息列表（封装类）
     *
     * @param studentQueryRequest
     * @param request
     * @return
     */
    @ApiOperation(value = "分页获取学生信息列表（封装类）")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<StudentVO>> listStudentVOByPage(@RequestBody StudentQueryRequest studentQueryRequest,
                                                               HttpServletRequest request) {
        long current = studentQueryRequest.getCurrent();
        long size = studentQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Student> studentPage = studentService.page(new Page<>(current, size),
                studentService.getQueryWrapper(studentQueryRequest));
        // 获取封装类
        return ResultUtils.success(studentService.getStudentVOPage(studentPage, request));
    }


    /**
     * 编辑学生信息（给用户使用）
     *
     * @param studentEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editStudent(@RequestBody StudentEditRequest studentEditRequest, HttpServletRequest request) {
        if (studentEditRequest == null || studentEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // todo 在此处将实体类和 DTO 进行转换
        Student student = new Student();
        BeanUtils.copyProperties(studentEditRequest, student);
        // 数据校验
        studentService.validStudent(student, false);
//        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = studentEditRequest.getId();
        Student oldStudent = studentService.getById(id);
        ThrowUtils.throwIf(oldStudent == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
//        if (!oldStudent.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
//            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
//        }
        // 操作数据库
        boolean result = studentService.updateById(student);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion
}
