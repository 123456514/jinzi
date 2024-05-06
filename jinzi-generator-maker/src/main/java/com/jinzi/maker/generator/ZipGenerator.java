package com.jinzi.maker.generator;

import com.jinzi.maker.generator.main.GenerateTemplate;

public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputPath, String jarPath, String shellOutputPath, String sourceOutputPath) {
        String distPath = super.buildDist(outputPath, jarPath, shellOutputPath, sourceOutputPath);
        return super.buildZip(distPath);
    }
}
