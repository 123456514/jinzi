package com.jinzi.web.job.once;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jinzi.web.manager.CacheManager;
import com.jinzi.web.model.dto.generator.GeneratorQueryRequest;
import com.jinzi.web.model.vo.GeneratorVO;
import com.jinzi.web.service.GeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 首页数据缓存预热
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@Component
public class IndexPageCacheWarmupInitializer implements InitializingBean {

    @Resource
    private CacheManager cacheManager;

    @Resource
    private GeneratorService generatorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        GeneratorQueryRequest defaultReq = new GeneratorQueryRequest();
        defaultReq.setCurrent(1);
        defaultReq.setPageSize(12);
        defaultReq.setSortField("updateTime");
        defaultReq.setSortOrder("descend");
        String cacheKey = CacheManager.getPageCacheKey(defaultReq);

        Page<GeneratorVO> generatorVOPage = generatorService.listGeneratorVOByPageSimplifyData(
                defaultReq);

        cacheManager.put(cacheKey, generatorVOPage);
        log.info("index page cache warmup completed");
    }
}
