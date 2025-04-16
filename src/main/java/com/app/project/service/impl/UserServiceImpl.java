package com.app.project.service.impl;

import com.app.project.common.ErrorCode;
import com.app.project.constant.UserConstant;
import com.app.project.exception.BusinessException;
import com.app.project.exception.ThrowUtils;
import com.app.project.model.dto.user.UserAuditRequest;
import com.app.project.model.dto.user.UserLoginRequest;
import com.app.project.model.entity.AuditLog;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.entity.Student;
import com.app.project.model.entity.Teacher;
import com.app.project.model.enums.AuditResultEnum;
import com.app.project.model.enums.AuditTargetTypeEnum;
import com.app.project.model.enums.RegisterStatusEnum;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.UserVO;
import com.app.project.service.*;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static com.app.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现
 *
 * @author
 * @from
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

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

    @Resource
    private AuditLogService auditLogService;

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

        if (userVO.getId() == null) {
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


    /**
     * 用户注销
     *
     * @param request
     */
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean auditUser(UserAuditRequest userAuditRequest, UserVO loginUser) {
        List<String> userRoleList = UserRoleEnum.getValues();
        String userRole = userAuditRequest.getUserRole();
        ThrowUtils.throwIf(!userRoleList.contains(userRole), ErrorCode.PARAMS_ERROR, "角色不存在");
        Integer status = userAuditRequest.getStatus();

        boolean auditResult = false;
        // 审核教师
        if (UserRoleEnum.TEACHER.getValue().equals(userRole)) {
            // 教师只能管理员审核
            if (!UserConstant.ADMIN_ROLE.equals(loginUser.getUserRole())) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            Teacher teacher = new Teacher();
            teacher.setId(userAuditRequest.getId());
            // 通过
            if (status.equals(AuditResultEnum.RESOLVED.getValue())) {
                teacher.setStatus(RegisterStatusEnum.RESOLVED.getValue());
            }
            // 拒绝
            if (status.equals(AuditResultEnum.REJECTED.getValue())) {
                teacher.setStatus(RegisterStatusEnum.REJECTED.getValue());
            }
            auditResult = teacherService.updateById(teacher);
        }
        // 审核学生
        if (UserRoleEnum.STUDENT.getValue().equals(userRole)) {
            Student student = new Student();
            student.setId(userAuditRequest.getId());
            if (status.equals(AuditResultEnum.RESOLVED.getValue())) {
                student.setStatus(RegisterStatusEnum.RESOLVED.getValue());
            }
            if (status.equals(AuditResultEnum.REJECTED.getValue())) {
                student.setStatus(RegisterStatusEnum.REJECTED.getValue());
            }
            auditResult = studentService.updateById(student);
        }

        // 审核企业
        if (UserRoleEnum.ENTERPRISE.getValue().equals(userRole)) {
            Enterprise enterprise = new Enterprise();
            enterprise.setId(userAuditRequest.getId());
            // 通过
            if (status.equals(AuditResultEnum.RESOLVED.getValue())) {
                enterprise.setStatus(RegisterStatusEnum.RESOLVED.getValue());
            }
            // 拒绝
            if (status.equals(AuditResultEnum.REJECTED.getValue())) {
                enterprise.setStatus(RegisterStatusEnum.REJECTED.getValue());
            }
            auditResult = enterpriseService.updateById(enterprise);
        }
        ThrowUtils.throwIf(!auditResult, ErrorCode.OPERATION_ERROR, "审核失败");

        // 添加审核记录
        AuditLog auditLog = new AuditLog();
        auditLog.setUserId(loginUser.getId());
        auditLog.setUserName(loginUser.getUserName());
        auditLog.setTargetType(AuditTargetTypeEnum.REGISTER.getValue());
        auditLog.setTargetId(userAuditRequest.getId());
        auditLog.setTargetName(userAuditRequest.getUserAccount());
        auditLog.setStatus(userAuditRequest.getStatus());
        auditLog.setRejectReason(userAuditRequest.getRejectReason());
        auditLogService.addAuditLog(auditLog);
        return true;
    }


}
