package com.azhang.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.azhang.cli.util.ReflexUtil;
import com.azhang.generator.MainGenerator;
import com.azhang.model.DataModel;
import lombok.Data;
import lombok.SneakyThrows;
import picocli.CommandLine;


/**
 * @author azhang
 * @date 2024年5月5日 下午10:41:15
 * @description 生成命令
 */
@Data
@CommandLine.Command(name = "generate", mixinStandardHelpOptions = true, description = "生成命令")
public class GenerateCommand implements Runnable {


    /**
    * MySQL数据库配置
    */
    static DataModel.MysqlConfig mysqlConfig  = new DataModel.MysqlConfig();

    @CommandLine.Command(name = "mysqlConfig")
    @Data
    public static class MysqlConfigCommand implements Runnable {
            @CommandLine.Option(
            names = {"--password"},
            arity = "0..1",
            description = "密码",
            echo = true,
            interactive = true)
            private String password;
            @CommandLine.Option(
            names = {"--url"},
            arity = "0..1",
            description = "地址",
            echo = true,
            interactive = true)
            private String url;
            @CommandLine.Option(
            names = {"--username"},
            arity = "0..1",
            description = "用户名",
            echo = true,
            interactive = true)
            private String username;

        @Override
        public void run(){
            mysqlConfig.setPassword(password);
            mysqlConfig.setUrl(url);
            mysqlConfig.setUsername(username);
        }

    }


    /**
    * 接口文档配置
    */
    static DataModel.DocsConfig docsConfig  = new DataModel.DocsConfig();

    @CommandLine.Command(name = "docsConfig")
    @Data
    public static class DocsConfigCommand implements Runnable {
            @CommandLine.Option(
            names = {"--description"},
            arity = "0..1",
            description = "接口文档描述",
            echo = true,
            interactive = true)
            private String description;
            @CommandLine.Option(
            names = {"--title"},
            arity = "0..1",
            description = "接口文档标题",
            echo = true,
            interactive = true)
            private String title;
            @CommandLine.Option(
            names = {"--version"},
            arity = "0..1",
            description = "接口文档版本",
            echo = true,
            interactive = true)
            private String version;

        @Override
        public void run(){
            docsConfig.setDescription(description);
            docsConfig.setTitle(title);
            docsConfig.setVersion(version);
        }

    }



        /**
        * 是否开启接口文档功能
        */
        @CommandLine.Option(
        names = {"--needDocs"},
        arity = "0..1",
        description = "是否开启接口文档功能",
        echo = true,
        interactive = true)
        private Boolean needDocs;


        /**
        * 是否开启帖子功能
        */
        @CommandLine.Option(
        names = {"--needPost"},
        arity = "0..1",
        description = "是否开启帖子功能",
        echo = true,
        interactive = true)
        private Boolean needPost;


        /**
        * 是否开启跨域功能
        */
        @CommandLine.Option(
        names = {"--needCors"},
        arity = "0..1",
        description = "是否开启跨域功能",
        echo = true,
        interactive = true)
        private Boolean needCors;


        /**
        * 是否开启ES功能
        */
        @CommandLine.Option(
        names = {"--needEs"},
        arity = "0..1",
        description = "是否开启ES功能",
        echo = true,
        interactive = true)
        private Boolean needEs;


        /**
        * 基础包名
        */
        @CommandLine.Option(
        names = {"--basePackage"},
        arity = "0..1",
        description = "基础包名",
        echo = true,
        interactive = true)
        private String basePackage;


        /**
        * 是否开启Redis功能
        */
        @CommandLine.Option(
        names = {"--needRedis"},
        arity = "0..1",
        description = "是否开启Redis功能",
        echo = true,
        interactive = true)
        private Boolean needRedis;

    @SneakyThrows
    @Override
    public void run() {
        ReflexUtil.setFieldsWithInteractiveAnnotation(this, this.getClass());

        System.out.println("输入MySQL数据库配置配置：");
        CommandLine mysqlConfigCommandLine = new CommandLine(MysqlConfigCommand.class);
        mysqlConfigCommandLine.execute("--password", "--url", "--username");
        if(needDocs) {
            System.out.println("输入接口文档配置配置：");
            CommandLine docsConfigCommandLine = new CommandLine(DocsConfigCommand.class);
            docsConfigCommandLine.execute("--description", "--title", "--version");
        }
        DataModel config = new DataModel();
        BeanUtil.copyProperties(this, config);
        config.setMysqlConfig(mysqlConfig);
        config.setDocsConfig(docsConfig);
        MainGenerator.doGenerate(config);
    }
}
