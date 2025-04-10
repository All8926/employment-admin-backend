package com.app.project.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 部门信息视图
 *
 * @author
 * @from
 */
@Data
public class DepartmentTreeVO implements Serializable {

    private Long id;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 上级部门ID
     */
    private Long parentId;

    /**
     * 排序
     */
    private Integer sort;

    /**
     * 子部门
     */
    private List<DepartmentTreeVO> children = new ArrayList<>();
}
