package com.azhang.cli.util;

import picocli.CommandLine;
import java.util.Arrays;

import static com.azhang.cli.util.ConsoleUtil.getConsoleValue;
import static com.azhang.cli.util.ConvertUtil.convertValueToFieldType;

/**
 * @author azhang
 * @date 2024年5月5日 下午10:41:15
 */
public class ReflexUtil {

    public static void setFieldsWithInteractiveAnnotation(Object instance,Class<?> classzz) throws IllegalAccessException {
        for (java.lang.reflect.Field field : classzz.getDeclaredFields()) {
            field.setAccessible(true);
            CommandLine.Option option = field.getAnnotation(CommandLine.Option.class);
            if (option != null && option.interactive() && field.get(instance) == null) {
                String value = getConsoleValue("enter for value for --" + field.getName() + "("+ Arrays.toString(option.description()) +")" + ": ");
                Object fieldValue = convertValueToFieldType(value, field.getType());
                field.set(instance, fieldValue);
            }
        }
    }

}
