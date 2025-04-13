package com.app.project.service;


import com.app.project.common.AuditRequest;
import com.app.project.common.DeleteRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationAddRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationEditRequest;
import com.app.project.model.dto.enterpriseCertification.EnterpriseCertificationQueryRequest;
import com.app.project.model.entity.EnterpriseCertification;
import com.app.project.model.vo.EnterpriseCertificationVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【enterprise_certification(企业资质)】的数据库操作Service
* @createDate 2025-04-12 13:07:27
*/
public interface EnterpriseCertificationService extends IService<EnterpriseCertification> {

    /**
     * 添加企业资质
     *
     * @param enterpriseCertificationAddRequest
     * @param loginUser
     * @return
     */
    boolean addEnterpriseCertification(EnterpriseCertificationAddRequest enterpriseCertificationAddRequest, UserVO loginUser);

    /**
     * 修改企业资质
     *
     * @param enterpriseCertificationEditRequest
     * @param loginUser
     * @return
     */
    boolean editEnterpriseCertification(EnterpriseCertificationEditRequest enterpriseCertificationEditRequest, UserVO loginUser);

    /**
     * 删除企业资质
     * @param deleteRequest
     * @param loginUser
     * @return
     */
    boolean removeEnterpriseCertificationById(DeleteRequest deleteRequest, UserVO loginUser);

    /**
     * 获取查询条件
     * @param enterpriseCertificationQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<EnterpriseCertification> getQueryWrapper(EnterpriseCertificationQueryRequest enterpriseCertificationQueryRequest, UserVO loginUser);

    /**
     * 分页获取列表
     * @param enterpriseCertificationPage
     * @param request
     * @return
     */
    Page<EnterpriseCertificationVO> getEnterpriseCertificationVOPage(Page<EnterpriseCertification> enterpriseCertificationPage, HttpServletRequest request);

    /**
     * 审核企业资质
     * @param enterpriseCertificationAuditRequest
     * @param loginUser
     * @return
     */
    boolean auditEnterpriseCertification(AuditRequest auditRequest, UserVO loginUser);
}
