package com.app.project.service;


import com.app.project.model.dto.enterprise.EnterpriseAddRequest;
import com.app.project.model.dto.enterprise.EnterpriseQueryRequest;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.entity.Enterprise;
import com.app.project.model.vo.EnterpriseVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author Administrator
* @description 针对表【enterprise(企业员工)】的数据库操作Service
* @createDate 2025-04-10 21:00:47
*/
public interface EnterpriseService extends IService<Enterprise> {
    /**
     * 获取查询条件
     *
     * @param enterpriseQueryRequest
     * @return
     */
    QueryWrapper<Enterprise> getQueryWrapper(EnterpriseQueryRequest enterpriseQueryRequest, UserVO userVO);

    /**
     * 获取学生信息封装
     *
     * @param enterprise
     * @param request
     * @return
     */
    EnterpriseVO getEnterpriseVO(Enterprise enterprise, HttpServletRequest request);

    /**
     * 分页获取学生信息封装
     *
     * @param enterprisePage
     * @param request
     * @return
     */
    Page<EnterpriseVO> getEnterpriseVOPage(Page<Enterprise> enterprisePage, HttpServletRequest request);

    /**
     * 注册
     *
     * @param enterpriseAddRequest
     * @return
     */
    boolean register(EnterpriseAddRequest enterpriseAddRequest);


}
