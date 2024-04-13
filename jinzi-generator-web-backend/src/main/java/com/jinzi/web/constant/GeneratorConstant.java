package com.jinzi.web.constant;

/**
 * 生成器常量
 *
 */
public interface GeneratorConstant {


    /**
     * 使用次数阈值
     * <p>
     * 设置热点阈值，比如生成器的使用次数，通过定时任务和每次下载之后判断
     */
    int HOT_GENERATOR_USE_COUNT_THRESHOLD = 500;

    String CACHE_KEY_PREFIX = "generator:page:";

}
