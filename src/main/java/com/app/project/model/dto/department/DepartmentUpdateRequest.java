package com.app.project.model.dto.department;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 更新部门信息请求
 *
 */
@Data
public class DepartmentUpdateRequest implements Serializable {

    /**
     *
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 部门名称
     */
    @NotBlank(message = "部门名称不能为空")
    private String name;

    /**
     * 上级部门ID
     */
    private Long parentId;

    /**
     * 排序字段
     */
    private Integer sort;


    private static final long serialVersionUID = 1L;
}