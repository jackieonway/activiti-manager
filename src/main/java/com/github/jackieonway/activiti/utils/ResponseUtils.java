package com.github.jackieonway.activiti.utils;


/**
 * @Author Jackieonway
 * @Description 返回工具类
 * @Date 2019/11/26 上午 10:57
 **/
@SuppressWarnings({"all"})
public class ResponseUtils<T> extends ResultMsg<T> {

    /**
     * 操作成功，无返回数据
     *
     * @return message
     */
    public static ResultMsg success() {
        return defaultSuccessResponse();
    }

    /**
     * 操作成功，有返回数据
     *
     * @return message
     */
    public static <T> ResultMsg<T> success(T data) {
        return defaultSuccessResponse().setData(data);
    }

    /**
     * 操作失败，无返回数据
     *
     * @return message
     */
    public static <T> ResultMsg<T> fail() {
        return defaultErrorResponse();
    }

    /**
     * 操作失败，无返回数据，设置错误信息
     *
     * @return message
     */
    public static <T> ResultMsg<T> fail(String msg) {
        return defaultErrorResponse().setData(msg);
    }

    /**
     * 验证失败，无返回数据
     *
     * @return message
     */
    public static <T> ResultMsg<T> validFail() {
        return validFailResponse();
    }

    /**
     * 验证失败，无返回数据，设置错误信息
     *
     * @return message
     */
    public static <T> ResultMsg<T> validFail(String msg) {
        return validFailResponse().setMsg(msg);
    }

    /**
     * 服务失败，无返回数据
     *
     * @return message
     */
    public static <T> ResultMsg<T> fallback() {
        return defaultFallBack();
    }

    /**
     * 服务失败，无返回数据，设置错误信息
     *
     * @return message
     */
    public static <T> ResultMsg<T> fallback(String msg) {
        return defaultFallBack().setMsg(msg);
    }

}
