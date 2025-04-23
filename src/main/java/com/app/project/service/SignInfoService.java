package com.app.project.service;


import com.app.project.common.DeleteRequest;
import com.app.project.model.dto.signInfo.SignInfoAddRequest;
import com.app.project.model.dto.signInfo.SignInfoEditRequest;
import com.app.project.model.dto.signInfo.SignInfoQueryRequest;
import com.app.project.model.entity.SignInfo;
import com.app.project.model.vo.SignInfoVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【sign_info(签约信息表)】的数据库操作Service
* @createDate 2025-04-23 09:25:28
*/
public interface SignInfoService extends IService<SignInfo> {

    /**
     * 获取查询条件
     * @param signInfoQueryRequest
     * @param loginUser
     * @return
     */
    QueryWrapper<SignInfo> getQueryWrapper(SignInfoQueryRequest signInfoQueryRequest, UserVO loginUser);

    /**
     * 分页获取签约信息
     * @param signInfoPage
     * @param request
     * @return
     */
    Page<SignInfoVO> getSignInfoVOPage(Page<SignInfo> signInfoPage, HttpServletRequest request);

    /**
     * 添加签约信息
     * @param signInfoAddRequest
     * @param loginUser
     * @return
     */
    boolean addSignInfo(SignInfoAddRequest signInfoAddRequest, UserVO loginUser);

    /**
     * 修改签约信息
     * @param signInfoEditRequest
     * @param loginUser
     * @return
     */
    boolean editSignInfo(SignInfoEditRequest signInfoEditRequest, UserVO loginUser);

    /**
     * 删除签约信息
     * @param deleteRequest
     * @param loginUser
     * @return
     */
    boolean removeSignInfoById(DeleteRequest deleteRequest, UserVO loginUser);
}
