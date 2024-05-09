package com.jinzi.web.model.vo;

import cn.hutool.json.JSONUtil;
import com.jinzi.web.meta.Meta;
import com.jinzi.web.model.entity.Generator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Objects;


@Data
public class GeneratorVO implements Serializable {

    /**
     * 创建人信息
     */
    private UserVO user;

    /**
     * 当前用户是否已点赞
     */
    private Boolean hasThumb;

    /**
     * 当前用户是否已收藏
     */
    private Boolean hasFavour;

    /**
     * id
     */
    private Long id;

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
     * git版本控制
     */
    private Boolean versionControl;

    /**
     * 强制交互式开关
     */
    private Boolean forcedInteractiveSwitch;

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
    private Meta.FileConfigDTO fileConfig;

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

    /**
     * 点赞数
     */
    private Integer thumbNum;

    /**
     * 收藏数
     */
    private Integer favourNum;

    /**
     * 创建时间
     */
    private Date  createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    private static final long serialVersionUID = 1L;

    /**
     * 包装类转对象
     */
    public static Generator voToObj(GeneratorVO generatorVO) {
        if (Objects.isNull(generatorVO)) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVO, generator);
        List<String> tagList = generatorVO.getTags();
        generator.setTags(tagList);
        Meta.FileConfigDTO fileConfig = generatorVO.getFileConfig();
        generator.setFileConfig(fileConfig);
        Meta.ModelConfig modelConfig = generatorVO.getModelConfig();
        generator.setModelConfig(modelConfig);
        return generator;
    }

    /**
     * 对象转包装类
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (Objects.isNull(generator)) {
            return null;
        }
        GeneratorVO generatorVO = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVO);
        generatorVO.setTags(generator.getTags());
        generatorVO.setFileConfig(generator.getFileConfig());
        generatorVO.setModelConfig(generator.getModelConfig());
        return generatorVO;
    }
}
