package com.app.project.model.dto.signInfo;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 创建简历信息请求
 *
 * @author
 * @from
 */
@Data
public class SignInfoAddRequest implements Serializable {

    /**
     * 合同id
     */
    @NotNull(message = "合同不能为空")
    private Long contractId;

    /**
     * 岗位
     */
    @NotBlank(message = "岗位不能为空")
    private String post;

    /**
     * 薪资
     */
    @NotBlank(message = "薪资不能为空")
    private String salary;

    /**
     * 备注
     */
    private String remark;



    private static final long serialVersionUID = 1L;
}