package com.app.project.model.dto.enterpriseCertification;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询简历信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnterpriseCertificationQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;



    /**
     * 名称
     */
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