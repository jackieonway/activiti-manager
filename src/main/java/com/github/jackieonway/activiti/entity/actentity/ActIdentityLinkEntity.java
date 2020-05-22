/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.actentity;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author Jackieonway
 * @version $id: ActIdentityLinkEntity.java v 0.1 2020-04-10 15:39 Jackie Exp $$
 */
@Data
@Accessors(chain = true)
public class ActIdentityLinkEntity {

    private String id;

    private String type;

    private String userId;

    private String groupId;

    private String taskId;

    private String processInstanceId;

    private String processDefId;
}
