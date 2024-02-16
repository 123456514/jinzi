package com.jinzi.cli.pattern;

public class Device {
    // 源
    private String name;
    public Device(String name){
        this.name = name;
    }
    public void turnOff(){
        System.out.println(name + "设备关闭");
    }
    public void turnON(){
        System.out.println(name + "设备打开");
    }
}
