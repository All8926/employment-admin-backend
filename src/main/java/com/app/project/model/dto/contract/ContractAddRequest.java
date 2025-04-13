package com.app.project.model.dto.contract;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Date;

/**
 * 创建合同信息请求
 *
 * @author
 * @from
 */
@Data
public class ContractAddRequest implements Serializable {

    /**
     * 路径
     */
    @NotBlank(message = "文件不能为空")
    private String filePath;

    /**
     * 名称
     */
    private String fileName;


    /**
     * 学生姓名
     */
    @NotBlank(message = "学生姓名不能为空")
    private String studentName;

    /**
     * 学生账号
     */
    @NotBlank(message = "学生账号不能为空")
    private String studentAccount;

    /**
     * 备注
     */
    private String remark;

    /**
     * 签约日期
     */
    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    private Date signDate;


    private static final long serialVersionUID = 1L;
}