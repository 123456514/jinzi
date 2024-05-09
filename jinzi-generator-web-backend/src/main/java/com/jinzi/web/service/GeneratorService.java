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

    void validGenerator(Generator generator, boolean add);
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);

    /**
     * 获取生成器封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    void cacheGenerators(List<Long> idList);

    List<Generator> getBatchByIds(List<Long> idList);

    void downloadGenerator(Generator generator, HttpServletResponse response) throws IOException;

    void makeGenerator(String zipFilePath, Meta meta, HttpServletResponse response);

    void onlineUseGenerator(Generator generator, Object dataModel, Long userId, HttpServletResponse response) throws IOException;
    List<Long> listHotGeneratorIds();
    Page<GeneratorVO> listGeneratorVOByPageSimplifyData(GeneratorQueryRequest generatorQueryRequest);
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage);
}
