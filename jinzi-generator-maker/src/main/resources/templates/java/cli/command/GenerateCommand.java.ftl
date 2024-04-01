package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;
import lombok.Data;
import picocli.CommandLine;

import java.io.IOException;
import java.util.concurrent.Callable;

/**
 * 这个命令  就是生成代码 作用即时接收采纳数病生成代码
 */
@Data
@CommandLine.Command(name = "generate",mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable {
<#list modelConfig.models as modelInfo>

    @CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"}, arity = "0..1", <#if modelInfo.description??>description = "${modelInfo.description}", </#if>interactive = true, echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#list>

    @Override
    public Integer call() throws TemplateException, IOException {
        DataModel mainTemplateConfig = new DataModel();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        MainGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
