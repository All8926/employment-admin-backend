package com.app.project.service;

import com.app.project.model.dto.user.UserAuditRequest;
import com.app.project.model.dto.user.UserLoginRequest;
import com.app.project.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户服务
 *
 * @author 
 * @from 
 */
public interface UserService  {

    /**
     * 用户登录
     *
     * @param request
     * @return 脱敏后的用户信息
     */
    UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request);



    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    UserVO getLoginUser(HttpServletRequest request);


    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);


    /**
     * 审核用户
     * @param userAuditRequest
     * @param loginUser
     * @return
     */
    boolean auditUser(UserAuditRequest userAuditRequest, UserVO loginUser);
}
