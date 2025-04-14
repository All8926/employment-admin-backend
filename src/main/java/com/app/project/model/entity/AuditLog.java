package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 审核记录
 * @TableName audit_log
 */
@TableName(value ="audit_log")
@Data
public class AuditLog implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 审核人id
     */
    private Long userId;

    /**
     * 审核人姓名
     */
    private String userName;

    /**
     * 审核对象id
     */
    private Long targetId;

    /**
     * 审核类型
     */
    private String targetType;

    /**
     * 审核对象名称
     */
    private String targetName;

    /**
     * 0拒绝 1通过
     */
    private Integer status;

    /**
     * 拒绝原因
     */
    private String rejectReason;

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