package com.app.project.model.dto.student;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 编辑学生信息请求
 *
 * @author
 * @from
 */
@Data
public class StudentEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    private static final long serialVersionUID = 1L;
}