package com.jinzi.web.model.vo;

import cn.hutool.json.JSONUtil;
import com.jinzi.web.meta.Meta;
import com.jinzi.web.model.entity.Generator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.List;

/**
 * 帖子视图
 *
 */
@Data
public class GeneratorVO implements Serializable {
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
     * 标签列表（json 数组）
     */
    private List<String> tags;

    /**
     * 图片
     */
    private String picture;

    /**
     * 文件配置（json字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 状态
     */
    private Integer status;

    private UserVO user;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     * 相当于对象转 json 字符串 把对象原本是json 字符串的对象转化成为json字符串
     * @param generatorVO
     * @return
     */
    public static Generator voToObj(GeneratorVO generatorVO) {
        if (generatorVO == null) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVO, generator);
        List<String> tagList = generatorVO.getTags();
        Meta.FileConfig fileConfig = generatorVO.getFileConfig();
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        Meta.ModelConfig modelConfig = generatorVO.getModelConfig();
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        generator.setTags(JSONUtil.toJsonStr(tagList));
        return generator;
    }

    /**
     * 对象转包装类
     *
     * @param generator
     * @return
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (generator == null) {
            return null;
        }
        GeneratorVO generatorVO = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVO);
        generatorVO.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVO.setModelConfig(JSONUtil.toBean(generator.getModelConfig(),Meta.ModelConfig.class));
        generatorVO.setFileConfig(JSONUtil.toBean(generator.getFileConfig(),Meta.FileConfig.class));
        return generatorVO;
    }
}
