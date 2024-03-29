package com.jinzi.marker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

public class MetaManager {
    private static volatile Meta meta;
    public static Meta getMetaObject() {
        if (meta == null) {
            // 保证多个线程不重复创建对象。多个线程之间进行抢锁
            synchronized (Meta.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }
    public static Meta initMeta(){
        // 读json 转对象
        // 如果我们每次 需要json 中的对象的时候，都需要进行 json 转化成为 bean 对象  那么此时如果 是上百万的线程去同时执行 同时进行 转 bean 那么这就是一个非常耗时间的任务、
        // 那么此时有一种方法 在这里我们使用 单例模式  在第一次创建成功 之后，以后每次使用这个bean对象的时候就只使用这一个对象即可，不用重复的创建 bean
        // 并且这里使用的是 双重校验锁的 懒汉模式的单例模式
        String s = ResourceUtil.readUtf8Str("meta.json");
        return JSONUtil.toBean(s, Meta.class);
    }
}
