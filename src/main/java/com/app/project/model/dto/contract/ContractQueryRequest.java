package com.app.project.model.dto.contract;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 查询合同信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ContractQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 名称
     */
    private String fileName;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 签约日期
     */
    private Date signDate;

    /**
     * 状态
     */
    private Integer status;



    private static final long serialVersionUID = 1L;
}