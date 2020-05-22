/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.actentity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author Jackieonway
 * @version $id: ActHistoryTaskEntity.java v 0.1 2020-02-25 11:37 Jackie Exp $$
 */
@Data
@Accessors(chain = true)
public class ActHistoryTaskEntity {

    private String id;
    private String processInstanceId;
    private String processDefinitionId;
    private String processDefinitionKey;
    private String processDefinitionName;
    private Integer processDefinitionVersion;
    private String deploymentId;
    private Date startTime;
    private Date endTime;
    private Long durationInMillis;
    private String deleteReason;
    private String executionId;
    private String name;
    private String localizedName;
    private String parentTaskId;
    private String description;
    private String localizedDescription;
    private String owner;
    private String assignee;
    private String taskDefinitionKey;
    private String formKey;
    private int priority;
    private Date dueDate;
    private Date claimTime;
    private String category;
    private String tenantId;
    private String businessKey;
    private List<ActCommentEntity> comments;

}
