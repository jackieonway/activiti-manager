/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.actentity;

import lombok.Data;

import java.util.Date;

/**
 * @author Jackieonway
 * @version $id: ActProcessInstanceEntity.java v 0.1 2020-04-10 9:21 Jackie Exp $$
 */
@Data
public class ActProcessInstanceEntity {

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
    private String endActivityId;
    private String businessKey;
    private String startUserId;
    private String startActivityId;
    private String superProcessInstanceId;
    private String tenantId;
    private String name;
    private String localizedName;
    private String description;
    private String localizedDescription;
}
