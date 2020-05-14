package com.github.jackieonway.activiti.utils;


import com.alibaba.fastjson.JSON;

import java.util.List;

public class BeanConvertUtils {
    private BeanConvertUtils() {
    }

    public static <T> T convert(Object source, Class<T> targetClass, String dateFormat) {
        String jsonStr = JSON.toJSONStringWithDateFormat(source, dateFormat);
        return JSON.parseObject(jsonStr, targetClass);
    }

    public static <T> T convert(Object source, Class<T> targetClass) {
        String jsonStr = JSON.toJSONString(source);
        return JSON.parseObject(jsonStr, targetClass);
    }

    public static <T> List<T> convertList(List source, Class<T> targetClass, String dateFormat) {
        String jsonStr = JSON.toJSONStringWithDateFormat(source, dateFormat);
        return JSON.parseArray(jsonStr, targetClass);
    }

    public static <T> List<T> convertList(List source, Class<T> targetClass) {
        String jsonStr = JSON.toJSONString(source);
        return JSON.parseArray(jsonStr, targetClass);
    }
}