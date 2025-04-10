package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.UserMapper;
import com.app.project.model.dto.user.UserLoginRequest;
import com.app.project.model.dto.user.UserQueryRequest;
import com.app.project.model.entity.*;
import com.app.project.model.enums.RegisterStatusEnum;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.LoginUserVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.*;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.bean.WxOAuth2UserInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.app.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author
 * @from
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xxx";

    @Resource
    private StudentService studentService;

    @Resource
    private TeacherService teacherService;

    @Resource
    private EnterpriseService enterpriseService;

    @Resource
    private UserVOFactory userVOFactory;

    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("userAccount", userAccount);
            long count = this.baseMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            User user = new User();
            user.setUserAccount(userAccount);
            user.setUserPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public UserVO userLogin(UserLoginRequest userLoginRequest, HttpServletRequest request) {
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        String userRole = userLoginRequest.getUserRole();
        ThrowUtils.throwIf(StringUtils.isAnyBlank(userAccount, userPassword, userRole), ErrorCode.PARAMS_ERROR);

        // 校验角色是否存在
        if (!UserRoleEnum.getValues().contains(userRole)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "角色不存在");
        }

        // 2. 密码加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        UserVO userVO = new UserVO();
        // 3.校验学生
        if (UserRoleEnum.STUDENT.getValue().equals(userRole)) {
            QueryWrapper<Student> studentQueryWrapper = new QueryWrapper<>();
            studentQueryWrapper.eq("userAccount", userAccount);
            studentQueryWrapper.eq("userPassword", encryptPassword);
            Student student = studentService.getOne(studentQueryWrapper);
            ThrowUtils.throwIf(student == null, ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
            BeanUtils.copyProperties(student, userVO);

        }

        // 4.校验教师/管理员
        if (UserRoleEnum.TEACHER.getValue().equals(userRole) || UserRoleEnum.ADMIN.getValue().equals(userRole)) {
            QueryWrapper<Teacher> teacherQueryWrapper = new QueryWrapper<>();
            teacherQueryWrapper.eq("userAccount", userAccount);
            teacherQueryWrapper.eq("userPassword", encryptPassword);
            Teacher teacher = teacherService.getOne(teacherQueryWrapper);
            ThrowUtils.throwIf(teacher == null, ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
            BeanUtils.copyProperties(teacher, userVO);

        }

        // 5.校验企业员工
        if (UserRoleEnum.ENTERPRISE.getValue().equals(userRole)) {
            QueryWrapper<Enterprise> enterpriseQueryWrapper = new QueryWrapper<>();
            enterpriseQueryWrapper.eq("userAccount", userAccount);
            enterpriseQueryWrapper.eq("userPassword", encryptPassword);
            Enterprise enterprise = enterpriseService.getOne(enterpriseQueryWrapper);
            ThrowUtils.throwIf(enterprise == null, ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
            BeanUtils.copyProperties(enterprise, userVO);
        }

        if(userVO.getId() == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "登录失败，请联系管理员");
        }

        // 非管理员需校验注册状态
        if (!UserRoleEnum.ADMIN.getValue().equals(userAccount)) {
            int status = userVO.getStatus();
            ThrowUtils.throwIf(status == RegisterStatusEnum.REJECTED.getValue(), ErrorCode.PARAMS_ERROR, "账号已被拒绝");
            ThrowUtils.throwIf(status == RegisterStatusEnum.PENDING.getValue(), ErrorCode.PARAMS_ERROR, "账号正在审核中");
        }

        request.getSession().setAttribute(USER_LOGIN_STATE, userVO);
        return userVO;
    }



    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public UserVO getLoginUser(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        UserVO currentUser = (UserVO) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        // 查询用户信息
        long userId = currentUser.getId();
        String userRole = currentUser.getUserRole();
        UserVO userDetails = userVOFactory.getUserDetails(userId, userRole);

        return userDetails;
    }



//    /**
//     * 是否为管理员
//     *
//     * @param request
//     * @return
//     */
//    @Override
//    public boolean isAdmin(HttpServletRequest request) {
//        // 仅管理员可查询
//        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
//        User user = (User) userObj;
//        return isAdmin(user);
//    }
//
//    @Override
//    public boolean isAdmin(User user) {
//        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
//    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }


}
