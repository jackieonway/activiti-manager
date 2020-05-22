package com.github.jackieonway.activiti.config.emum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @program:
 * @description: 统一返回错误码，所有错误码必须在这个文档里面定义
 * @author: Jackieonway
 * @create: 2019-04-26 10:49
 **/
@Getter
@AllArgsConstructor
public enum ResultEnum {

    /* 成功状态码 */
    SUCCESS(0, "成功"),

    /* 参数错误：10001-19999 */
    PARAM_IS_INVALID(10001, "参数无效"),
    PARAM_IS_BLANK(10002, "参数为空"),
    PARAM_TYPE_BIND_ERROR(10003, "参数类型错误"),
    PARAM_NOT_COMPLETE(10004, "参数缺失"),
    PARAM_ILLEGAL(10005, "参数不合法"),
    PARAM_TOO_LONG(10006, "参数超出规定长度"),
    PARAM_ANALYTICAL_ERROR(10007, "参数解析错误"),

    /* 用户错误：20001-29999*/
    USER_NOT_LOGGED_IN(20001, "用户未登录"),
    USER_LOGIN_ERROR(20002, "账号不存在或密码错误"),
    USER_ACCOUNT_FORBIDDEN(20003, "账号已被禁用"),
    USER_NOT_EXIST(20004, "用户不存在"),
    USER_HAS_EXISTED(20005, "用户已存在"),
    LOGIN_CREDENTIAL_EXISTED(20006, "凭证已存在"),
    USER_ADD_FAIL(20007, "用户添加失败"),
    USER_UPDATE_FAIL(20008, "用户修改失败"),
    USER_GET_INFO_FAIL(20009, "用户信息获取失败"),
    USER_UPDATE_PWD_FAIL(20010, "密码修改失败"),
    USER_REPWD_DISACCORD_ERROR(20011, "两次密码输入不一致"),
    USER_OLD_PWD_ERROR(20012, "原密码不正确"),
    USER_NEW_PWD_SAME(20013, "新密码与原密码不能相同"),
    USER_SINGLE_SESSION(20014, "您的账号重复登录，您被迫下线"),
    USER_VERIFICATION_CODE(20015, "验证码输入错误"),


    /* 业务错误：30001-39999 */
    SPECIFIED_QUESTIONED_USER_NOT_EXIST(30001, "业务错误"),

    /* 系统错误：40001-49999 */
    SYSTEM_INNER_ERROR(40001, "系统繁忙，请稍后重试"),

    /* 数据错误：50001-599999 */
    RESULE_DATA_NONE(50001, "数据未找到"),
    DATA_IS_WRONG(50002, "数据有误"),
    DATA_ALREADY_EXISTED(50003, "数据已存在"),
    DATA_TYPE_BIND_ERROR(50004, "数据类型错误"),
    DATA_CLASS_CAST_EXCEPTION(50005, "数据转型异常"),
    DATA_ANALYTICAL_ERROR(50006, "数据解析错误"),

    /* 接口错误：60001-69999 */
    INTERFACE_INNER_INVOKE_ERROR(60001, "内部系统接口调用异常"),
    INTERFACE_OUTTER_INVOKE_ERROR(60002, "外部系统接口调用异常"),
    INTERFACE_FORBID_VISIT(60003, "该接口禁止访问"),
    INTERFACE_ADDRESS_INVALID(60004, "接口地址无效"),
    INTERFACE_REQUEST_TIMEOUT(60005, "接口请求超时"),
    INTERFACE_EXCEED_LOAD(60006, "接口负载过高"),

    /* 权限错误：70001-79999 */
    PERMISSION_NO_ACCESS(70001, "无访问权限"),
    RESOURCE_EXISTED(70002, "资源已存在"),
    RESOURCE_NOT_EXISTED(70003, "资源不存在"),

    /*微服务错误*/


    /* HTTP状态码 */
    HTTP_STATUS_CONTINUE(100, "客户端应当继续发送请求"),
    HTTP_STATUS_SWITCHING_PROTOCOLS(101, "服务器已经理解了客户端的请求"),
    HTTP_STATUS_PROCESSING(102, "处理将被继续执行"),
    HTTP_STATUS_CHECKPOINT(103, "Checkpoint"),
    HTTP_STATUS_OK(200, "请求已成功"),
    HTTP_STATUS_CREATED(201, "请求已经被实现，而且有一个新的资源已经依据请求的需要而建立"),
    HTTP_STATUS_ACCEPTED(202, "服务器已接受请求，但尚未处理"),
    HTTP_STATUS_NON_AUTHORITATIVE_INFORMATION(203, "Non-Authoritative Information"),
    HTTP_STATUS_NO_CONTENT(204, "请求成功，但无任何内容返回"),
    HTTP_STATUS_RESET_CONTENT(205, "Reset Content"),
    HTTP_STATUS_PARTIAL_CONTENT(206, "Partial Content"),
    HTTP_STATUS_MULTI_STATUS(207, "Multi-Status"),
    HTTP_STATUS_ALREADY_REPORTED(208, "Already Reported"),
    HTTP_STATUS_IM_USED(226, "IM Used"),
    HTTP_STATUS_MULTIPLE_CHOICES(300, "Multiple Choices"),
    HTTP_STATUS_MOVED_PERMANENTLY(301, "Moved Permanently"),
    HTTP_STATUS_FOUND(302, "Found"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_MOVED_TEMPORARILY(302, "Moved Temporarily"),
    HTTP_STATUS_SEE_OTHER(303, "See Other"),
    HTTP_STATUS_NOT_MODIFIED(304, "Not Modified"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_USE_PROXY(305, "Use Proxy"),
    HTTP_STATUS_TEMPORARY_REDIRECT(307, "Temporary Redirect"),
    HTTP_STATUS_PERMANENT_REDIRECT(308, "Permanent Redirect"),
    HTTP_STATUS_BAD_REQUEST(400, "当前请求无法被服务器理解"),
    HTTP_STATUS_UNAUTHORIZED(401, "当前请求无权限"),
    HTTP_STATUS_PAYMENT_REQUIRED(402, "Payment Required"),
    HTTP_STATUS_FORBIDDEN(403, "服务器已经理解请求，但是拒绝执行它"),
    HTTP_STATUS_NOT_FOUND(404, "请求资源不存在"),
    HTTP_STATUS_METHOD_NOT_ALLOWED(405, "不允许的方法"),
    HTTP_STATUS_NOT_ACCEPTABLE(406, "请求的资源无法满足请求头中的条件"),
    HTTP_STATUS_PROXY_AUTHENTICATION_REQUIRED(407, "Proxy Authentication Required"),
    HTTP_STATUS_REQUEST_TIMEOUT(408, "请求超时"),
    HTTP_STATUS_CONFLICT(409, "Conflict"),
    HTTP_STATUS_GONE(410, "Gone"),
    HTTP_STATUS_LENGTH_REQUIRED(411, "Length Required"),
    HTTP_STATUS_PRECONDITION_FAILED(412, "Precondition Failed"),
    HTTP_STATUS_PAYLOAD_TOO_LARGE(413, "Payload Too Large"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_REQUEST_ENTITY_TOO_LARGE(413, "Request Entity Too Large"),
    HTTP_STATUS_URI_TOO_LONG(414, "URI Too Long"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_REQUEST_URI_TOO_LONG(414, "Request-URI Too Long"),
    HTTP_STATUS_UNSUPPORTED_MEDIA_TYPE(415, "Unsupported Media Type"),
    HTTP_STATUS_REQUESTED_RANGE_NOT_SATISFIABLE(416, "Requested range not satisfiable"),
    HTTP_STATUS_EXPECTATION_FAILED(417, "Expectation Failed"),
    HTTP_STATUS_I_AM_A_TEAPOT(418, "I'm a teapot"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_INSUFFICIENT_SPACE_ON_RESOURCE(419, "Insufficient Space On Resource"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_METHOD_FAILURE(420, "Method Failure"),
    /**
     * @deprecated
     */
    @Deprecated
    HTTP_STATUS_DESTINATION_LOCKED(421, "Destination Locked"),
    HTTP_STATUS_UNPROCESSABLE_ENTITY(422, "Unprocessable Entity"),
    HTTP_STATUS_LOCKED(423, "Locked"),
    HTTP_STATUS_FAILED_DEPENDENCY(424, "Failed Dependency"),
    HTTP_STATUS_UPGRADE_REQUIRED(426, "Upgrade Required"),
    HTTP_STATUS_PRECONDITION_REQUIRED(428, "要求先决条件"),
    HTTP_STATUS_TOO_MANY_REQUESTS(429, "太多请求"),
    HTTP_STATUS_REQUEST_HEADER_FIELDS_TOO_LARGE(431, "请求头字段太大"),
    HTTP_STATUS_UNAVAILABLE_FOR_LEGAL_REASONS(451, "Unavailable For Legal Reasons"),
    HTTP_STATUS_INTERNAL_SERVER_ERROR(500, "服务器内部错误"),
    HTTP_STATUS_NOT_IMPLEMENTED(501, "服务器不支持当前请求"),
    HTTP_STATUS_BAD_GATEWAY(502, "转发请求收到无效响应"),
    HTTP_STATUS_SERVICE_UNAVAILABLE(503, "服务器当前无法处理请求"),
    HTTP_STATUS_GATEWAY_TIMEOUT(504, "转发请求未收到响应"),
    HTTP_STATUS_HTTP_VERSION_NOT_SUPPORTED(505, "HTTP版本不支持"),
    HTTP_STATUS_VARIANT_ALSO_NEGOTIATES(506, "Variant Also Negotiates"),
    HTTP_STATUS_INSUFFICIENT_STORAGE(507, "服务器无法存储完成请求所必须的内容"),
    HTTP_STATUS_LOOP_DETECTED(508, "Loop Detected"),
    HTTP_STATUS_BANDWIDTH_LIMIT_EXCEEDED(509, "服务器达到带宽限制"),
    HTTP_STATUS_NOT_EXTENDED(510, "获取资源所需要的策略并没有被满足"),
    HTTP_STATUS_NETWORK_AUTHENTICATION_REQUIRED(511, "要求网络认证");

    private Integer code;

    private String msg;

}

