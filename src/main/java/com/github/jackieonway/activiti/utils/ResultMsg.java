package com.github.jackieonway.activiti.utils;

import com.github.jackieonway.activiti.config.emum.ResultEnum;
import com.github.jackieonway.activiti.config.emum.ResultStatusCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * descpription： 返回信息统一处理
 * auth:Jackieonway
 */
@ApiModel(
        description = "请求响应数据"
)
public class ResultMsg<T> implements Serializable {
    private static final long serialVersionUID = 7514826298158585250L;
    @ApiModelProperty("状态码 0：成功，-1：失败")
    private Integer resultCode;
    @ApiModelProperty("返回消息")
    private String resultMsg;

    //错误码：从ResultEnum枚举类里面取值
    @ApiModelProperty("错误码")
    private Integer errorCode;

    @ApiModelProperty("数据对象")
    private T resultData;

    private ResultEnum resultEnum;
    private Exception exception;

    public ResultMsg() {
    }

    /**
     * resultMsg 构造
     *
     * @param resultCode
     * @param resultMsg
     * @param resultData
     */
    public ResultMsg(int resultCode, String resultMsg, T resultData) {
        this.resultCode = resultCode;
        this.resultData = resultData;
        this.resultMsg = resultMsg;
    }

    /**
     * @param resultCode
     * @param resultMsg
     * @param resultData
     * @param e
     */
    public ResultMsg(int resultCode, String resultMsg, T resultData, Exception e) {
        this.resultCode = resultCode;
        this.resultData = resultData;
        this.resultMsg = resultMsg;
        this.exception = e;
    }

    /**
     * @return 返回封装类
     * @Author deng1
     * @Description 默认成功返回值
     * @Date 2019/11/26 上午 10:12
     **/
    public static final ResultMsg defaultSuccessResponse() {
        ResultMsg rm = new ResultMsg();
        return rm.setResultCode(ResultStatusCode.OK.getResultCode()).setResultMsg(ResultEnum.SUCCESS.message());
    }

    /**
     * @return 返回封装类
     * @Author deng1
     * @Description 默认失败返回值
     * @Date 2019/11/26 上午 10:12
     **/
    public static final ResultMsg defaultErrorResponse() {
        ResultMsg rm = new ResultMsg();
        return rm.setResultCode(ResultStatusCode.ERROR.getResultCode()).setResultMsg(ResultStatusCode.ERROR.getResultMsg());
    }

    /**
     * @Author deng1
     * @Description 默认回滚返回值
     * @Date 2019/11/26 上午 10:21
     **/
    public static ResultMsg defaultFallBack() {
        ResultMsg rm = new ResultMsg();
        rm.setResultCode(ResultStatusCode.ERROR.getResultCode());
        rm.setErrorCode(ResultEnum.SYSTEM_INNER_ERROR.code());
        rm.setResultMsg(ResultEnum.SYSTEM_INNER_ERROR.message());
        return rm;
    }

    public static final ResultMsg validFailResponse() {
        ResultMsg rm = new ResultMsg();
        return rm.setResultCode(ResultStatusCode.ERROR.getResultCode()).setResultMsg("输入校验失败");
    }

    public Integer getResultCode() {
        return resultCode;
    }

    public ResultMsg<T> setResultCode(final Integer resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public String getResultMsg() {
        return resultMsg;
    }

    public ResultMsg<T> setResultMsg(final String msg) {
        this.resultMsg = msg;
        return this;
    }

    public Integer getErrorCode() {
        return errorCode;
    }

    public ResultMsg<T> setErrorCode(final Integer errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public T getResultData() {
        return resultData;
    }

    public ResultMsg<T> setResultData(final T data) {
        this.resultData = data;
        return this;
    }

    public ResultEnum getResultEnum() {
        return resultEnum;
    }

    public ResultMsg<T> setResultEnum(final Exception exception) {
        this.exception = exception;
        return this;
    }

    public Exception getException() {
        return exception;
    }

    public ResultMsg<T> getException(final ResultEnum resultEnum) {
        this.resultEnum = resultEnum;
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"resultCode\":")
                .append(resultCode);
        sb.append(",\"resultMsg\":\"")
                .append(resultMsg).append('\"');
        sb.append(",\"resultData\":")
                .append(resultData);
        sb.append(",\"resultEnum\":")
                .append(resultEnum);
        sb.append(",\"exception\":")
                .append(exception);
        sb.append('}');
        return sb.toString();
    }
}
