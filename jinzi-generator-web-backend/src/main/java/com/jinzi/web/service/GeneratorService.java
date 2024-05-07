package com.jinzi.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jinzi.maker.meta.Meta;
import com.jinzi.web.model.dto.generator.GeneratorQueryRequest;
import com.jinzi.web.model.entity.Generator;
import com.jinzi.web.model.vo.GeneratorVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 帖子服务
 *
 */
public interface GeneratorService extends IService<Generator> {

    /**
     * 校验
     *
     * @param generator
     * @param add
     */
    void validGenerator(Generator generator, boolean add);

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);



    /**
     * 获取帖子封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request);


    /**
     * 缓存生成器
     * @param idList
     */
    void cacheGenerators(List<Long> idList);

    /**
     * 批量获取
     *
     * @param idList
     * @return
     */
    List<Generator> getBatchByIds(List<Long> idList);

    /**
     * 下载生成器
     * @param generator
     * @param response
     * @throws IOException
     */
    void downloadGenerator(Generator generator, HttpServletResponse response) throws IOException;

    /**
     * 使用生成器
     */
    void useGenerator(String path, long generatorId, long loginUserId, Map<String, Object> dataModel, HttpServletResponse response);

    void makeGenerator(String zipFilePath, Meta meta, HttpServletResponse response);

    void onlineUseGenerator(Generator generator, Object dataModel, Long userId, HttpServletResponse response) throws IOException;
}
