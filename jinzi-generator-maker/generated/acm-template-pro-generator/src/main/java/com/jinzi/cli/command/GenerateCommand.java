package com.jinzi.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.jinzi.generator.MainGenerator;
import com.jinzi.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;
/**
 * 这个命令  就是生成代码 作用即时接收采纳数病生成代码
 */
@Data
@CommandLine.Command(name = "generate",description = "生成代码",mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

        @CommandLine.Option(names = {"-n", "--needGit"}, arity = "0..1", description = "是否生成.gitignore文件", interactive = true, echo = true)
        private boolean needGit = true;

        @CommandLine.Option(names = {"-l", "--loop"}, arity = "0..1", description = "是否生成循环", interactive = true, echo = true)
        private boolean loop = false;

    /**
     * 核心模板
     */
    static DataModel.MainTemplate mainTemplate = new DataModel.MainTemplate();
    @CommandLine.Command(name = "mainTemplate")
    @Data
    public static class MainTemplateCommand implements Runnable{
        @CommandLine.Option(names = {"-a", "--author"}, arity = "0..1", description = "作者注释", interactive = true, echo = true)
        private String author = "zhoujin";
        @CommandLine.Option(names = {"-o", "--outputText"}, arity = "0..1", description = "输出信息", interactive = true, echo = true)
        private String outputText = "sum = ";

        @Override
        public void run() {
             mainTemplate.author = author;
             mainTemplate.outputText = outputText;
        }
    }

    @Override
    public Integer call() throws Exception  {
        if (loop) {
            System.out.println("输入核心模板配置：");
            CommandLine commandLine = new CommandLine(MainTemplateCommand.class);
            commandLine.execute("--author", "--outputText");
        }
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        dataModel.mainTemplate = mainTemplate;
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}