package com.github.jackieonway.activiti.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * act_id_info
 * @author 
 */
@Data
public class ActIdInfo implements Serializable {
    private String id;

    private Integer rev;

    private String userId;

    private String type;

    private String key;

    private String value;

    private String parentId;

    /**
     * 租户id
     */
    private String tenantId;

    private byte[] password;

    private static final long serialVersionUID = 1L;
}