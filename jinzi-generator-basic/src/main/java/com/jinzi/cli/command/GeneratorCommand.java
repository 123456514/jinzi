package com.jinzi.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.jinzi.generator.MainGenerator;
import com.jinzi.model.MainTemplateConfig;
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
public class GeneratorCommand implements Callable {
    @CommandLine.Option(names = {"-l","--loop"},description = "是否循环",arity = "0..1",interactive = true,echo = true)
    private boolean loop;

    @CommandLine.Option(names = {"-a","--author"},description = "作者名称",arity = "0..1",interactive = true,echo = true)
    private String author;

    @CommandLine.Option(names = {"-o","--outputText"},description = "输出文本",arity = "0..1",interactive = true,echo = true)
    private String outputText;

    @Override
    public Integer call() throws TemplateException, IOException {
        MainTemplateConfig mainTemplateConfig = new MainTemplateConfig();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        MainGenerator.doGenerator(mainTemplateConfig);
        return 0;
    }
}
