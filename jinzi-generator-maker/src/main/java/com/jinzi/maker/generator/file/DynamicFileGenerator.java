package com.jinzi.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;

// 动态代码生成器
public class DynamicFileGenerator {
//    public static void doGenerate(String inputPath,String outputPath,Object model) throws IOException, TemplateException {
//        // new 出一个Configuration对象，参数为FreeMarker
//        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
//        // 指定模板文件所在路径
//        File templateDir = new File(inputPath).getParentFile();
//        configuration.setDirectoryForTemplateLoading(templateDir);
//        // 设置模板文件使用字符集
//        configuration.setDefaultEncoding("utf-8");
//        configuration.setNumberFormat("0.######");
//        // 创建模板对象 加载指定文件
//        String templateName = new File(inputPath).getName();
//        Template template = configuration.getTemplate(templateName,"utf-8");
//        // 如果文件不存在就创建目录
//        if(!FileUtil.exist(outputPath)){
//            FileUtil.touch(outputPath);
//        }
//        // 防止创建出的文件 中存在中文乱码
//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
//        template.process(model,out);
//        out.close();
//    }
//
//
//
//    public static void doGenerateByPath(String relativeInputPath,String outputPath,Object model) throws IOException, TemplateException {
//        // new 出一个Configuration对象，参数为FreeMarker
//        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
//
//        // 获取模板文件所属包和模板文件
//        // templates/java/model/DataModel.java.ftl
//        int lastSplitIndex = relativeInputPath.lastIndexOf("/");
//        String basePackagePath = relativeInputPath.substring(0,lastSplitIndex);
//        String templateName = relativeInputPath.substring(lastSplitIndex +1);
//
//        ClassTemplateLoader classTemplateLoader = new ClassTemplateLoader(DynamicFileGenerator.class,basePackagePath);
//        configuration.setTemplateLoader(classTemplateLoader);
//
//        // 设置模板文件使用字符集
//        configuration.setDefaultEncoding("utf-8");
//        configuration.setNumberFormat("0.######");
//
//        // 创建模板对象
//        Template template = configuration.getTemplate(templateName,"utf-8");
//
//        // 如果文件不存在就创建目录
//        if(!FileUtil.exist(outputPath)){
//            FileUtil.touch(outputPath);
//        }
//
//        // 防止创建出的文件 中存在中文乱码
//        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
//        template.process(model,out);
//        out.close();
//    }
    /**
     * 动态生成文件
     *
     * @param inputPath  模板文件路径
     * @param outputPath 输出文件路径
     * @param model      数据模型
     */
    public static void doGenerateByPath(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        // 1.获取配置对象
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        // 2. 指定模板文件所在的路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        // 3. 设置字符编码
        configuration.setDefaultEncoding("UTF-8");
        configuration.setEncoding(Locale.getDefault(), "UTF-8");
        // 4. 创建模板对象，并设置模板文件
        Template template = configuration.getTemplate(new File(inputPath).getName(), "UTF-8");
        // 文件不存在则创建文件和父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }
        // 5. 输出文件
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
        template.process(model, out);
        // 6. 关闭流
        out.close();
    }

    /**
     * 使用相对路径生成文件
     *
     * @param relativeInputPath 相对输入路径
     * @param outputPath        输出路径
     * @param model             数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerate(String relativeInputPath, String outputPath, Object model) throws IOException, TemplateException {
        // new 出 Configuration 对象，参数为 FreeMarker 版本号
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        relativeInputPath = FileUtil.normalize(relativeInputPath);
        int lastSplitIndex = relativeInputPath.lastIndexOf("/");
        String basePackagePath = relativeInputPath.substring(0, lastSplitIndex);
        String templateName = relativeInputPath.substring(lastSplitIndex + 1);

        // 指定模板文件所在的路径
        ClassTemplateLoader templateLoader = new ClassTemplateLoader(DynamicFileGenerator.class, basePackagePath);
        configuration.setTemplateLoader(templateLoader);

        // 设置模板文件使用的字符集
        configuration.setDefaultEncoding("utf-8");
        configuration.setEncoding(Locale.getDefault(), "UTF-8");

        // 创建模板对象，加载指定模板
        Template template = configuration.getTemplate(templateName, "UTF-8");


        // 文件不存在则创建文件和父目录
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 生成
        // 5. 输出文件
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
        template.process(model, out);

        // 生成文件后别忘了关闭哦
        out.close();
    }
}
