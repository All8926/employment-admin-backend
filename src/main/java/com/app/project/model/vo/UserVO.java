package com.app.project.model.vo;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 用户视图（脱敏）
 *
 * @author 
 * @from 
 */
@Data
public class UserVO implements Serializable {

    private Long id;
    private String userAccount;
    private String userName;
    private String userAvatar;
    private Integer gender;
    private String phone;
    private String email;
    private Long deptId;
    private String deptName;
    private Integer status;
    private String userProfile;
    private String userRole; // admin/student/teacher/enterprise

    /**
     * 创建时间
     */
    private Date createTime;

    private static final long serialVersionUID = 1L;
}