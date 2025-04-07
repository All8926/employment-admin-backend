package com.app.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.app.project.model.dto.student.StudentQueryRequest;
import com.app.project.model.entity.Student;
import com.app.project.model.vo.StudentVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 学生信息服务
 *
 * @author
 * @from
 */
public interface StudentService extends IService<Student> {

    /**
     * 校验数据
     *
     * @param student
     * @param add 对创建的数据进行校验
     */
    void validStudent(Student student, boolean add);

    /**
     * 获取查询条件
     *
     * @param studentQueryRequest
     * @return
     */
    QueryWrapper<Student> getQueryWrapper(StudentQueryRequest studentQueryRequest);
    
    /**
     * 获取学生信息封装
     *
     * @param student
     * @param request
     * @return
     */
    StudentVO getStudentVO(Student student, HttpServletRequest request);

    /**
     * 分页获取学生信息封装
     *
     * @param studentPage
     * @param request
     * @return
     */
    Page<StudentVO> getStudentVOPage(Page<Student> studentPage, HttpServletRequest request);
}
