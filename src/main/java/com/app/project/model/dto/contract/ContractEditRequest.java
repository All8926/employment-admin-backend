package com.app.project.model.dto.contract;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
 * 编辑合同信息请求
 *
 * @author
 * @from
 */
@Data
public class ContractEditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 路径
     */
    private String filePath;

    /**
     * 名称
     */
    private String fileName;


    /**
     * 备注
     */
    private String remark;

    /**
     * 签约日期
     */
    private Date signDate;


    private static final long serialVersionUID = 1L;
}