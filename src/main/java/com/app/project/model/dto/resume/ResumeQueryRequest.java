package com.app.project.model.dto.resume;

import com.app.project.common.PageRequest;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询简历信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ResumeQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;


    /**
     * 简历名称
     */
    private String fileName;


    /**
     * 备注
     */
    private String remark;

    /**
     * 生效状态 0否 1是
     */
    private Integer isActive;

    private static final long serialVersionUID = 1L;
}