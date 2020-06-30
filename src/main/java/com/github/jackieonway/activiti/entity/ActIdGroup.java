package com.github.jackieonway.activiti.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * act_id_group
 * @author 
 */
@Data
public class ActIdGroup implements Serializable {
    private String id;

    private Integer rev;

    private String name;

    private String type;

    /**
     * 租户id
     */
    private String tenantId;

    private static final long serialVersionUID = 1L;
}