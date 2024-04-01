package ${basePackage}.generator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
public class MainGenerator {
    public static void doGenerate(DataModel model) throws TemplateException, IOException {
        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";
        String inputPath;
        String outputPath;
    <#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = model.${modelInfo.fieldName};
    </#list>
<#list fileConfig.files as fileInfo>
<#--    生成.gitignore 文件-->
    <#if fileInfo.condition??>
        if(${fileInfo.condition}){
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType == "static">
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
            <#else >
            DynamicGenerator.doGenerate(inputPath, outputPath, model);
            </#if>
        }
    <#else >
<#--        不生成.gitignore 文件-->
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType == "static">
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
            <#else >
            DynamicGenerator.doGenerate(inputPath, outputPath, model);
            </#if>
    </#if>
</#list>
    }
}
