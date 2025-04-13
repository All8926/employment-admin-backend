package com.app.project.common;

import io.swagger.annotations.ApiParam;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 审核合同信息请求
 *
 * @author
 * @from
 */
@Data
public class AuditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 审核状态
     */
    @NotNull(message = "审核状态不能为空")
    @ApiParam(value = "0-拒绝 1-通过")
    private Integer status;


    @ApiParam(value = "拒绝原因")
    private String rejectReason;


    private static final long serialVersionUID = 1L;
}