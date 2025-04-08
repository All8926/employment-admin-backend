package com.app.project.model.dto.department;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 查询部门信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DepartmentQueryRequest extends PageRequest implements Serializable {

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