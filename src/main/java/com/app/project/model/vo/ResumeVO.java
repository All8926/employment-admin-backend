package com.app.project.model.vo;

import cn.hutool.json.JSONUtil;
import com.app.project.model.entity.Resume;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;
import org.springframework.beans.BeanUtils;

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
public class ResumeVO implements Serializable {

    /**
     * id
     */
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
     * 创建人id
     */
    private Long userId;

    /**
     * 创建人姓名
     */
    private String userName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 生效状态 0否 1是
     */
    private Integer isActive;

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
