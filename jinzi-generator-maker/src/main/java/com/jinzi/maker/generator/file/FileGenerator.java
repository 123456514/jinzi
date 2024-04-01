package com.jinzi.maker.generator.file;

import com.jinzi.maker.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class FileGenerator {
    public static void doGenerator(Object model) throws TemplateException, IOException {
        // 静态文件生成
        String projectPath = System.getProperty("user.dir");
        System.out.println(projectPath);
        String inputPath =   new File(projectPath).getParent() + File.separator +  "jinzi-generator-demo-projects" + File.separator + "acm-template";
        System.out.println(inputPath);
        String outputPath = projectPath;
        StaticFileGenerator.copyFilesByHutool(inputPath,outputPath);
        // 动态文件生成
        String dynamicInputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String dynamicOutputPath = projectPath + File.separator + "acm-template/src/com/jinzi/acm/MainTemplate.java";
        DynamicFileGenerator.doGenerate(dynamicInputPath,dynamicOutputPath,model);
    }
}
