package com.jinzi.web.model.dto.generator;

import com.jinzi.web.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询请求
 *
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GeneratorQueryRequest extends PageRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 不根据这些id 查数据
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 至少有一个标签
     */
    private List<String> orTags;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;


    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;
    private Long userId;


    private static final long serialVersionUID = 1L;
}