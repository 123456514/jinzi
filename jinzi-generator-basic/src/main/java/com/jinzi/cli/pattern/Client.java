package com.jinzi.cli.pattern;


public class Client {
    public static void main(String[] args) {
        // 创建接受者对象 相当于 电视机
        Device tv= new Device("TV");
        Device stereo = new Device("Stereo");

        // 创建具体命令对象，可以绑定不同设备  相当于 一个功能性的 按钮
        TurnOffCommand turnOffCommand = new TurnOffCommand(tv);
        TurnOnCommand turnOnCommand = new TurnOnCommand(stereo);

        // 创建遥控器
        RemoteControl control = new RemoteControl();

        // 执行命令
        control.setCommand(turnOffCommand);
        control.pressButton();

        control.setCommand(turnOnCommand);
        control.pressButton();
    }
}
