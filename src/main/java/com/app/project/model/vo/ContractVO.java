package com.app.project.model.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 简历信息视图
 *
 * @author
 * @from
 */
@Data
public class ContractVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 路径
     */
    private String filePath;

    /**
     * 名称
     */
    private String fileName;

    /**
     * 企业id(创建人)
     */
    private Long enterpriseId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 学生id
     */
    private Long studentId;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 学生简历
     */
    private List<ResumeVO> resumeList;

    /**
     * 教师id(审核人)
     */
    private Long teacherId;

    /**
     * 教师姓名
     */
    private String teacherName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 签约日期
     */
    private Date signDate;

    /**
     * 状态
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


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
