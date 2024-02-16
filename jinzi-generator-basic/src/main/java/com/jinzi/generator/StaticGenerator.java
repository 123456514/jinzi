package com.jinzi.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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
        copyFilesByRecursive(inputPath,outputPath);
    }

    /**
     * 拷贝文件 会将输入目录完整拷贝到输入目录下
     * @param inputPath 输入路径
     * @param outputPath 输出路径
     */
    public static void copyFilesByHutool(String inputPath,String outputPath){
        FileUtil.copy(inputPath,outputPath,false);
    }

    /**
     * 使用递归的方法 找到目录下的文件 进行拷贝
     * @param inputPath 输入文件路径
     * @param outputPath 输出文件路径
     */
    public static void copyFilesByRecursive(String inputPath,String outputPath){
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            copyFileByRecursive(inputFile,outputFile);
        } catch (IOException e) {
            System.out.println("文件复制出错");
            throw new RuntimeException(e);
        }
    }

    /**
     * 先创建目录，然后遍历目录内的文件，依次复制
     * @param inputFile
     * @param outputFile
     * @throws IOException
     */
    private static void copyFileByRecursive(File inputFile,File outputFile) throws IOException {

        if(inputFile.isDirectory()){
            System.out.println(inputFile.getName());
            // 在目标目录创建一个和 输入目录相同的 目录名
            File destOutputFile = new File(outputFile,inputFile.getName());
            if(!destOutputFile.exists()){
                destOutputFile.mkdirs();
            }
            File[] files = inputFile.listFiles();
            if(ArrayUtil.isEmpty(files)){
                return;
            }
            for (File file : files){
                copyFileByRecursive(file,destOutputFile);
            }
        }else{
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(),destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
//  todo  得到目录的完整结构树信息，可以由此制作出 文件 对比工具，目录分析工具，目录总结工具等