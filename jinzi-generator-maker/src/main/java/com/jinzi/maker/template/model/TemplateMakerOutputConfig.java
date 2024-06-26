package com.jinzi.maker.template.model;

import lombok.Data;

/**
 * 输出规则配置
 */
@Data
public class TemplateMakerOutputConfig {

    /**
     * 是否从未分组文件配置中移除和已分组文件同名的文件配置
     */
    private boolean removeGroupFileFromRoot = true;

    /**
     * 多个生成条件下 将文件移出组内 放在外层
     */
    private boolean removeFileFromGroup = true;


}
