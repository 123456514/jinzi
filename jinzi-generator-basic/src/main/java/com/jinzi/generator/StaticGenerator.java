package com.jinzi.generator;

import cn.hutool.core.io.FileUtil;

import java.io.File;

/**
 * 静态文件生成器
 */
public class StaticGenerator {
    public static void main(String[] args) {
        // 在项目的根目录存放 拷贝之后生成的静态文件
        // 1. 获取整个项目的跟路径
        String projectPath = System.getProperty("user.dir");
        // 2. 为什么要在这里加一个 File.separator ？
        //      因为在不同的操作系统下 项目分层的时候 使用的分隔符是不一样的 在 unix中使用的是 '\' 在windows 中使用的是 '//'
        String inputPath =  projectPath + File.separator + "jinzi-generator-demo-projects" + File.separator + "acm-template";
        System.out.println(inputPath);
        String outputPath = projectPath;
        copyFilesByHutool(inputPath,outputPath);
    }

    /**
     * 拷贝文件 会将输入目录完整拷贝到输入目录下
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }
}