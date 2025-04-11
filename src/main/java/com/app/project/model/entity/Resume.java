package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 简历
 * @TableName resume
 */
@TableName(value ="resume")
@Data
public class Resume implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 简历路径
     */
    private String filePath;

    /**
     * 简历名称
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
     * 生效状态 0否 1是
     */
    private Integer isActive;

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