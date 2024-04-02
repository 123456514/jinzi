package com.jinzi.maker.model;

import lombok.Data;

@Data
public class DataModel {
    // 1. 在代码开头 添加作者 @author 注释 添加代码
    // 2. 修改程序输出的信息提示(替换代码)
    // 3. 将循环读取输入改为单次读取（可选代码）


    /**
     * 是否循环 相当于一个开关
     */
    private boolean loop;
    /**
     * 核心模板
     */
    public MainTemplate mainTemplate;
    @Data
    public static class MainTemplate{
        /**
         * 注释中 作者名称的替换
         */
        private String author = "jinzi";
        /**
         * 代码中输出值的替换
         */
        private String outputText = "输出结果";
    }
}
