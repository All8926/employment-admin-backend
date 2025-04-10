package com.app.project.service;

import com.app.project.model.dto.user.UserLoginRequest;
import com.app.project.model.dto.user.UserQueryRequest;
import com.app.project.model.entity.User;
import com.app.project.model.vo.LoginUserVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author 
 * @from 
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword);

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
     * 是否为管理员
     *
     * @param request
     * @return
     */
//    boolean isAdmin(HttpServletRequest request);
//
//    /**
//     * 是否为管理员
//     *
//     * @param user
//     * @return
//     */
//    boolean isAdmin(User user);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);





}
