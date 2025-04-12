package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业资质
 * @TableName enterprise_certification
 */
@TableName(value ="enterprise_certification")
@Data
public class EnterpriseCertification implements Serializable {
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
     * 创建人
     */
    private Long userId;

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

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}