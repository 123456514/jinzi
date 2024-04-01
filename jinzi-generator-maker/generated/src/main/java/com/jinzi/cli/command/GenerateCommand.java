package com.jinzi.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.jinzi.generator.MainGenerator;
import com.jinzi.model.DataModel;
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

    @CommandLine.Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否生成循环", interactive = true, echo = true)
    private boolean loop = false;

    @CommandLine.Option(names = {"-a", "--author"}, arity = "0..1", description = "作者注释", interactive = true, echo = true)
    private String author = "yupi";

    @CommandLine.Option(names = {"-o", "--outputText"}, arity = "0..1", description = "输出信息", interactive = true, echo = true)
    private String outputText = "sum = ";

    @Override
    public Integer call() throws TemplateException, IOException {
        DataModel mainTemplateConfig = new DataModel();
        BeanUtil.copyProperties(this,mainTemplateConfig);
        MainGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
