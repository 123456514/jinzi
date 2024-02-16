package com.jinzi.generator;

import com.jinzi.model.MainTemplateConfig;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void doGenerator(Object model) throws TemplateException, IOException {
        // 静态文件生成
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String inputPath =   new File(projectPath).getParent() + File.separator +  "jinzi-generator-demo-projects" + File.separator + "acm-template";
        System.out.println(inputPath);
        String outputPath = projectPath;
        StaticGenerator.copyFilesByRecursive(inputPath,outputPath);
        // 动态文件生成
        String dynamicInputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/jinzi/acm/MainTemplate.java";
        DynamicGenerator.doGenerator(dynamicInputPath,dynamicOutputPath,model);
    }

    public static void main(String[] args) throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("jinzi");
        mainTemplateConfig.setOutputText("总和为：");
        mainTemplateConfig.setLoop(true);
        doGenerator(mainTemplateConfig);
    }
}
