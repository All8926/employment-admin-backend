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
     * 职位
     */
    private String job;

    /**
     * 是否认证
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

}
