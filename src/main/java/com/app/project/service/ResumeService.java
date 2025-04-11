package com.app.project.service;


import com.app.project.common.DeleteRequest;
import com.app.project.model.dto.resume.ResumeAddRequest;
import com.app.project.model.dto.resume.ResumeEditRequest;
import com.app.project.model.dto.resume.ResumeQueryRequest;
import com.app.project.model.dto.student.StudentQueryRequest;
import com.app.project.model.entity.Resume;
import com.app.project.model.entity.Student;
import com.app.project.model.vo.ResumeVO;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【resume(简历)】的数据库操作Service
* @createDate 2025-04-11 20:09:44
*/
public interface ResumeService extends IService<Resume> {

    /**
     * 新增简历
     * @param resumeAddRequest
     * @param loginUser
     * @return
     */
    boolean addResume(ResumeAddRequest resumeAddRequest, UserVO loginUser);

    /**
     * 修改简历
     * @param resumeEditRequest
     * @param loginUser
     * @return
     */
    boolean editResume(ResumeEditRequest resumeEditRequest, UserVO loginUser);

    /**
     * 删除简历
     * @param deleteRequest
     * @param loginUser
     * @return
     */
    boolean removeResumeById(DeleteRequest deleteRequest, UserVO loginUser);

    /**
     * 获取查询条件
     *
     * @param resumeQueryRequest
     * @return
     */
    QueryWrapper<Resume> getQueryWrapper(ResumeQueryRequest resumeQueryRequest, UserVO userVO);

    /**
     * 分页获取简历
     * @param resumePage
     * @param request
     * @return
     */
    Page<ResumeVO> getResumeVOPage(Page<Resume> resumePage, HttpServletRequest request);
}
