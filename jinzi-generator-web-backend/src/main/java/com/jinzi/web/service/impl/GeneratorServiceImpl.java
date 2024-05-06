package com.jinzi.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jinzi.web.common.ErrorCode;
import com.jinzi.web.constant.CommonConstant;
import com.jinzi.web.exception.BusinessException;
import com.jinzi.web.exception.ThrowUtils;
import com.jinzi.web.manager.CosManager;
import com.jinzi.web.manager.LocalFileCacheManager;
import com.jinzi.web.mapper.GeneratorFavourMapper;
import com.jinzi.web.mapper.GeneratorMapper;
import com.jinzi.web.mapper.GeneratorThumbMapper;
import com.jinzi.web.mapstruct.GeneratorConvert;
import com.jinzi.web.model.dto.generator.GeneratorQueryRequest;
import com.jinzi.web.model.entity.Generator;
import com.jinzi.web.model.entity.GeneratorFavour;
import com.jinzi.web.model.entity.GeneratorThumb;
import com.jinzi.web.model.entity.User;
import com.jinzi.web.model.vo.GeneratorVO;
import com.jinzi.web.model.vo.UserVO;
import com.jinzi.web.service.GeneratorService;
import com.jinzi.web.service.UserService;
import com.jinzi.web.utils.SqlUtils;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
 */
@Service
@Slf4j
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator> implements GeneratorService {

    @Resource
    private UserService userService;

    @Resource
    private GeneratorThumbMapper generatorThumbMapper;

    @Resource
    private GeneratorFavourMapper generatorFavourMapper;

    @Resource
    private GeneratorMapper generatorMapper;

    @Resource
    private CosManager cosManager;


    private static final ExecutorService CLEAN_UP_POOL = new ThreadPoolExecutor(
            1,
            5,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> new Thread(r, "clean-up-thread"),
            new ThreadPoolExecutor.AbortPolicy()
    );

    private static final ExecutorService INCR_COUNT_POOL = new ThreadPoolExecutor(
            1,
            5,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(100),
            r -> new Thread(r, "clean-up-thread"),
            new ThreadPoolExecutor.AbortPolicy()
    );




    @Override
    public void validGenerator(Generator Generator, boolean add) {
        if (Generator == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String name = Generator.getName();
        List<String> tags = Generator.getTags();
        String description = Generator.getDescription();
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(name), ErrorCode.PARAMS_ERROR);
        }
        if(CollUtil.isEmpty(tags)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标签不可为空");
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(description) && description.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    /**
     * 获取查询包装类
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();
        Long id = generatorQueryRequest.getId();
        String title = generatorQueryRequest.getTitle();
        String content = generatorQueryRequest.getContent();
        List<String> tagList = generatorQueryRequest.getTags();
        Long userId = generatorQueryRequest.getUserId();
        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "name", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "description", content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    @Override
    public GeneratorVO getGeneratorVO(Generator Generator, HttpServletRequest request) {
        GeneratorVO generatorVO = GeneratorConvert.INSTANCE.convertGeneratorVOByGenerator(Generator);

        long generatorId = Generator.getId();
        // 1. 关联查询用户信息
        Long userId = Generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            // 获取点赞
            QueryWrapper<GeneratorThumb> generatorThumbQueryWrapper = new QueryWrapper<>();
            generatorThumbQueryWrapper.in("generatorId", generatorId);
            generatorThumbQueryWrapper.eq("userId", loginUser.getId());
            GeneratorThumb generatorThumb = generatorThumbMapper.selectOne(generatorThumbQueryWrapper);
            generatorVO.setHasThumb(generatorThumb != null);
            // 获取收藏
            QueryWrapper<GeneratorFavour> generatorFavourQueryWrapper = new QueryWrapper<>();
            generatorFavourQueryWrapper.in("generatorId", generatorId);
            generatorFavourQueryWrapper.eq("userId", loginUser.getId());
            GeneratorFavour generatorFavour = generatorFavourMapper.selectOne(generatorFavourQueryWrapper);
            generatorVO.setHasFavour(generatorFavour != null);
        }
        return generatorVO;
    }

    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 2. 已登录，获取用户点赞、收藏状态
        Map<Long, Boolean> generatorIdHasThumbMap = new HashMap<>();
        Map<Long, Boolean> generatorIdHasFavourMap = new HashMap<>();
        User loginUser = userService.getLoginUserPermitNull(request);
        if (loginUser != null) {
            Set<Long> generatorIdSet = generatorList.stream().map(Generator::getId).collect(Collectors.toSet());
            User login  = userService.getLoginUser(request);
            // 获取点赞
            QueryWrapper<GeneratorThumb> generatorThumbQueryWrapper = new QueryWrapper<>();
            generatorThumbQueryWrapper.in("generatorId", generatorIdSet);
            generatorThumbQueryWrapper.eq("userId", login.getId());
            List<GeneratorThumb> generatorGeneratorThumbList = generatorThumbMapper.selectList(generatorThumbQueryWrapper);
            generatorGeneratorThumbList.forEach(generatorGeneratorThumb -> generatorIdHasThumbMap.put(generatorGeneratorThumb.getGeneratorId(), true));
            // 获取收藏
            QueryWrapper<GeneratorFavour> generatorFavourQueryWrapper = new QueryWrapper<>();
            generatorFavourQueryWrapper.in("generatorId", generatorIdSet);
            generatorFavourQueryWrapper.eq("userId", login.getId());
            List<GeneratorFavour> generatorFavourList = generatorFavourMapper.selectList(generatorFavourQueryWrapper);
            generatorFavourList.forEach(generatorFavour -> generatorIdHasFavourMap.put(generatorFavour.getGeneratorId(), true));
        }
        // 填充信息
        List<GeneratorVO> generatorVOList = generatorList.stream().map(Generator -> {
            GeneratorVO generatorVO = GeneratorConvert.INSTANCE.convertGeneratorVOByGenerator(Generator);
            Long userId = Generator.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
            generatorVO.setHasThumb(generatorIdHasThumbMap.getOrDefault(Generator.getId(), false));
            generatorVO.setHasFavour(generatorIdHasFavourMap.getOrDefault(Generator.getId(), false));
            return generatorVO;
        }).collect(Collectors.toList());
        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }
    @Override
    public void cacheGenerators(List<Long> idList) {
        for (Long id : idList) {
            if (id <= 0) {
                continue;
            }
            log.info("cache generator, id = {}", id);
            Generator generator = this.getById(id);
            if (Objects.isNull(generator)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
            }
            String distPath = generator.getDistPath();
            if (StrUtil.isBlank(distPath)) {
                throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "产物包不存在");
            }

            String zipFilePath = LocalFileCacheManager.getCacheFilePath(id, distPath);

            if (FileUtil.exist(zipFilePath)) {
                FileUtil.del(zipFilePath);
            }
            FileUtil.touch(zipFilePath);

            try {
                cosManager.download(distPath, zipFilePath);
                // 给缓存设置过期时间
                LocalFileCacheManager.updateCacheExpiration(zipFilePath);
            } catch (InterruptedException e) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
            }
        }
    }
    @Override
    public List<Generator> getBatchByIds(List<Long> idList) {
        if (CollUtil.isEmpty(idList)) {
            return Collections.emptyList();
        }
        return generatorMapper.selectBatchIds(idList);
    }


    @Override
    public void downloadGenerator(Generator generator, HttpServletResponse response) throws IOException {
        String filepath = generator.getDistPath();
        Long id = generator.getId();
        // 设置响应头
        response.setContentType("application/octet-stream;charSet=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + filepath);

        // 查询本地缓存
        String zipFilePath = LocalFileCacheManager.getCacheFilePath(id, generator.getDistPath());
        if (FileUtil.exist(zipFilePath)) {
            log.info("download generator from cache, id = {}", id);
            // 从缓存下载
            Files.copy(Paths.get(zipFilePath), response.getOutputStream());
            return;
        }
        int lastIndex = filepath.lastIndexOf('/');
        int secondLastIndex = lastIndex != 0 ? filepath.lastIndexOf('/', lastIndex - 1) : -1;
        int thirdLastIndex = secondLastIndex != 0 ? filepath.lastIndexOf('/', secondLastIndex - 1) : -1;
        if (thirdLastIndex != -1) {
            filepath= filepath.substring(thirdLastIndex);
            System.out.println(filepath); // 输出类似: /path/to/file.txt
        } else {
            System.out.println("没有找到倒数第三个'/'或字符串中'/'的数量少于2个。");
        }

        // 从对象存储下载
        COSObjectInputStream cosObjectInput = null;
        try {
            COSObject cosObject = cosManager.getObject(filepath);
            cosObjectInput = cosObject.getObjectContent();
            // 处理下载到的流
            byte[] bytes = IOUtils.toByteArray(cosObjectInput);

            // 写入响应
            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (Exception e) {
            log.error("file download error, filepath = " + filepath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInput != null) {
                cosObjectInput.close();
            }
            CompletableFuture.runAsync(() ->incrDownloadCount(generator), INCR_COUNT_POOL);
        }
    }
    private void incrDownloadCount(Generator generator) {
        generator.setDownloadCount(generator.getDownloadCount() + 1);
        this.updateById(generator);
    }

    private void incrUseCount(Generator generator) {
        generator.setUseCount(generator.getUseCount() + 1);
        this.updateById(generator);
    }


}


