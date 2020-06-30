package com.github.jackieonway.activiti.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * act_id_membership
 * @author 
 */
@Data
public class ActIdMembership extends ActIdMembershipKey implements Serializable {
    /**
     * 租户id
     */
    private String tenantId;

    private static final long serialVersionUID = 1L;
}