package com.app.project.model.dto.signInfo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 编辑简历信息请求
 *
 * @author
 * @from
 */
@Data
public class SignInfoEditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 岗位
     */
    private String post;

    /**
     * 薪资
     */
    private String salary;

    private static final long serialVersionUID = 1L;
}