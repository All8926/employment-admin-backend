package com.app.project.model.dto.enterpriseCertification;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

/**
 * 编辑简历信息请求
 *
 * @author
 * @from
 */
@Data
public class EnterpriseCertificationEditRequest implements Serializable {

    /**
     * id
     */
    @TableId(type = IdType.AUTO)
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
     * 资质类型
     */
    private String certType;


    private static final long serialVersionUID = 1L;
}