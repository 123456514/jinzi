package com.jinzi.cli.pattern;

/**
 * 遥控器中的关闭按钮
 */
public class TurnOffCommand implements Command{
    private Device device;
    public TurnOffCommand(Device device){
        this.device = device;
    }
    @Override
    public void execute() {
        device.turnOff();
    }
}
