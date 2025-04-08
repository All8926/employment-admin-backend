package com.app.project.model.dto.department;

import lombok.Data;

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
    private String name;

    /**
     * 上级部门ID
     */
    private Long parentId;


    private static final long serialVersionUID = 1L;
}