package com.app.project.service;


import com.app.project.common.AuditRequest;
import com.app.project.common.DeleteRequest;
import com.app.project.model.dto.contract.ContractAddRequest;
import com.app.project.model.dto.contract.ContractEditRequest;
import com.app.project.model.dto.contract.ContractQueryRequest;
import com.app.project.model.entity.Contract;
import com.app.project.model.vo.ContractVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【contract(合同)】的数据库操作Service
* @createDate 2025-04-12 20:34:09
*/
public interface ContractService extends IService<Contract> {

    /**
     * 新增合同
     * @param contractAddRequest
     * @param loginUser
     * @return
     */
    boolean addContract(ContractAddRequest contractAddRequest, UserVO loginUser);

    /**
     * 修改合同
     * @param contractEditRequest
     * @param loginUser
     * @return
     */
    boolean editContract(ContractEditRequest contractEditRequest, UserVO loginUser);

    /**
     * 删除合同
     * @param deleteRequest
     * @param loginUser
     * @return
     */
    boolean removeContractById(DeleteRequest deleteRequest, UserVO loginUser);

    /**
     * 获取查询条件
     * @param contractQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<Contract> getQueryWrapper(ContractQueryRequest contractQueryRequest, UserVO loginUser);

    /**
     * 分页获取合同
     * @param contractPage
     * @param request
     * @return
     */
    Page<ContractVO> getContractVOPage(Page<Contract> contractPage, HttpServletRequest request);

    /**
     * 审核合同
     * @param contractAuditRequest
     * @param loginUser
     * @return
     */
    boolean auditContract(AuditRequest auditRequest, UserVO loginUser);
}
