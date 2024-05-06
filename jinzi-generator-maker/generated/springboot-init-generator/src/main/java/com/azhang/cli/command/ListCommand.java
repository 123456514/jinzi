package com.azhang.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * @author azhang
 * @date 2024年5月5日 下午10:41:15
 * @description 列表命令
 */
@CommandLine.Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Override
    public void run() {
        String inputPath = ".source/springboot-init";
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File f : files) {
            System.out.println(f.getName());
        }
    }
}