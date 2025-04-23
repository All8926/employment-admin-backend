package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 签约信息表
 * @TableName sign_info
 */
@TableName(value ="sign_info")
@Data
public class SignInfo implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 岗位
     */
    private String post;

    /**
     * 薪资
     */
    private String salary;

    /**
     * 备注
     */
    private String remark;

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