package com.jinzi.maker.generator;

import com.jinzi.maker.generator.main.GenerateTemplate;
import com.jinzi.maker.meta.Meta;

import java.io.IOException;

public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String jarPath, String shellOutputPath, String sourceOutputPath) {
        String distPath = super.buildDist(outputPath, jarPath, shellOutputPath, sourceOutputPath);
        return super.buildZip(distPath);
    }
    @Override
    protected void versionControl(Meta meta, String outputPath) throws IOException, InterruptedException {
        System.out.println("重写子类 不生成git版本控制文件 和 .gitignore 文件啦");
    }

}
