package com.jinzi.maker.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author zhang
 * @date 2023/11/10 10:33
 * 核心生成器
 */
public class FileGenerator {

    /**
     * 核心生成器
     * @param model model数据模型
     * @throws IOException IOException
     * @throws TemplateException TemplateException
     */
    public static void doGenerate(Object model) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        // 输入路径
        String inputPath = new File(parentFile, "jinzi-generator-demo-projects/acm-template").getAbsolutePath();
        // 生成静态文件
        StaticFileGenerator.copyFilesByHuTool(inputPath, projectPath);
        // 生成动态文件
        String dynamicInputPath = "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/azhang/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerate(dynamicInputPath, dynamicOutputPath,model);
    }


}
