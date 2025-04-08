package com.app.project.model.dto.department;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建部门信息请求
 *
 * @author
 * @from
 */
@Data
public class DepartmentAddRequest implements Serializable {

    /**
     * 部门名称
     */
    @NotBlank(message = "名称不能为空")
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