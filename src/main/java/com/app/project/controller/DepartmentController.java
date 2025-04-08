package com.app.project.controller;

import com.app.project.annotation.AuthCheck;
import com.app.project.common.BaseResponse;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ResultUtils;
import com.app.project.constant.UserConstant;
import com.app.project.model.dto.department.DepartmentAddRequest;
import com.app.project.model.dto.department.DepartmentUpdateRequest;
import com.app.project.model.entity.User;
import com.app.project.model.vo.DepartmentTreeVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/department")
@Api(tags = "部门管理")
public class DepartmentController {

    @Resource
    private DepartmentService departmentService;

    @Resource
    private UserService userService;


    @ApiOperation("新增部门")
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> addDepartment(@Valid @RequestBody DepartmentAddRequest departmentAddRequest, HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);

        boolean result = departmentService.addDepartment(departmentAddRequest);
        return ResultUtils.success(result);
    }

    @ApiOperation("修改部门")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public  BaseResponse<Boolean> updateDepartment(@Valid @RequestBody DepartmentUpdateRequest departmentUpdateRequest) {
        boolean result = departmentService.updateDepartment(departmentUpdateRequest);
        return ResultUtils.success(result);
    }

    @ApiOperation("删除部门")
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public  BaseResponse<Boolean> deleteDepartment(@Valid @RequestBody DeleteRequest deleteRequest) {
        boolean result = departmentService.removeDepartmentById(deleteRequest);
        return ResultUtils.success(result);
    }

    @ApiOperation("查询部门树")
    @GetMapping("/tree")
    public  BaseResponse<List<DepartmentTreeVO>> deleteDepartment() {
          List<DepartmentTreeVO> departmentTree = departmentService.getDepartmentTree();
        return ResultUtils.success(departmentTree);
    }
}
