package com.jinzi.web.job.cycle;


import com.jinzi.web.service.GeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 热点代码生成器定时任务
 *
 * @author <a href="https://github.com/dingxinliang88">youyi</a>
 */
@Slf4j
@Component
public class HotGeneratorJobHandler {

    @Resource
    private GeneratorService generatorService;


    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨1点执行
    public void cacheHotGenerators() {
        List<Long> idList = generatorService.listHotGeneratorIds();
        generatorService.cacheGenerators(idList);
        log.info("cache hot generator ids: {}", idList);
    }
}
