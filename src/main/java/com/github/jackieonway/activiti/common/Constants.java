package com.github.jackieonway.activiti.common;

/**
 * 常量接口
 * @author Jackieonway
 */
public interface Constants {

    /**
     * 操作失败的统一返回消息内容
     */
    String ERROR_MSG = "操作失败！";

    /**
     * id校验
     */
    String ID_REGX = "^[0-9a-zA-Z-_]{1,32}$";

    /**
     * 逗号
     */
    String COMMA = ",";
    /**
     * 默认日期时间格式
     */
    String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 默认日期格式
     */
    String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";
    /**
     * 默认时间格式
     */
    String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    /**
     * 逻辑已删除(字符串）
     */
    String LOGIC_DELETE_STR = "1";
    /**
     * 逻辑已删除
     */
    Integer LOGIC_DELETE = 1;
    /**
     * 逻辑未删除
     */
    Integer LOGIC_NOT_DELETE = 0;
    /**
     * 日期时间格式
     */
    String DATE_TIME_FORMAT = "HH:mm";

    /**
     * rest Template 连接超时
     */
    int REST_CONNECT_TIMEOUT = 3000;

    /**
     * rest Template 读写超时
     */
    int REST_READ_TIMEOUT = 30000;

    /**
     * 以后需要添加模板的下载地址
     */
    String TEMPLATE = "templates/export_template.xlsx";

    /**
     * 字符长度常量
     */
    Integer CHAR_LENGTH = 200;

    /**
     * 批量上传最大文件个数
     */
    Integer MAX_FILE_NUM = 4;

    /**
     * 批量上传的数据正文内容从第几行开始读取
     */
    Integer ROW_INDEX = 4;

    /**
     * 表单填报校验保存操作
     */
    String FORMFILL_SAVE_CHECK = "1";

    /**
     * 表单填报校验修改操作
     */
    String FORMFILL_UPDATE_CHECK = "2";


    Integer VERSION_MUN_MAX = 10;

    /**
     * 每月第一天
     */
    Integer FIRSTDAY_OF_MOUTH = 01;

    /**
     * 横线
     */
    String LINE = "-";

    /**
     * 数字常量2
     */
    Integer NUM_TWO = 2;

    /**
     * excel文件上传类型，批量/单个
     */
    String BATCH = "batch";
    String SINGLE = "single";
}
