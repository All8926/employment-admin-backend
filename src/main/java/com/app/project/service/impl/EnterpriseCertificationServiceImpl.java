package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.excel.util.StringUtils;
import com.app.project.common.AuditRequest;
import com.app.project.common.DeleteRequest;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.EnterpriseCertificationMapper;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationAddRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationEditRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationQueryRequest;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.entity.EnterpriseCertification;
import com.app.project.model.enums.UserRoleEnum;
import com.app.project.model.vo.EnterpriseCertificationVO;
import com.app.project.model.vo.EnterpriseVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.EnterpriseCertificationService;
import com.app.project.service.EnterpriseService;
import com.app.project.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
* @author Administrator
* @description 针对表【enterprise_certification(企业资质)】的数据库操作Service实现
* @createDate 2025-04-12 13:07:27
*/
@Service
public class EnterpriseCertificationServiceImpl extends ServiceImpl<EnterpriseCertificationMapper, EnterpriseCertification>
    implements EnterpriseCertificationService {

    @Resource
    private EnterpriseService enterpriseService;

    @Override
    public boolean addEnterpriseCertification(EnterpriseCertificationAddRequest enterpriseCertificationAddRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        Long userId = loginUser.getId();
        EnterpriseCertification enterpriseCertification = new EnterpriseCertification();
        BeanUtils.copyProperties(enterpriseCertificationAddRequest, enterpriseCertification);
        // 设置创建人用户id
        enterpriseCertification.setUserId(userId);
        boolean result = this.save(enterpriseCertification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean editEnterpriseCertification(EnterpriseCertificationEditRequest enterpriseCertificationEditRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);

        // 只能修改自己的资质
        long userId = loginUser.getId();
        long enterpriseCertificationId = enterpriseCertificationEditRequest.getId();
        EnterpriseCertification oldEnterpriseCertification = this.getById(enterpriseCertificationId);
        ThrowUtils.throwIf(oldEnterpriseCertification == null, ErrorCode.NOT_FOUND_ERROR, "资质不存在");
        ThrowUtils.throwIf(userId != oldEnterpriseCertification.getUserId(), ErrorCode.NO_AUTH_ERROR, "无权限");


        EnterpriseCertification enterpriseCertification = new EnterpriseCertification();
        BeanUtils.copyProperties(enterpriseCertificationEditRequest, enterpriseCertification);
        boolean result = this.updateById(enterpriseCertification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public boolean removeEnterpriseCertificationById(DeleteRequest deleteRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        // 只能修改自己的资质
        long userId = loginUser.getId();
        long enterpriseCertificationId = deleteRequest.getId();
        EnterpriseCertification oldEnterpriseCertification = this.getById(enterpriseCertificationId);
        ThrowUtils.throwIf(oldEnterpriseCertification == null, ErrorCode.NOT_FOUND_ERROR, "资质不存在");
        ThrowUtils.throwIf(userId != oldEnterpriseCertification.getUserId(), ErrorCode.NO_AUTH_ERROR);

        boolean result = this.removeById(enterpriseCertificationId);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }

    @Override
    public QueryWrapper<EnterpriseCertification> getQueryWrapper(EnterpriseCertificationQueryRequest enterpriseCertificationQueryRequest, UserVO loginUser) {
        QueryWrapper<EnterpriseCertification> queryWrapper = new QueryWrapper<>();
        if (enterpriseCertificationQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = enterpriseCertificationQueryRequest.getId();
        String fileName = enterpriseCertificationQueryRequest.getFileName();
        String remark = enterpriseCertificationQueryRequest.getRemark();
        String certType = enterpriseCertificationQueryRequest.getCertType();
        Integer status = enterpriseCertificationQueryRequest.getStatus();
        String sortOrder = enterpriseCertificationQueryRequest.getSortOrder();
        String sortField = enterpriseCertificationQueryRequest.getSortField();

        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(fileName), "fileName", fileName);
        queryWrapper.like(StringUtils.isNotBlank(remark), "remark", remark);
        queryWrapper.like(StringUtils.isNotBlank(certType), "certType", certType);

        // 精确查询
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);

        String userRole = loginUser.getUserRole();
        long userId = loginUser.getId();

        // 企业只能查询自己的
        if (UserRoleEnum.ENTERPRISE.getValue().equals(userRole)) {
            queryWrapper.eq("userId", userId);
        }

        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public Page<EnterpriseCertificationVO> getEnterpriseCertificationVOPage(Page<EnterpriseCertification> enterpriseCertificationPage, HttpServletRequest request) {
        List<EnterpriseCertification> enterpriseCertificationList = enterpriseCertificationPage.getRecords();
        Page<EnterpriseCertificationVO> enterpriseCertificationVOPage = new Page<>(enterpriseCertificationPage.getCurrent(), enterpriseCertificationPage.getSize(), enterpriseCertificationPage.getTotal());
        if (CollUtil.isEmpty(enterpriseCertificationList)) {
            return enterpriseCertificationVOPage;
        }
        // EnterpriseCertification => EnterpriseCertificationVO
        List<EnterpriseCertificationVO> enterpriseCertificationVOList = enterpriseCertificationList.stream().map(enterpriseCertification -> {
            EnterpriseCertificationVO enterpriseCertificationVO = new EnterpriseCertificationVO();
            BeanUtils.copyProperties(enterpriseCertification, enterpriseCertificationVO);
            return enterpriseCertificationVO;
        }).collect(Collectors.toList());

        // 1.取出所有企业信息的id
        final Set<Long> userIds = enterpriseCertificationList.stream().map(EnterpriseCertification::getUserId).collect(Collectors.toSet());
        // 2.批量查询企业信息 返回 enterpriseId -> Enterprise
        Map<Long, List<Enterprise>> userIdDepartmentListMap = enterpriseService.listByIds(userIds).stream().collect(Collectors.groupingBy(Enterprise::getId));
        // 3.填充企业信息
        enterpriseCertificationVOList.forEach(enterpriseCertificationVO -> {
            List<Enterprise> enterprises = userIdDepartmentListMap.get(enterpriseCertificationVO.getUserId());
            if (CollUtil.isNotEmpty(enterprises) && enterprises.size() != 0) {
                  EnterpriseVO enterpriseVO = new EnterpriseVO();
                BeanUtils.copyProperties(enterprises.get(0), enterpriseVO);
                enterpriseCertificationVO.setEnterprise(enterpriseVO);
            }
        });

        enterpriseCertificationVOPage.setRecords(enterpriseCertificationVOList);
        return enterpriseCertificationVOPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean auditEnterpriseCertification(AuditRequest auditRequest, UserVO loginUser) {
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN_ERROR);
        int requestStatus = auditRequest.getStatus();
        ThrowUtils.throwIf(requestStatus != 0 && requestStatus != 1, ErrorCode.PARAMS_ERROR);
        // 查询资质
        EnterpriseCertification enterpriseCertification = this.getById(auditRequest.getId());
        ThrowUtils.throwIf(enterpriseCertification == null, ErrorCode.NOT_FOUND_ERROR, "资质不存在");

        // 拒绝的话需要填写拒绝原因
        String rejectReason = auditRequest.getRejectReason();
        ThrowUtils.throwIf(requestStatus == 0 && StringUtils.isBlank(rejectReason), ErrorCode.PARAMS_ERROR, "拒绝原因不能为空");
        //拒绝
        if(requestStatus == 0){
            enterpriseCertification.setStatus(2);
            enterpriseCertification.setRejectReason(rejectReason);
        }
        // 通过
        if(requestStatus == 1){
            enterpriseCertification.setStatus(1);

            // 企业未认证的话只要资质通过就设置为已认证
            Enterprise enterprise = enterpriseService.getById(enterpriseCertification.getUserId());
            int isAuthorized = enterprise.getIsAuthorized();
            if(isAuthorized == 0){
                enterprise.setIsAuthorized(1);
                enterpriseService.updateById(enterprise);
            }
        }
        boolean result = this.updateById(enterpriseCertification);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return true;
    }
}




