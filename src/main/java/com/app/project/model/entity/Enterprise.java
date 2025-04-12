package com.app.project.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 企业员工
 * @TableName enterprise
 */
@TableName(value ="enterprise")
@Data
public class Enterprise implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 头像
     */
    private String userAvatar;

    /**
     * 性别 0-男 1-女 2-未知
     */
    private Integer gender;

    /**
     * 职务
     */
    private String job;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：admin/student/teacher/enterprise
     */
    private String userRole;

    /**
     * 是否认证 0未认证 1已认证
     */
    private Integer isAuthorized;

    /**
     * 统一社会信用代码
     */
    private String licenseNum;

    /**
     * 办公地址
     */
    private String address;

    /**
     * 经营范围
     */
    private String businessScope;

    /**
     * 所属行业
     */
    private String industry;

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