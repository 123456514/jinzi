package com.jinzi.cli.pattern;

/**
 * 遥控器中的关闭按钮
 */
public class TurnOffCommand implements Command{
    // device 表示的是 关闭命令 要操作的设备
    private Device device;
    public TurnOffCommand(Device device){
        this.device = device;
    }
    @Override
    public void execute() {
        device.turnOff();
    }
}
