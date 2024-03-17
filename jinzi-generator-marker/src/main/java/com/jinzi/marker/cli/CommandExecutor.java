package com.jinzi.marker.cli;

import com.jinzi.marker.cli.command.ConfigCommand;
import com.jinzi.marker.cli.command.GeneratorCommand;
import com.jinzi.marker.cli.command.ListCommand;
import picocli.CommandLine;
import picocli.CommandLine.Command;

// 相当于一个 调用者 调用Command
@Command(name = "jinzi",mixinStandardHelpOptions = true)
public class CommandExecutor implements Runnable {
    private final CommandLine commandLine;
    {
        commandLine = new CommandLine(this)
                .addSubcommand(new GeneratorCommand())
                .addSubcommand(new ConfigCommand())
                .addSubcommand(new ListCommand());
    }
    /**
     * 执行命令
     */
    public Integer doExecute(String []args){
        return commandLine.execute(args);
    }

    @Override
    public void run() {
        System.out.println("请输入具体命令，或者输入 --help 查看命令提示");
    }
}
