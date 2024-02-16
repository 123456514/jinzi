import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreeMarkerTest {
    @Test
    public void test() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_31);
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");
        // 加载指定模板 创建模板对象
        Template template = configuration.getTemplate("myweb.html.ftl");

        // 把 输入的文件 和 数据模型添加给 指定的 模板文件，让模板文件对象生成指定格式数据类型文件
        Map<String,Object> dataModel = new HashMap<>();
        dataModel.put("currentYear",2024);
        List<Map<String, Object>> menuItems = new ArrayList<>();

        Map<String,Object> menuitem1 = new HashMap<>();
        menuitem1.put("url","https://codefather.cn");
        menuitem1.put("label","编程导航");

        Map<String,Object> menuitem2 = new HashMap<>();
        menuitem2.put("url","https://codefather.cn");
        menuitem2.put("label","编程导航");

        menuItems.add(menuitem1);
        menuItems.add(menuitem2);
        dataModel.put("menuItems",menuItems);

        Writer out = new FileWriter("myweb.html");

        template.process(dataModel,out);
        out.close();
    }
}
