package com.jinzi.web.model.dto.generatorfavour;


import com.jinzi.web.common.PageRequest;
import com.jinzi.web.model.dto.generator.GeneratorQueryRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 代码生成器收藏查询请求
 *
 * @author zhoujin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class GeneratorFavourQueryRequest extends PageRequest implements Serializable {

    /**
     * 代码生成器查询请求
     */
    private GeneratorQueryRequest generatorQueryRequest;

    /**
     * 用户 id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}