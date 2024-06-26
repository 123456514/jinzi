package com.jinzi.web.controller;


import com.jinzi.web.common.BaseResponse;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.common.ResultUtils;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.model.dto.generatorthumb.GeneratorThumbAddRequest;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.GeneratorThumbService;
import com.jinzi.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 代码生成器点赞接口
 *
 * @author zhoujin
 */
@RestController
@RequestMapping("/generator_thumb")
@Slf4j
public class GeneratorThumbController {

    @Resource
    private GeneratorThumbService generatorThumbService;

    @Resource
    private UserService userService;

    /**
     * 点赞 / 取消点赞
     *
     * @param generatorThumbAddRequest
     * @param request
     * @return resultNum 本次点赞变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody GeneratorThumbAddRequest generatorThumbAddRequest,
                                         HttpServletRequest request) {
        if (generatorThumbAddRequest == null || generatorThumbAddRequest.getGeneratorId() <= 0L) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能点赞
        User loginUser = userService.getLoginUser(request);
        long generatorId = generatorThumbAddRequest.getGeneratorId();
        int result = generatorThumbService.doGeneratorThumb(generatorId, loginUser);
        return ResultUtils.success(result);
    }

}