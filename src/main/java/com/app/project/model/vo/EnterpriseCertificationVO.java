package com.app.project.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简历信息视图
 *
 * @author
 * @from
 */
@Data
public class EnterpriseCertificationVO implements Serializable {

    /**
     * id
     */
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
     * 创建人
     */
    private Long userId;

    /**
     * 企业信息
     */
    private EnterpriseVO enterprise;

    /**
     * 备注
     */
    private String remark;

    /**
     * 资质类型
     */
    private String certType;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
