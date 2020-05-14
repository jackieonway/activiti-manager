package com.github.jackieonway.activiti.utils;

import java.util.UUID;

/**
 * @author Jackieonway
 * @Description 公共工具类
 * @Date 下午 2:22 2019/11/25
 **/
public class CommonUtil {
    private CommonUtil() {
    }

    /**
     * @Author deng1
     * @Description 生成uuid
     * @Date 2019/11/25 下午 3:10
     **/
    public static String uuidString() {
        return UUID.randomUUID().toString().replace("-", "");
    }

}
