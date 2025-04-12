package com.app.project.model.dto.enterpriseCertification;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 创建简历信息请求
 *
 * @author
 * @from
 */
@Data
public class EnterpriseCertificationAddRequest implements Serializable {


    /**
     * 路径
     */
    @NotBlank(message = "路径不能为空")
    private String filePath;

    /**
     * 名称
     */
    @NotBlank(message = "名称不能为空")
    private String fileName;


    /**
     * 备注
     */
    private String remark;

    /**
     * 资质类型
     */
    private String certType;



    private static final long serialVersionUID = 1L;
}