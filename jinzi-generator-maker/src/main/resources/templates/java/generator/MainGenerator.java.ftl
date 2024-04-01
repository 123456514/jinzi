package ${basePackage}.generator;

import ${basePackage}.generator.StaticGenerator;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
public class MainGenerator {
    public static void doGenerate(Object model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;
    <#list fileConfig.files as fileInfo>

        <#if fileInfo.type == "static">
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
        <#else >
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            DynamicGenerator.doGenerate(inputPath, outputPath, model);
        </#if>
    </#list>
    }
}