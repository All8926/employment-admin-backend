package com.app.project.model.dto.resume;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 编辑简历信息请求
 *
 * @author
 * @from
 */
@Data
public class ResumeEditRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "id不能为空")
    private Long id;

    /**
     * 简历路径
     */
    private String filePath;

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