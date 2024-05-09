package com.jinzi.web.controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.util.concurrent.RateLimiter;
import com.jinzi.maker.meta.Meta;
import com.jinzi.web.annotation.AuthCheck;
import com.jinzi.web.common.BaseResponse;
import com.jinzi.web.common.DeleteRequest;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.common.ResultUtils;
import com.jinzi.web.constant.UserConstant;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.exception.ThrowUtils;
import com.jinzi.web.manager.CacheManager;
import com.jinzi.web.manager.CosManager;
import com.jinzi.web.manager.LocalFileCacheManager;
import com.jinzi.web.mapstruct.GeneratorConvert;
import com.jinzi.web.model.dto.generator.*;
import com.jinzi.web.model.entity.Generator;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.entity.dishPathInfo;
import com.jinzi.web.model.vo.GeneratorVO;
import com.jinzi.web.service.GeneratorService;
import com.jinzi.web.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 生成器接口
 *
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;


    @Resource
    private CacheManager cacheManager;

    private static final RateLimiter USE_LIMITER = RateLimiter.create(10);
    private static final RateLimiter MAKE_LIMITER = RateLimiter.create(10);

    // region 增删改查

    /**
     * 创建
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        if (generatorAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean isFull = userService.reduceWalletBalance(loginUser.getId(), 10);
        if(!isFull){
            return ResultUtils.error(ErrorCode.NO_FULL);
        }
        String name = generatorAddRequest.getName();
        String description = generatorAddRequest.getDescription();
        String basePackage = generatorAddRequest.getBasePackage();
        String version = generatorAddRequest.getVersion();
        String author = generatorAddRequest.getAuthor();
        List<String> tags = generatorAddRequest.getTags();
        String picture = generatorAddRequest.getPicture();
        com.jinzi.web.meta.Meta.FileConfigDTO fileConfigDTO = generatorAddRequest.getFileConfigDTO();
        com.jinzi.web.meta.Meta.ModelConfig modelConfig = generatorAddRequest.getModelConfig();
        dishPathInfo distPath = generatorAddRequest.getDistPath();
        Integer status = generatorAddRequest.getStatus();

        Generator generator = new Generator();
        generator.setName(name);
        generator.setDescription(description);
        generator.setBasePackage(basePackage);
        generator.setVersion(version);
        generator.setAuthor(author);
        generator.setTags(tags);
        generator.setPicture(picture);
        generator.setFileConfig(fileConfigDTO);
        generator.setModelConfig(modelConfig);
        generator.setDistPath(distPath.getUrl());
        generator.setStatus(status);


        generatorService.validGenerator(generator, true);

        generator.setUserId(loginUser.getId());
        generator.setFavourNum(0);
        generator.setThumbNum(0);
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = generatorService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = GeneratorConvert.INSTANCE.convertGeneratorByUpdateRequest(generatorUpdateRequest);
        // 参数校验
        generatorService.validGenerator(generator, false);
        long id = generatorUpdateRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取列表（仅管理员）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @return
     */
    @Deprecated
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size), generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page/vo/v2")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPageSimplifyData(
            @RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);

        // 优先从缓存获取
        String cacheKey = CacheManager.getPageCacheKey(generatorQueryRequest);
        Object cache = cacheManager.get(cacheKey);
        if (Objects.nonNull(cache)) {
            // noinspection unchecked
            return ResultUtils.success((Page<GeneratorVO>) cache);
        }
        Page<GeneratorVO> generatorVOPage = generatorService.listGeneratorVOByPageSimplifyData(
                generatorQueryRequest);
        // 写入缓存
        cacheManager.put(cacheKey, generatorVOPage);
        return ResultUtils.success(generatorVOPage);
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
            HttpServletRequest request) {
        if (generatorQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage));
    }



    /**
     * 编辑（用户）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Generator generator = GeneratorConvert.INSTANCE.convertGeneratorByEditRequest(generatorEditRequest);
        // 参数校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        long id = generatorEditRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = generatorService.updateById(generator);
        return ResultUtils.success(result);
    }

    /**
     * 下载代码生成器
     * @param id
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/download")
    public void downloadGeneratorById(Long id, HttpServletRequest request,
                                      HttpServletResponse response) throws IOException {
        if (id == null || id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        userService.reduceWalletBalance(loginUser.getId(), 10);
        Generator generator = generatorService.getById(id);
        if (generator == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }

        String filepath = generator.getDistPath();
        if (StrUtil.isBlank(filepath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包路径不存在");
        }

        // 追踪事件
        log.info("user {} download {}", loginUser, filepath);

        generatorService.downloadGenerator(generator, response);
    }
    /**
     * 在线使用生成器
     *
     * @param generatorUseRequest
     * @param request
     * @param response
     */
    @PostMapping("/useGenerator")
    public void onlineUseGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (!USE_LIMITER.tryAcquire()) {
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
        // 获取用户的输入参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();
        if (ObjectUtil.isEmpty(dataModel)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 需要用户登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId: {} use generator, generatorId: {}", loginUser.getId(), id);

        // 获取到生成器存储路径
        Generator generator = generatorService.getById(id);
        if (Objects.isNull(generator)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        String distPath = generator.getDistPath();
        if (StrUtil.isBlank(distPath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
        }

        generatorService.onlineUseGenerator(generator, dataModel, loginUser.getId(), response);
    }

    /**
     * 制作代码生成器
     *
     * @param generatorMakeRequest 请求参数
     * @param request              请求对象上下文
     * @param response             响应对象上下文
     */
    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 1）输入参数
        String zipFilePath = generatorMakeRequest.getZipFilePath();
        Meta meta = generatorMakeRequest.getMeta();

        // 需要登录
        User loginUser = userService.getLoginUser(request);

        // 2）创建独立工作空间，下载压缩包到本地
        if (StrUtil.isBlank(zipFilePath)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "压缩包不存在");
        }
        generatorService.makeGenerator(zipFilePath,meta,response);
    }


    /**
     * 缓存代码生成器
     */
    @PostMapping("/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void cacheGenerator(@RequestBody GeneratorCacheRequest generatorCacheRequest) {
        List<Long> idList = generatorCacheRequest.getIdList();
        if (CollUtil.isEmpty(idList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        generatorService.cacheGenerators(idList);
    }

    /**
     * 删除缓存
     * @param generatorDelCacheRequest
     */
    @DeleteMapping("/del/cache")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public void delCache(@RequestBody GeneratorDelCacheRequest generatorDelCacheRequest) {
        if (Objects.isNull(generatorDelCacheRequest) || CollUtil.isEmpty(
                generatorDelCacheRequest.getIds())) {
            LocalFileCacheManager.clearExpireCache();
            return;
        }

        List<Long> ids = generatorDelCacheRequest.getIds();
        List<Generator> generatorList = generatorService.getBatchByIds(ids);
        List<String> cacheKeyList = generatorList.stream()
                .filter(generator -> StrUtil.isNotBlank(generator.getDistPath()))
                .map(generator -> LocalFileCacheManager.getCacheFilePath(generator.getId(),
                        generator.getDistPath()))
                .collect(Collectors.toList());
        LocalFileCacheManager.clearCache(cacheKeyList);
    }
}
