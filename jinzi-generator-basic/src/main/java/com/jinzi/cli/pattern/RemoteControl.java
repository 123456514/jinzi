package com.jinzi.cli.pattern;

/**
 * 这里就是所有命令的操作集合，把每个命令看做成为是一个 按钮，那么此时的这个类就是 安装这些 按钮的遥控器
 */
public class RemoteControl {
    private Command command;

    /**
     * 在遥控器中安装按钮
     * @param command 按钮
     */
    public void setCommand(Command command){
        this.command = command;
    }
    public void pressButton(){
        command.execute();
    }
}
