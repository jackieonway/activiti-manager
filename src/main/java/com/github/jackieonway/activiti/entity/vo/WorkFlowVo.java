/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * @author Jackieonway
 * @version $id: WorkFlowRequest.java v 0.1 2020-02-20 10:11 Jackie Exp $$
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("工作流视图实体")
public class WorkFlowVo {

    /**
     * 业务键
     */
    @ApiModelProperty("业务键")
    private String businessKey;

    /**
     * 流程执行实例id
     */
    @ApiModelProperty("流程执行实例id")
    private String processId;

    /**
     * 流程实例id
     */
    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**
     * 流程任务id
     */
    @ApiModelProperty("流程任务id")
    private String taskId;

    /**
     * 流程定义键
     */
    @ApiModelProperty("流程定义键")
    private String processDefinationKey;

    /**
     * 流程变量
     */
    @ApiModelProperty("流程变量")
    private Map<String, Object> variables;

    /**
     * 流程定义Id
     */
    @ApiModelProperty("流程定义Id")
    private String processDefinationId;

    /**
     * 部署id
     */
    @ApiModelProperty("部署id")
    private String deployId;

    /**
     * 部署名称
     */
    @ApiModelProperty("部署名称")
    private String deployName;
}
