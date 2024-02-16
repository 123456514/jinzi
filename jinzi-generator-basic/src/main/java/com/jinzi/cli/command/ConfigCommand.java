package com.jinzi.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.jinzi.model.MainTemplateConfig;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * 用户输入 --config 或者 -c 指令 给用户显示 交互的字段信息
 */
@CommandLine.Command(name = "config",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{

    @Override
    public void run() {
        Field[] fields = ReflectUtil.getFields(MainTemplateConfig.class);
        for(Field field : fields){
            System.out.println("字段类型 :" + field.getType());
            System.out.println("字段名称 :" + field.getName());
        }
    }
}
