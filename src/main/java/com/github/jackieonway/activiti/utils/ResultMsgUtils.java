/*
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.utils;

import com.baomidou.mybatisplus.plugins.pagination.Pagination;
import com.github.jackieonway.activiti.config.emum.ResultStatusCode;
import com.github.jackieonway.activiti.utils.page.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Jackie
 * @version $id: ResultMsgUtils.java v 0.1 2020-03-27 13:52 Jackie Exp $$
 */
@Slf4j
public class ResultMsgUtils {

    private ResultMsgUtils() {
    }

    public static <T> List<T> getDataList(ResultMsg<List<T>> resultMsg) {
        if (Objects.isNull(resultMsg)) {
            return Collections.emptyList();
        }
        if (ResultStatusCode.OK.getResultCode() != resultMsg.getResultCode()) {
            log.info("getDataList 获取ResultData错误...,错误原因:[{}]", resultMsg.getResultMsg());
            return Collections.emptyList();
        }
        if (CollectionUtils.isEmpty(resultMsg.getResultData())) {
            log.info("getDataList 获取ResultData为空...");
            return Collections.emptyList();
        }
        return resultMsg.getResultData();
    }

    public static <T> PageResult<T> getDataPageResult(ResultMsg<PageResult<T>> resultMsg, Pagination pagination) {
        if (Objects.isNull(resultMsg)) {
            return PageResult.newEmptyResult(pagination);
        }
        if (ResultStatusCode.OK.getResultCode() != resultMsg.getResultCode()) {
            log.info("getDataPageResult 获取ResultData错误...,错误原因:[{}]", resultMsg.getResultMsg());
            return PageResult.newEmptyResult(pagination);
        }
        if (Objects.isNull(resultMsg.getResultData())) {
            log.info("getDataPageResult 获取ResultData为空...");
            return PageResult.newEmptyResult(pagination);
        }
        return resultMsg.getResultData();
    }

    public static <T, K> Map<T, K> getDataMap(ResultMsg<Map<T, K>> resultMsg) {
        if (Objects.isNull(resultMsg)) {
            return Collections.emptyMap();
        }
        if (ResultStatusCode.OK.getResultCode() != resultMsg.getResultCode()) {
            log.info("getDataMap 获取ResultData错误...,错误原因:[{}]", resultMsg.getResultMsg());
            return Collections.emptyMap();
        }
        if (CollectionUtils.isEmpty(resultMsg.getResultData())) {
            log.info("getDataMap 获取ResultData为空...");
            return Collections.emptyMap();
        }
        return resultMsg.getResultData();
    }
}
