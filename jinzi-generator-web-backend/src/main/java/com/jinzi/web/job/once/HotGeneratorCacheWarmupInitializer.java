package com.jinzi.web.job.once;


import com.jinzi.web.service.GeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 热点代码生成器缓存预热
 *
 */
@Slf4j
@Component
public class HotGeneratorCacheWarmupInitializer implements InitializingBean {

    @Resource
    private GeneratorService generatorService;

    @Override
    public void afterPropertiesSet() throws Exception {
        // 缓存热点代码生成器
        List<Long> idList = generatorService.listHotGeneratorIds();
        generatorService.cacheGenerators(idList);
        log.info("cache hot generator ids: {}", idList);
    }
}
