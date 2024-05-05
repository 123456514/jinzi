package com.jinzi.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 缓存代码生成器请求
 *
 *
 */
@Data
public class GeneratorCacheRequest implements Serializable {


    /**
     * 生成器 ID
     */
    private List<Long> idList;

    private static final long serialVersionUID = 1L;
}
