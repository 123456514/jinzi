package com.jinzi.web.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinzi.web.common.BaseResponse;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.common.ResultUtils;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.exception.ThrowUtils;
import com.jinzi.web.model.dto.generator.GeneratorQueryRequest;
import com.jinzi.web.model.dto.generatorfavour.GeneratorFavourAddRequest;
import com.jinzi.web.model.dto.generatorfavour.GeneratorFavourQueryRequest;
import com.jinzi.web.model.entity.Generator;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.vo.GeneratorVO;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.GeneratorFavourService;
import com.jinzi.web.service.GeneratorService;
import com.jinzi.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 生成器收藏接口
 *
 * @author zhoujn
 */
@RestController
@RequestMapping("/generator_favour")
@Slf4j
public class GeneratorFavourController {

    @Resource
    private GeneratorFavourService generatorFavourService;

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    /**
     * 收藏 / 取消收藏
     *
     * @param generatorFavourAddRequest
     * @param request
     * @return resultNum 收藏变化数
     */
    @PostMapping("/")
    public BaseResponse<Integer> doGeneratorFavour(@RequestBody GeneratorFavourAddRequest generatorFavourAddRequest,
                                                   HttpServletRequest request) {
        if (generatorFavourAddRequest == null || generatorFavourAddRequest.getGeneratorId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能操作
        final UserVO loginUser = userService.getLoginUser(request);
        long generatorId = generatorFavourAddRequest.getGeneratorId();
        int result = generatorFavourService.doGeneratorFavour(generatorId, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 获取我收藏的生成器列表
     *
     * @param generatorQueryRequest
     * @param request
     */
    @PostMapping("/my/list/page")
    public BaseResponse<Page<GeneratorVO>> listMyFavourGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                       HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        UserVO loginUser = userService.getLoginUser(request);
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorFavourService.listFavourGeneratorByPage(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest), loginUser.getId());
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 获取用户收藏的生成器列表
     *
     * @param generatorFavourQueryRequest
     * @param request
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<GeneratorVO>> listFavourGeneratorByPage(@RequestBody GeneratorFavourQueryRequest generatorFavourQueryRequest,
                                                                HttpServletRequest request) {
        if (generatorFavourQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        long current = generatorFavourQueryRequest.getCurrent();
        long size = generatorFavourQueryRequest.getPageSize();
        Long userId = generatorFavourQueryRequest.getUserId();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20 || userId == null, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorFavourService.listFavourGeneratorByPage(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorFavourQueryRequest.getGeneratorQueryRequest()), userId);
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }
}