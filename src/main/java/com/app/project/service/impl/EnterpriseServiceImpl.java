package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.BusinessException;
import com.app.project.mapper.EnterpriseMapper;
import com.app.project.model.dto.enterprise.EnterpriseAddRequest;
import com.app.project.model.dto.enterprise.EnterpriseQueryRequest;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.enums.RegisterStatusEnum;
import com.app.project.model.vo.EnterpriseVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.DepartmentService;
import com.app.project.service.EnterpriseService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【enterprise(企业员工)】的数据库操作Service实现
* @createDate 2025-04-10 21:00:47
*/
@Service
public class EnterpriseServiceImpl extends ServiceImpl<EnterpriseMapper, Enterprise>
    implements EnterpriseService {
    /**
     * 盐值，混淆密码
     */
    public static final String SALT = "xxx";


    @Resource
    private DepartmentService departmentService;


    /**
     * 获取查询条件
     *
     * @param enterpriseQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Enterprise> getQueryWrapper(EnterpriseQueryRequest enterpriseQueryRequest, UserVO loginUser) {
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<>();
        if (enterpriseQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = enterpriseQueryRequest.getId();
        String userName = enterpriseQueryRequest.getUserName();
        String enterpriseName = enterpriseQueryRequest.getEnterpriseName();
        String email = enterpriseQueryRequest.getEmail();
        String phone = enterpriseQueryRequest.getPhone();
        Integer gender = enterpriseQueryRequest.getGender();
        Integer status = enterpriseQueryRequest.getStatus();
        String userAccount = enterpriseQueryRequest.getUserAccount();
        String sortOrder = enterpriseQueryRequest.getSortOrder();
        String sortField = enterpriseQueryRequest.getSortField();
          Integer isAuthorized = enterpriseQueryRequest.getIsAuthorized();


        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(userName), "userName", userName);
        queryWrapper.like(StringUtils.isNotBlank(enterpriseName), "enterpriseName", enterpriseName);
        queryWrapper.like(StringUtils.isNotBlank(email), "email", email);
        queryWrapper.like(StringUtils.isNotBlank(phone), "phone", phone);
        queryWrapper.like(StringUtils.isNotBlank(userAccount), "userAccount", userAccount);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(gender), "gender", gender);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(isAuthorized), "isAuthorized", isAuthorized);


        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }



    /**
     * 获取学生信息封装
     *
     * @param enterprise
     * @param request
     * @return
     */
    @Override
    public EnterpriseVO getEnterpriseVO(Enterprise enterprise, HttpServletRequest request) {
        // 对象转封装类
        EnterpriseVO enterpriseVO = new EnterpriseVO();
        BeanUtils.copyProperties(enterprise, enterpriseVO);


        // endregion

        return enterpriseVO;
    }

    /**
     * 分页获取学生信息封装
     *
     * @param enterprisePage
     * @param request
     * @return
     */
    @Override
    public Page<EnterpriseVO> getEnterpriseVOPage(Page<Enterprise> enterprisePage, HttpServletRequest request) {
        List<Enterprise> enterpriseList = enterprisePage.getRecords();
        Page<EnterpriseVO> enterpriseVOPage = new Page<>(enterprisePage.getCurrent(), enterprisePage.getSize(), enterprisePage.getTotal());
        if (CollUtil.isEmpty(enterpriseList)) {
            return enterpriseVOPage;
        }
        // Enterprise => EnterpriseVO
        List<EnterpriseVO> enterpriseVOList = enterpriseList.stream().map(enterprise -> {
            EnterpriseVO enterpriseVO = new EnterpriseVO();
            BeanUtils.copyProperties(enterprise, enterpriseVO);
            return enterpriseVO;
        }).collect(Collectors.toList());

        enterpriseVOPage.setRecords(enterpriseVOList);
        return enterpriseVOPage;
    }

    @Override
    public boolean register(EnterpriseAddRequest enterpriseAddRequest) {
        Enterprise enterprise = new Enterprise();
        BeanUtils.copyProperties(enterpriseAddRequest, enterprise);
        String userAccount = enterprise.getUserAccount();

        // 单机锁，账号不能重复
        synchronized (userAccount.intern()) {
            // 1. 校验账号是否存在
            QueryWrapper<Enterprise> enterpriseQueryWrapper = new QueryWrapper<>();
            enterpriseQueryWrapper.eq("userAccount", userAccount);
            Enterprise enterpriseOne = this.getOne(enterpriseQueryWrapper);

            // 2. 存在 -> 校验账号状态
            if (enterpriseOne != null) {
                int enterpriseOneStatus = enterpriseOne.getStatus();
                if (enterpriseOneStatus == RegisterStatusEnum.REJECTED.getValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已被拒绝");
                }
                if (enterpriseOneStatus == RegisterStatusEnum.PENDING.getValue()) {
                    throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号正在审核中");
                }
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号已存在");
            }

            // 3.加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + enterprise.getUserPassword()).getBytes());
            enterprise.setUserPassword(encryptPassword);
            boolean saveResult = this.save(enterprise);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return true;
        }

    }
}




