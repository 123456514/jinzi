package com.jinzi.maker.meta;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@Data
public class Meta {

    private String name;
    private String description;
    private String basePackage;
    private String version;
    private String author;
    private String createTime;
    private FileConfig fileConfig;
    private ModelConfig modelConfig;

    @NoArgsConstructor
    @Data
    public static class FileConfig implements Serializable {
        private String inputRootPath;
        private String outputRootPath;
        private String sourceRootPath;
        private String type;
        private List<FileInfo> files;

        @NoArgsConstructor
        @Data
        public static class FileInfo implements Serializable {
            private String inputPath;
            private String outputPath;
            private String type;
            private String generateType;
            private String condition; // 该分组共享的生成条件，同时控制组能多个文件的生成
            private String groupKey; // 组的唯一标识
            private String groupName; // 组的名称
            private List<FileInfo> files; // 组中包含的文件
        }
    }

    @NoArgsConstructor
    @Data
    public static class ModelConfig implements Serializable {
        private List<ModelInfo> models;

        @NoArgsConstructor
        @Data
        public static class ModelInfo implements Serializable {
            private String fieldName;
            private String type;
            private String description;
            private Object defaultValue;
            private String abbr;
            private String  groupKey;
            private String groupName;
            private List<ModelInfo> models;
            private String condition;

            // 中间参数  该分组下所有参数拼接字符串
            private String allArgsStr;
        }
    }
}