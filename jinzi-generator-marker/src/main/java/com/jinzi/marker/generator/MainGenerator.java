package com.jinzi.marker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.StrUtil;
import com.jinzi.marker.generator.file.DynamicFileGenerator;
import com.jinzi.marker.meta.Meta;
import com.jinzi.marker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

public class MainGenerator {
    public static void main(String[] args) {
        Meta meta = MetaManager.getMetaObject();
        // 输出的根路径
        String projectPath = System.getProperty("user.dir");
        String outputPath = projectPath + File.separator + "generated";
        if(!FileUtil.exist(outputPath)){
            FileUtil.mkdir(outputPath);
        }
        // 读取resource 目录
        ClassPathResource classPathResource = new ClassPathResource("");
        String inputResourcePath = classPathResource.getAbsolutePath();

        // java包基础路径
        // com.jinzi
        String outputBasePackage = meta.getBasePackage();
        // com/jinzi
        String outputBasePackagePath = StrUtil.join("/",StrUtil.split(outputBasePackage,"."));
        // generated/src/java/com/jinzi
        String outputBaseJavaPackagePath = outputPath + File.separator + "src/main/java" + outputBasePackagePath;
        String inputFilePath;
        String outputFilePath;

        // 生成model.DataModel
        inputFilePath = inputResourcePath + File.separator + "templates/java/model/DataModel.java.ftl";
        outputFilePath = outputBaseJavaPackagePath + "/model/DataModel.java";
        try {
            DynamicFileGenerator.doGenerator(inputFilePath,outputFilePath,meta);
        } catch (IOException | TemplateException e) {
            throw new RuntimeException(e);
        }
    }
}
