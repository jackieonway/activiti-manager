package com.github.jackieonway.activiti.config.emum;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * resultCode枚举类
 *
 * @author :Jackieonway
 */
@Getter
@AllArgsConstructor
public enum ResultStatusCode {

    /**
     * OK
     */
    OK(0, "OK"),
    /**
     * ERROR
     */
    ERROR(-1, "ERROR");

    private Integer code;
    private String msg;

}
