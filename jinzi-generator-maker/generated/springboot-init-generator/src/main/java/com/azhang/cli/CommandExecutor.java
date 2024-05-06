package com.azhang.cli;

import com.azhang.cli.command.ConfigCommand;
import com.azhang.cli.command.GenerateCommand;
import com.azhang.cli.command.JsonGenerateCommand;
import com.azhang.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * @author azhang
 * @date 2024年5月5日 下午10:41:15
 * @description 绑定所有子命令
 */
@Command(name = "springboot-init-generator", mixinStandardHelpOptions = true, version = "1.0")
public class CommandExecutor implements Runnable {

    private final CommandLine commandLine;

    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GenerateCommand())
                .addSubcommand(new ListCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new JsonGenerateCommand());
    }


    @Override
    public void run() {
        // 不执行子命令时候 打印帮助信息
        commandLine.usage(System.out);
    }

    /**
     * 执行子命令
     *
     * @param args 命令行参数
     */
    public void doExecute(String[] args) {
        commandLine.execute(args);
    }

}
