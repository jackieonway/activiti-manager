/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.actentity;

import lombok.Data;

import java.util.Date;

/**
 * @author Jackieonway
 * @version $id: ActCommentEntity.java v 0.1 2020-02-25 11:05 Jackie Exp $$
 */
@Data
public class ActCommentEntity {

    /**
     * 批注id
     */
    private String id;

    /**
     * 类型
     */
    private String type;

    /**
     * 审批人id
     */
    private String userId;

    /**
     * 时间
     */
    private Date time;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 动作
     */
    private String action;

    /**
     * 内容
     */
    private String message;

    /**
     * 全内容
     */
    private String fullMessage;
}
