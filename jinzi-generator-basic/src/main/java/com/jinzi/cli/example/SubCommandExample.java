package com.jinzi.cli.example;

import picocli.CommandLine;

@CommandLine.Command(name = "main" ,mixinStandardHelpOptions = true)
public class SubCommandExample implements Runnable{

    @Override
    public void run() {
        System.out.println("执行主命令");
    }
    @CommandLine.Command(name = "add",description = "增加",mixinStandardHelpOptions = true)
    static class AddCommand implements Runnable{
        public void run(){
            System.out.println("执行增添命令");
        }
    }
    @CommandLine.Command(name = "delete",description = "删除",mixinStandardHelpOptions = true)
    static class DelCommand implements Runnable{
        public void run(){
            System.out.println("执行删除命令");
        }
    }
    @CommandLine.Command(name = "query",description = "查找",mixinStandardHelpOptions = true)
    static class QueryCommand implements Runnable{
        public void run(){
            System.out.println("执行查找命令");
        }
    }

    public static void main(String[] args) {
        String []a = new String[]{"ad"};
        int code = new CommandLine(new SubCommandExample())
                .addSubcommand(new AddCommand())
                .addSubcommand(new DelCommand())
                .addSubcommand(new QueryCommand()).execute(a);
        System.out.println(code);
    }
}
