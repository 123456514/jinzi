package com.jinzi.marker.generator.file;

import cn.hutool.core.io.FileUtil;
import com.jinzi.marker.model.DataModel;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
// 动态代码生成器
public class DynamicFileGenerator {
    public static void doGenerator(String inputPath,String outputPath,Object model) throws IOException, TemplateException {
        // new 出一个Configuration对象，参数为FreeMarker
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        // 指定模板文件所在路径
        File templateDir = new File(inputPath).getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);
        // 设置模板文件使用字符集
        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");
        // 创建模板对象 加载指定文件
        String templateName = new File(inputPath).getName();
        Template template = configuration.getTemplate(templateName,"utf-8");

        // 创建数据模型
        DataModel dataModel = new DataModel();
        dataModel.setAuthor("zhoujin");
        dataModel.setOutputText("sum = ");
        dataModel.setLoop(false);

        // 如果文件不存在就创建目录
        if(!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }
        // 防止创建出的文件 中存在中文乱码
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(Paths.get(outputPath)), StandardCharsets.UTF_8));
        template.process(model,out);
        out.close();
    }
}
