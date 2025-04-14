package com.app.project.service;


import com.app.project.model.dto.teacher.TeacherAddRequest;
import com.app.project.model.dto.teacher.TeacherQueryRequest;
import com.app.project.model.entity.Teacher;
import com.app.project.model.vo.TeacherVO;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;

/**
* @author Administrator
* @description 针对表【teacher(教师)】的数据库操作Service
* @createDate 2025-04-08 19:01:36
*/
public interface TeacherService extends IService<Teacher> {


    /**
     * 创建教师信息
     *
     * @param teacherAddRequest
     * @return
     */
    Boolean registerTeacher(TeacherAddRequest teacherAddRequest);

    /**
     * 获取查询条件
     *
     * @param teacherQueryRequest
     * @return
     */
    QueryWrapper<Teacher> getQueryWrapper(TeacherQueryRequest teacherQueryRequest);

    /**
     * 分页获取教师信息封装
     *
     * @param teacherPage
     * @param request
     * @return
     */
    Page<TeacherVO> getTeacherVOPage(Page<Teacher> teacherPage, HttpServletRequest request);
}
