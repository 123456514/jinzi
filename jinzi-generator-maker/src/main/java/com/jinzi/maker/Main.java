package com.jinzi.maker;

import com.jinzi.maker.generator.MainGenerator;
import com.jinzi.maker.generator.main.GenerateTemplate;
import com.jinzi.maker.generator.ZipGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {

        GenerateTemplate generator = new ZipGenerator();
        generator.doGenerate();
    }
}
