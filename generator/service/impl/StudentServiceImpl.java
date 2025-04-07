package com.app.project.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.app.project.common.ErrorCode;
import com.app.project.constant.CommonConstant;
import com.app.project.exception.ThrowUtils;
import com.app.project.mapper.StudentMapper;
import com.app.project.model.dto.student.StudentQueryRequest;
import com.app.project.model.entity.Student;
import com.app.project.model.entity.StudentFavour;
import com.app.project.model.entity.StudentThumb;
import com.app.project.model.entity.User;
import com.app.project.model.vo.StudentVO;
import com.app.project.model.vo.UserVO;
import com.app.project.service.StudentService;
import com.app.project.service.UserService;
import com.app.project.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 学生信息服务实现
 *
 * @author
 * @from
 */
@Service
@Slf4j
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param student
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validStudent(Student student, boolean add) {
        ThrowUtils.throwIf(student == null, ErrorCode.PARAMS_ERROR);
        // todo 从对象中取值
        String title = student.getTitle();
        // 创建数据时，参数不能为空
        if (add) {
            // todo 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(title), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // todo 补充校验规则
        if (StringUtils.isNotBlank(title)) {
            ThrowUtils.throwIf(title.length() > 80, ErrorCode.PARAMS_ERROR, "标题过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param studentQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Student> getQueryWrapper(StudentQueryRequest studentQueryRequest) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        if (studentQueryRequest == null) {
            return queryWrapper;
        }
        // todo 从对象中取值
        Long id = studentQueryRequest.getId();
        Long notId = studentQueryRequest.getNotId();
        String title = studentQueryRequest.getTitle();
        String content = studentQueryRequest.getContent();
        String searchText = studentQueryRequest.getSearchText();
        String sortField = studentQueryRequest.getSortField();
        String sortOrder = studentQueryRequest.getSortOrder();
        List<String> tagList = studentQueryRequest.getTags();
        Long userId = studentQueryRequest.getUserId();
        // todo 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("title", searchText).or().like("content", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取学生信息封装
     *
     * @param student
     * @param request
     * @return
     */
    @Override
    public StudentVO getStudentVO(Student student, HttpServletRequest request) {
        // 对象转封装类
        StudentVO studentVO = StudentVO.objToVo(student);

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = student.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        studentVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        long studentId = student.getId();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<StudentThumb> studentThumbQueryWrapper = new QueryWrapper<>();
            studentThumbQueryWrapper.in("studentId", studentId);
            studentThumbQueryWrapper.eq("userId", loginUser.getId());
            StudentThumb studentThumb = studentThumbMapper.selectOne(studentThumbQueryWrapper);
            studentVO.setHasThumb(studentThumb != null);
            // 获取收藏
            QueryWrapper<StudentFavour> studentFavourQueryWrapper = new QueryWrapper<>();
            studentFavourQueryWrapper.in("studentId", studentId);
            studentFavourQueryWrapper.eq("userId", loginUser.getId());
            StudentFavour studentFavour = studentFavourMapper.selectOne(studentFavourQueryWrapper);
            studentVO.setHasFavour(studentFavour != null);
        }
        // endregion

        return studentVO;
    }

    /**
     * 分页获取学生信息封装
     *
     * @param studentPage
     * @param request
     * @return
     */
    @Override
    public Page<StudentVO> getStudentVOPage(Page<Student> studentPage, HttpServletRequest request) {
        List<Student> studentList = studentPage.getRecords();
        Page<StudentVO> studentVOPage = new Page<>(studentPage.getCurrent(), studentPage.getSize(), studentPage.getTotal());
        if (CollUtil.isEmpty(studentList)) {
            return studentVOPage;
        }
        // 对象列表 => 封装对象列表
        List<StudentVO> studentVOList = studentList.stream().map(student -> {
            return StudentVO.objToVo(student);
        }).collect(Collectors.toList());

        // todo 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = studentList.stream().map(Student::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> studentIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> studentIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> studentIdSet = studentList.stream().map(Student::getId).collect(Collectors.toSet());
            loginUser = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<StudentThumb> studentThumbQueryWrapper = new QueryWrapper<>();
            studentThumbQueryWrapper.in("studentId", studentIdSet);
            studentThumbQueryWrapper.eq("userId", loginUser.getId());
            List<StudentThumb> studentStudentThumbList = studentThumbMapper.selectList(studentThumbQueryWrapper);
            studentStudentThumbList.forEach(studentStudentThumb -> studentIdHasThumbMap.put(studentStudentThumb.getStudentId(), true));
            // 获取收藏
            QueryWrapper<StudentFavour> studentFavourQueryWrapper = new QueryWrapper<>();
            studentFavourQueryWrapper.in("studentId", studentIdSet);
            studentFavourQueryWrapper.eq("userId", loginUser.getId());
            List<StudentFavour> studentFavourList = studentFavourMapper.selectList(studentFavourQueryWrapper);
            studentFavourList.forEach(studentFavour -> studentIdHasFavourMap.put(studentFavour.getStudentId(), true));
        }
        // 填充信息
        studentVOList.forEach(studentVO -> {
            Long userId = studentVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            studentVO.setUser(userService.getUserVO(user));
            studentVO.setHasThumb(studentIdHasThumbMap.getOrDefault(studentVO.getId(), false));
            studentVO.setHasFavour(studentIdHasFavourMap.getOrDefault(studentVO.getId(), false));
        });
        // endregion

        studentVOPage.setRecords(studentVOList);
        return studentVOPage;
    }

}
