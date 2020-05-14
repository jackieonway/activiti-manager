package com.github.jackieonway.activiti.handler;

import com.github.jackieonway.activiti.common.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Jackie
 * @description 业务异常类
 * @date 2019年10月15日 09:59:27
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -6990655293380293378L;
    private String message;

    public BusinessException() {
        super();
        setMessage(Constants.ERROR_MSG);
    }

    public BusinessException(String message) {
        super(message);
        setMessage(message);
    }

    public BusinessException(String message, Exception e) {
        super(message, e);
        setMessage(message);
    }
}
