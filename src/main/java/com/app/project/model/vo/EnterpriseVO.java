package com.app.project.model.vo;

import lombok.Data;

/**
 * 学生信息视图
 *
 * @author
 * @from
 */
@Data
public class EnterpriseVO extends UserVO {


    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 所属企业ID（关联企业信息表）
     */
    private Long enterpriseId;

    /**
     * 职位
     */
    private String job;

    /**
     * 是否认证
     */
    private Integer isAuthorized;

}
