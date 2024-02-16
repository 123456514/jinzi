package com.jinzi.cli.pattern;

/**
 * 遥控器
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
