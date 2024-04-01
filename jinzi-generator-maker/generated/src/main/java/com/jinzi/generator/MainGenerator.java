package com.jinzi.generator;

import com.jinzi.generator.StaticGenerator;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
public class MainGenerator {
    public static void doGenerate(Object model) throws TemplateException, IOException {
        String inputRootPath = "D:/code/jinzi-generator/jinzi-generator-demo-projects/acm-template-pro";
        String outputRootPath = "generated";

        String inputPath;
        String outputPath;

            inputPath = new File(inputRootPath, "src/com/jinzi/acm/MainTemplate.java.ftl").getAbsolutePath();
            outputPath = new File(outputRootPath, "src/com/jinzi/acm/MainTemplate.java").getAbsolutePath();
            DynamicGenerator.doGenerate(inputPath, outputPath, model);

            inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
            outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
            DynamicGenerator.doGenerate(inputPath, outputPath, model);

            inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
            outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
            DynamicGenerator.doGenerate(inputPath, outputPath, model);
    }
}