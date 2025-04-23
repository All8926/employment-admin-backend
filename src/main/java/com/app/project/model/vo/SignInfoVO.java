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
public class SignInfoVO implements Serializable {

    /**
     * 主键
     */
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 合同信息
     */
    private ContractVO contract;

    /**
     * 学生ID
     */
    private Long studentId;

    /**
     * 学生信息
     */
    private StudentVO student;

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


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
