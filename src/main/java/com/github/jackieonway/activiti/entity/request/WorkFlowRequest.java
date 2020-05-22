/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.entity.request;

import com.github.jackieonway.activiti.common.ValidatedGroup;
import com.github.jackieonway.activiti.utils.page.QueryConditionBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * @author Jackieonway
 * @version $id: WorkFlowRequest.java v 0.1 2020-02-20 10:11 Jackie Exp $$
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
@ApiModel("工作流视图实体")
public class WorkFlowRequest {

    /**
     * 业务键
     */
    @ApiModelProperty("业务键")
    @NotBlank(message = "业务键不能为空", groups = ValidatedGroup.BusinessKeyGroup.class)
    private String businessKey;

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
     * 部署名称
     */
    @ApiModelProperty("部署名称")
    private String deployName;

    /**
     * 流程分类
     */
    @ApiModelProperty("流程分类")
    private String category;
    /**
     * 分页
     */
    @ApiModelProperty("分页")
    @Valid
    @NotNull(message = "queryConditionBean不能为空", groups = ValidatedGroup.QueryConditionBean.class)
    private QueryConditionBean queryConditionBean;

    /**
     * 批注信息/原因
     */
    @ApiModelProperty("批注信息/原因")
    @NotBlank(message = "queryConditionBean不能为空", groups = ValidatedGroup.UpdateGroup.class)
    private String comment;

    /**
     * 办理用户id
     */
    @ApiModelProperty("办理用户id")
    @NotBlank(message = "办理用户id不能为空", groups = ValidatedGroup.UserIdGroup.class)
    private String userUuid;

    /**
     * 流程办理人
     */
    @ApiModelProperty("流程办理人")
    @NotBlank(message = "流程办理人不能为空", groups = ValidatedGroup.AssigneeGroup.class)
    private String assignee;

    /**
     * 候选人列表
     */
    @ApiModelProperty("候选人列表")
    @NotBlank(message = "候选人列表不能为空", groups = ValidatedGroup.CandidateUsersGroup.class)
    private List<String> candidateUsers;

    /**
     * 租户
     */
    @ApiModelProperty("租户")
    @NotBlank(message = "租户不能为空", groups = ValidatedGroup.TenantGroup.class)
    private String tenant;

    /**
     * 流程名字
     */
    @ApiModelProperty("流程名字")
    private String processName;
}
