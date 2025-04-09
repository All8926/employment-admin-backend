package com.app.project.controller;

import com.app.project.common.BaseResponse;
import com.app.project.common.ErrorCode;
import com.app.project.common.ResultUtils;
import com.app.project.exception.BusinessException;
import com.app.project.model.dto.postthumb.PostThumbAddRequest;
import com.app.project.model.entity.User;
import com.app.project.model.vo.LoginUserVO;
import com.app.project.service.PostThumbService;
import com.app.project.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 帖子点赞接口
 *
 * @author 
 * @from 
 */
@RestController
@RequestMapping("/post_thumb")
@Slf4j
public class PostThumbController {

    @Resource
    private PostThumbService postThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param postThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody PostThumbAddRequest postThumbAddRequest,
            HttpServletRequest request) {
        if (postThumbAddRequest == null || postThumbAddRequest.getPostId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        final LoginUserVO loginUser = userService.getLoginUser(request);
        final User user = new User();
        BeanUtils.copyProperties(loginUser, user);
        long postId = postThumbAddRequest.getPostId();
        int result = postThumbService.doPostThumb(postId, user);
        return ResultUtils.success(result);
    }

}
