package com.jinzi.web.job.cycle;

import cn.hutool.core.util.StrUtil;


import com.jinzi.web.manager.CosManager;
import com.jinzi.web.mapper.GeneratorMapper;
import com.jinzi.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 清除 COS 服务中的文件
 *
 */
@Component
@Slf4j
public class ClearCosJobHandler {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() {
        log.info("clear cos job handler start...");

        // 1. 包括用户上传的模板制作文件（`generator_make_template`）
        // TODO 思考问题：定时批量删除模板制作文件会不会有什么问题？比如刚上传就删了。考虑设置文件保护期
        cosManager.deleteDir("/generator_make_template/");
        // 2. 已删除的代码生成器对应的产物包文件（`generator_dist`）
        List<Generator> generatorList = generatorMapper.listDeletedGenerator();
        List<String> keyList = generatorList.stream().map(Generator::getDistPath)
                .filter(StrUtil::isNotBlank)
                // 去除 / 前缀
                .map(path -> path.substring(1))
                .collect(Collectors.toList());
        cosManager.deleteObjects(keyList);
        log.info("clear cos job handler stop...");
    }

}
