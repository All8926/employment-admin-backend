package com.app.project.model.dto.enterprise;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询学生信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class EnterpriseQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 姓名
     */
    private String userName;

    /**
     * 企业名称
     */
    private String enterpriseName;



    /**
     * 性别 0-男 1-女 2-未知
     */
    private Integer gender;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态 0-待审核 1-已通过 2-已拒绝
     */
    private Integer status;

    /**
     * 是否认证
     */
    private Integer isAuthorized;



    private static final long serialVersionUID = 1L;
}