package com.app.project.model.dto.auditLog;

import com.app.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 查询审核记录请求
 *
 * @author
 * @from
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AuditLogQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 审核人姓名
     */
    private String userName;


    /**
     * 审核类型
     */
    private String targetType;

    /**
     * 审核对象名称
     */
    private String targetName;

    /**
     * 0拒绝 1通过
     */
    private Integer status;


    private static final long serialVersionUID = 1L;
}