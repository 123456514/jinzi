package com.jinzi.cli.pattern;


/**
 * 遥控器中开按钮
 */
public class TurnOnCommand implements Command{
    private Device device;
    public TurnOnCommand(Device device){
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnON();
    }
}
