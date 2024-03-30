package com.jinzi.cli.example;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Java 命令行开发工具 介绍
 * 这里的一个类就是一个 命令的类型，并并且在实现类逻辑的时候，必须要实现 Runnable 中的 run 方法，那么以后再执行这个命令的时候，执行的是excute()方法
 *  而这个方法就会调用 类中的run 方法
 *  但是如果要实现和用户要进行交互的命令行，那么就需要实现 Callable 中的call方法
 *
 * @Command 直接介绍  name 表示的是命令的名称 version 表示的是这个命令的版本 最后一个参数表示的是 是否开启帮助文档
 */
@Command(name = "ASCIIArt", version = "ASCIIArt 1.0", mixinStandardHelpOptions = true)
public class ASCIIArt implements Runnable {

    // 表示的是 用户输入 ASCIIArt -s 20 那么此时就会那这个 20 赋值给 fontSize
    @Option(names = { "-s", "--font-size" }, description = "Font size")
    int fontSize = 19;

    // 这里表示的是 如果用户输入了这个 word 那么就打印 word 如果用户没有输入 word 那么就给用户默认的返回一个 word
    @Parameters(paramLabel = "<word>", defaultValue = "Hello, picocli",
            description = "Words to be translated into ASCII art.")
    private String[] words = { "Hello,", "picocli" };

    // 用户执行某条命令之后 会实际触发的方法
    @Override
    public void run() {
        // 自己实现业务逻辑
        System.out.println("fontsize = " + fontSize);
        System.out.println("words = " + String.join(",",words));
    }

    public static void main(String[] args) {
        // 这里会返回一个状态码，args 表示的是用户输入的参数
        // 这里的一个类就表示的 一个命令的类型
        int exitCode = new CommandLine(new ASCIIArt()).execute(args);
        System.exit(exitCode);
    }
}








