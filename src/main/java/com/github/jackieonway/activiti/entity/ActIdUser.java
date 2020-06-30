package com.github.jackieonway.activiti.entity;

import java.io.Serializable;
import lombok.Data;

/**
 * act_id_user
 * @author 
 */
@Data
public class ActIdUser implements Serializable {
    private String id;

    private Integer rev;

    private String first;

    private String last;

    private String email;

    private String pwd;

    private String pictureId;

    /**
     * 租户id
     */
    private String tenantId;

    private static final long serialVersionUID = 1L;
}