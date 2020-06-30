package com.github.jackieonway.activiti.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * act_id_membership
 * @author 
 */
@Data
public class ActIdMembershipKey implements Serializable {
    private String userId;

    private String groupId;

    private static final long serialVersionUID = 1L;
}