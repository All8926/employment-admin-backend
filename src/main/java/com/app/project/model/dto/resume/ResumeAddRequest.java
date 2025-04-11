package com.app.project.model.dto.resume;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * 创建简历信息请求
 *
 * @author
 * @from
 */
@Data
public class ResumeAddRequest implements Serializable {


    /**
     * 简历路径
     */
    @NotBlank(message = "简历不能为空")
    private String filePath;

    /**
     * 简历名称
     */
    @NotBlank(message = "简历名称不能为空")
    private String fileName;


    /**
     * 备注
     */
    private String remark;


    private static final long serialVersionUID = 1L;
}