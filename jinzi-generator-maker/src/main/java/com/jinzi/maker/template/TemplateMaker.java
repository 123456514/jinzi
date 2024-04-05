package com.jinzi.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jinzi.maker.meta.Meta;
import com.jinzi.maker.meta.enums.FileGenerateTypeEnum;
import com.jinzi.maker.meta.enums.FileTypeEnum;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 * 预期生成 meta.json 和  挖好啃得文件
 */

public class TemplateMaker {
    /**
     * 文件去重
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList){
        List<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(
                fileInfoList.stream()
                        .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath,o -> o,(e,r) -> r)).values()
        );
        return newFileInfoList;
    }

    /**
     * 模型去重
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList){
        List<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(
                modelInfoList.stream()
                        .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName,o -> o,(e,r) -> r)).values()
        );
        return newModelInfoList;
    }
    public static long makeTemplate(Meta newMeta,String originProjectPath,String inputFilePath,Meta.ModelConfig.ModelInfo modelInfo,String searchStr,Long id){
        // 如果没有id 则生成id
        if(id == null){
            id = IdUtil.getSnowflakeNextId();
        }
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";
        String templatePath = tempDirPath + File.separator + id;

        // 是否为首次制作模板
        // 如果这个雪花算法生成的文件在原有的项目目录中不存在，那么就是第一次创建
        if(!FileUtil.exist(templatePath)){
            FileUtil.mkdir(templatePath);
            // 把原来 源项目中的文件 拷贝到 工作空间目录中
            FileUtil.copy(originProjectPath,templatePath,true);
        }
        // 基本信息 输入信息

        String sourceRootPath = templatePath + File.separator +  FileUtil.getLastPathEle(Paths.get(originProjectPath)).toString(); // 得到实例模板的绝对路径
        // 修改 win系统中的分割符 把 // 进行转义
        sourceRootPath = sourceRootPath.replaceAll("\\\\","/");

        String fileInputPath = inputFilePath;
        String fileOutputPath = fileInputPath + ".ftl";


        // 使用字符串替换，生成模板文件
        String fileInputAbsolutePath = sourceRootPath + File.separator + fileInputPath;  // 源文件绝对路径
        String fileOutputAbsolutePath = sourceRootPath + File.separator + fileOutputPath;
        String fileContent = null; // 读取到源文件的所有信息，转化成为String 类型的格式

        // 如果已有模板文件，说明不是第一次制作，则在模板基础上再次挖坑
        if(FileUtil.exist(fileOutputAbsolutePath)){
            fileContent = FileUtil.readUtf8String(fileOutputAbsolutePath);
        }else{
            fileContent = FileUtil.readUtf8String(fileInputAbsolutePath);
        }

        String replacement = String.format("${%s}",modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent,searchStr,replacement); // 把源文件中的 "sum = " 转化成为 ${outputText}

        FileUtil.writeUtf8String(newFileContent,fileInputAbsolutePath); // 将新的转化后的String 类型的信息 写到 绝对路径下

        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(fileInputPath);
        fileInfo.setOutputPath(fileOutputPath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 生成配置文件
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        // 如果已经有了meta 文件，说明不是第一次制作，则在Meta基础上进行修改
        if(FileUtil.exist(metaOutputPath)){
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath),Meta.class);
            // 追加配置参数
            newMeta = oldMeta;
            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.add(fileInfo);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);
            // 配置去重
            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));
        }else{
            // meta 模型 文件参数配置
            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);
            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.add(fileInfo);
            // meta 模型 model参数配置
            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);
        }
        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta),metaOutputPath);
        return id;

    }
    public static void main(String[] args){
        Meta meta = new Meta();
        meta.setName("acm-template-generator");
        meta.setDescription("ACM 实例模板生成器");

        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "jinzi-generator-demo-projects/acm-template";
        String inputFilePath = "src/com/jinzi/acm/MainTemplate.java";

        // 模型参数
        // 第一次配置
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
//        modelInfo.setFieldName("output");
//        modelInfo.setType("String");
//        modelInfo.setDescription("Sum : ");

        // 第二次配置
        modelInfo.setFieldName("className");
        modelInfo.setType("String");
//        String searchStr = "Sum : ";
        String searchStr = "MainTemplate";
        long l = makeTemplate(meta, originProjectPath, inputFilePath, modelInfo, searchStr, 1775691228327657472L);
        System.out.println(l);
    }
}
