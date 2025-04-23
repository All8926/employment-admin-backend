package com.app.project.model.dto.signInfo;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询简历信息请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SignInfoQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 学生姓名
     */
    private String studentName;

    /**
     * 岗位
     */
    private String post;

    /**
     * 薪资
     */
    private String salary;



    private static final long serialVersionUID = 1L;
}