package com.github.jackieonway.activiti.utils;

import com.github.jackieonway.activiti.config.emum.ResultEnum;
import com.github.jackieonway.activiti.config.emum.ResultStatusCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * descpription： 返回信息统一处理
 * auth:Jackieonway
 */
@ApiModel(description = "请求响应数据")
@Builder
@Data
@Accessors(chain = true)
public class ResultMsg<T> implements Serializable {

    private static final long serialVersionUID = 7514826298158585250L;

    @ApiModelProperty("状态码 0：成功，-1：失败")
    private Integer code;

    @ApiModelProperty("返回消息")
    private String msg;

    @ApiModelProperty("数据对象")
    private T data;

    public ResultMsg() {
    }

    /**
     * resultMsg 构造
     *
     * @param code 返回码
     * @param msg 消息
     * @param data 数据
     */
    public ResultMsg(int code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }



    /**
     *  默认成功返回值
     * @return 返回封装类
     * @date 2019/11/26 上午 10:12
     **/
    public static ResultMsg defaultSuccessResponse() {
        return ResultMsg.builder().code(ResultStatusCode.OK.getCode()).msg(ResultEnum.SUCCESS.getMsg()).build();
    }

    /**
     * 默认失败返回值
     * @return 返回封装类
     * @date 2019/11/26 上午 10:12
     **/
    public static final ResultMsg defaultErrorResponse() {
        return ResultMsg.builder().code(ResultStatusCode.ERROR.getCode()).msg(ResultStatusCode.ERROR.getMsg()).build();
    }

    /**
     *  默认回滚返回值
     * @date  2019/11/26 上午 10:21
     **/
    public static ResultMsg defaultFallBack() {
        return ResultMsg.builder().code(ResultEnum.SYSTEM_INNER_ERROR.getCode())
                .msg(ResultEnum.SYSTEM_INNER_ERROR.getMsg()).build();
    }

    public static ResultMsg validFailResponse() {
        return ResultMsg.builder().code(ResultStatusCode.ERROR.getCode()).msg("输入校验失败").build();
    }
}
