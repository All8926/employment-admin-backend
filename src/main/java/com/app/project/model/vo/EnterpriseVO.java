package com.app.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
     * 所属企业ID（关联企业资质表）
     */
    private Long enterpriseId;



}
