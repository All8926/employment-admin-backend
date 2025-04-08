package com.app.project.service;


import com.app.project.common.DeleteRequest;
import com.app.project.model.dto.department.DepartmentAddRequest;
import com.app.project.model.dto.department.DepartmentUpdateRequest;
import com.app.project.model.entity.Department;
import com.app.project.model.vo.DepartmentTreeVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;


/**
* @author Administrator
* @description 针对表【department(部门表)】的数据库操作Service
* @createDate 2025-04-08 09:16:16
*/
public interface DepartmentService extends IService<Department> {

    /**
     * 新增部门
     * @param departmentAddRequest
     * @return
     */
    boolean addDepartment(DepartmentAddRequest departmentAddRequest);

    /**
     * 修改部门
     * @param departmentUpdateRequest
     * @return
     */
    boolean updateDepartment(DepartmentUpdateRequest departmentUpdateRequest);

    /**
     * 根据id删除部门
     * @param deleteRequest
     * @return
     */
    boolean removeDepartmentById(DeleteRequest deleteRequest);

    /**
     * 查询部门树
     * @return
     */
    List<DepartmentTreeVO> getDepartmentTree();
}
