package com.jinzi.generator;

import cn.hutool.core.io.FileUtil;

/**
 * 静态文件生成器
 */
public class StaticGenerator {

    /**
     * 拷贝文件 会将输入目录完整拷贝到输入目录下
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }
}