package com.jinzi.cli.example;

import picocli.CommandLine;

import java.util.concurrent.Callable;

public class Login implements Callable<Integer> {

    @CommandLine.Option(names = {"-u","-user"},description = "user name")
    String user;

    /**
     * interactive 这个参数表示的是 要和用户交互
     * echo 表示的是用户输入的内容是否回回显出来，用户是可见的，在打了jar 的情况下，运行jar中的代码，那么此时设置 echo 为 false就是 用户输入的信息是隐藏的
     */
    @CommandLine.Option(names = {"-p","--password"},description = "Passphrase",interactive = true,echo = true,prompt = "请输入密码")
    String password;
    @CommandLine.Option(names = {"-cp","--checkPassword"},description = "Passphrase",interactive = true,echo = true,prompt = "请输入确认密码")
    String checkPassword;
    @Override
    public Integer call() throws Exception {
        System.out.println("password = " + password );
        System.out.println("checkPassword = " + checkPassword );
        return 0;
    }

    public static void main(String[] args) {
        new CommandLine(new Login()).execute("-u","user123","-p","-cp");
    }
}
