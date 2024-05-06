package com.azhang.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import com.azhang.cli.util.ReflexUtil;
import com.azhang.generator.MainGenerator;
import com.azhang.model.DataModel;
import lombok.Data;
import lombok.SneakyThrows;
import picocli.CommandLine;


/**
 * @author azhang
 * @date 2024年5月5日 下午10:41:15
 * @description 读取json生成命令
 */
@Data
@CommandLine.Command(name = "json-generate", mixinStandardHelpOptions = true, description = "读取json生成命令")
public class JsonGenerateCommand implements Runnable {


    /**
     * json文件路径
     */
    @CommandLine.Option(
            names = {"-f", "--file"},
            arity = "0..1",
            description = "json文件路径",
            echo = true,
            interactive = true)
    private String filePath;


    @SneakyThrows
    @Override
    public void run() {
        ReflexUtil.setFieldsWithInteractiveAnnotation(this, this.getClass());

        String dataModelStr = FileUtil.readUtf8String(filePath);
        DataModel dataModel = JSONUtil.toBean(dataModelStr, DataModel.class);
        MainGenerator.doGenerate(dataModel);
    }
}
