package com.azhang.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.azhang.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * @author azhang
 * @date 2024年5月5日 下午10:41:15
 * @description 配置命令
 */
@CommandLine.Command(name = "config", description = "配置命令",mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable{

    @Override
    public void run() {
        Field[] fields =
                ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.println(field.getName() + " : " + field.getType().getName());
        }
    }
}
