package com.jinzi.generator;

import com.jinzi.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class DynamicGenerator {
    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir");
        String inputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "MainTemplate.java";
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        mainTemplateConfig.setAuthor("jinzi");
        mainTemplateConfig.setOutputText("总和为：");
        mainTemplateConfig.setLoop(false);
        doGenerator(inputPath,outputPath,mainTemplateConfig);
    }
    public static void doGenerator(String inputPath,String outputPath,Object model) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");
        // 创建模板对象 加载指定文件
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName);
        Writer out = new FileWriter(outputPath);
        template.process(model,out);
        out.close();
    }
}
