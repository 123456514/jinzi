package com.jinzi.web.model.dto.generatorfavour;

import lombok.Data;

import java.io.Serializable;

/**
 * 生成器收藏 / 取消收藏请求
 *
 * @author zhoujin
 */
@Data
public class GeneratorFavourAddRequest implements Serializable {

    /**
     * 代码生成器 id
     */
    private Long GeneratorId;

    private static final long serialVersionUID = 1L;
}