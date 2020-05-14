/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.actentity;

import lombok.Data;

import java.util.Date;

/**
 * @author Jackieonway
 * @version $id: ActDeployment.java v 0.1 2020-04-09 11:27 Jackie Exp $$
 */
@Data
public class ActDeployment {
    private String id;
    private String name;
    private String category;
    private String tenantId;
    private Date deploymentTime;
    private boolean isNew;
}
