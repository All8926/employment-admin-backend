package com.app.project.service.impl;

import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.DepartmentMapper;
import com.app.project.model.dto.department.DepartmentAddRequest;
import com.app.project.model.dto.department.DepartmentUpdateRequest;
import com.app.project.model.entity.Department;
import com.app.project.model.vo.DepartmentTreeVO;
import com.app.project.service.DepartmentService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * @author Administrator
 * @description 针对表【department(部门表)】的数据库操作Service实现
 * @createDate 2025-04-08 09:16:16
 */
@Service
public class DepartmentServiceImpl extends ServiceImpl<DepartmentMapper, Department>
        implements DepartmentService {

    @Override
    public boolean addDepartment(DepartmentAddRequest departmentAddRequest) {

        // 1.校验名称是否重复
        QueryWrapper<Department> departmentQueryWrapper = new QueryWrapper<>();
        departmentQueryWrapper.eq("name", departmentAddRequest.getName());
        long count = this.count(departmentQueryWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "名称重复");

        // 2.校验父级是否存在
        Long parentId = departmentAddRequest.getParentId();
        if (parentId != null && parentId != 0) {
            Department parent = this.getById(departmentAddRequest.getParentId());
            ThrowUtils.throwIf(parent == null, ErrorCode.PARAMS_ERROR, "父级不存在");
        }

        // 3.插入数据
        Department department = new Department();
        BeanUtils.copyProperties(departmentAddRequest, department);
        boolean saveResult = this.save(department);
        ThrowUtils.throwIf(!saveResult, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean updateDepartment(DepartmentUpdateRequest departmentUpdateRequest) {
        // 1. 判断要修改的部门是否存在
        long updateRequestId = departmentUpdateRequest.getId();
        Department oldDept = this.getById(updateRequestId);
        ThrowUtils.throwIf(oldDept == null, ErrorCode.NOT_FOUND_ERROR, "部门不存在");

        // 2. 校验名称是否重复（排除自己）
        QueryWrapper<Department> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("name", departmentUpdateRequest.getName()).ne("id", departmentUpdateRequest.getId());
        long count = this.count(nameWrapper);
        ThrowUtils.throwIf(count > 0, ErrorCode.PARAMS_ERROR, "部门名称重复");

        // 3. 校验父级是否存在（非0时校验）
        Long parentId = departmentUpdateRequest.getParentId();
        if (parentId != null && parentId != 0) {
            Department parent = this.getById(parentId);
            ThrowUtils.throwIf(parent == null, ErrorCode.PARAMS_ERROR, "父级部门不存在");
            // 防止设置自己为自己的父部门
            ThrowUtils.throwIf(parentId.equals(departmentUpdateRequest.getId()), ErrorCode.PARAMS_ERROR, "不能将自己设置为自己的父部门");

            Set<Long> allChildIds = getAllChildDepartmentIds(updateRequestId);
            ThrowUtils.throwIf(allChildIds.contains(parentId), ErrorCode.PARAMS_ERROR, "不能将子部门设置为上级部门");
        }

        // 4. 更新
        Department updateDept = new Department();
        BeanUtils.copyProperties(departmentUpdateRequest, updateDept);
        boolean result = this.updateById(updateDept);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "更新失败");

        return true;
    }

    @Override
    public boolean removeDepartmentById(DeleteRequest deleteRequest) {
        long id = deleteRequest.getId();
        // 1. 校验是否存在
        Department department = this.getById(id);
        ThrowUtils.throwIf(department == null, ErrorCode.NOT_FOUND_ERROR, "部门不存在");

        // 2. 校验是否有未被删除的子部门
        QueryWrapper<Department> childWrapper = new QueryWrapper<>();
        childWrapper.eq("parentId", id);
        long childCount = this.count(childWrapper);
        ThrowUtils.throwIf(childCount > 0, ErrorCode.PARAMS_ERROR, "请先删除子部门");

        // 3. 逻辑删除
        boolean result = this.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR, "删除失败");
        return true;
    }

    @Override
    public List<DepartmentTreeVO> getDepartmentTree() {
        // 1. 查询所有部门
        List<Department> departmentList = this.list(new QueryWrapper<Department>().orderByAsc("sort"));

        // 2. 转换成 map，方便查找
        Map<Long, DepartmentTreeVO> idToNodeMap = new HashMap<>();
        List<DepartmentTreeVO> rootList = new ArrayList<>();

        for (Department dept : departmentList) {
            DepartmentTreeVO node = new DepartmentTreeVO();
            BeanUtils.copyProperties(dept, node);
            idToNodeMap.put(node.getId(), node);
        }

        // 3. 构造树
        for (Department dept : departmentList) {
            Long parentId = dept.getParentId();
            DepartmentTreeVO node = idToNodeMap.get(dept.getId());

            if (parentId == 0 || !idToNodeMap.containsKey(parentId)) {
                // 根节点
                rootList.add(node);
            } else {
                // 子节点加入到父节点的 children 中
                DepartmentTreeVO parent = idToNodeMap.get(parentId);
                parent.getChildren().add(node);
            }
        }

        return rootList;
    }

    @Override
    public Set<Long> getAllChildDepartmentIds(Long parentId) {
        Set<Long> resultSet = new HashSet<>();
        collectChildDeptIds(parentId, resultSet);
        return resultSet;
    }


    private void collectChildDeptIds(Long currentId, Set<Long> collector) {
        // 查询当前节点的直接子部门
        List<Department> children = this.list(new QueryWrapper<Department>().eq("parentId", currentId));
        for (Department child : children) {
            Long childId = child.getId();
            if (collector.add(childId)) { // 避免死循环
                collectChildDeptIds(childId, collector); // 递归收集
            }
        }
    }
}




