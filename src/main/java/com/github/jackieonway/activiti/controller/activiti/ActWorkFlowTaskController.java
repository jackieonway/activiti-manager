/**
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.controller.activiti;

import com.github.jackieonway.activiti.common.ValidatedGroup;
import com.github.jackieonway.activiti.entity.actentity.*;
import com.github.jackieonway.activiti.entity.request.WorkFlowRequest;
import com.github.jackieonway.activiti.service.ActWorkFlowService;
import com.github.jackieonway.activiti.utils.ResultMsg;
import com.github.jackieonway.activiti.utils.page.PageResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Jackie
 * @version $id: ActWorkFlowTaskController.java v 0.1 2020-06-16 10:20 Jackie Exp $$
 */

@CrossOrigin
@RestController
@RequestMapping("/workFlow/task")
@Api(value = "工作流流程任务相关接口", tags = "工作流流程任务相关接口")
public class ActWorkFlowTaskController {

    @Resource
    private ActWorkFlowService actWorkFlowService;


    @ApiOperation(value = "执行流程", notes = "必传参数: userUuid: 办理用户Id,  businessKey: 业务键, " +
            "tenant: 租户, comment: 审批意见, variables:流程变量(Map) ,可选参数: assignee: 流程办理人")
    @PostMapping("/doTask")
    public ResultMsg doTask(@RequestBody @Validated({ValidatedGroup.TenantGroup.class, ValidatedGroup.UpdateGroup.class,
            ValidatedGroup.BusinessKeyGroup.class, ValidatedGroup.UserIdGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.doTask(workFlowRequest);
    }


    /**
     * 查询当前用户的待办任务
     */
    @ApiOperation(value = "查询当前用户的待办任务", notes = "必传参数: userUuid: 办理用户Id, tenant: 租户, " +
            "queryConditionBean: 分页, 可选参数:  businessKey: 业务键")
    @PostMapping("/queryUsersToDoTasks")
    public ResultMsg<PageResult<ActTaskEntity>> queryUsersToDoTasks(
            @RequestBody @Validated({ValidatedGroup.UserIdGroup.class, ValidatedGroup.QueryConditionBean.class,
                    ValidatedGroup.TenantGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.queryToDoTasks(workFlowRequest);
    }

    /**
     * 查询所有待办任务
     */
    @ApiOperation(value = "查询所有待办任务", notes = "必传参数:  tenant: 租户," +
            " queryConditionBean: 分页, 可选参数 businessKey: 业务键")
    @PostMapping("/queryAllToDoTasks")
    public ResultMsg<PageResult<ActTaskEntity>> queryToDoTasks(
            @RequestBody @Validated({ValidatedGroup.QueryConditionBean.class,
                    ValidatedGroup.TenantGroup.class}) WorkFlowRequest workFlowRequest) {
        workFlowRequest.setUserUuid(null);
        return actWorkFlowService.queryToDoTasks(workFlowRequest);
    }
    /**
     * 查询审批批注
     */
    @ApiOperation(value = "查询审批批注", notes = "必传参数: businessKey: 业务键, tenant: 租户, queryConditionBean : 分页")
    @PostMapping("/queryComments")
    public ResultMsg<PageResult<ActCommentEntity>> queryComments(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.QueryConditionBean.class, ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.queryComments(workFlowRequest);
    }

    /**
     * 查询当前用户的已办任务
     */
    @ApiOperation(value = "查询当前用户的已办任务", notes = "必传参数: userUuid: 办理用户Id, tenant: 租户, queryConditionBean : 分页")
    @PostMapping("/queryCurrentUserHistoryTasks")
    public ResultMsg<PageResult<ActHistoryTaskEntity>> queryCurrentUserHistoryTasks(
            @RequestBody @Validated({ValidatedGroup.TenantGroup.class, ValidatedGroup.UserIdGroup.class,
                    ValidatedGroup.QueryConditionBean.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.queryHistoryTasks(workFlowRequest);
    }

    /**
     * 查询当前用户的已办任务
     */
    @ApiOperation(value = "查询所有已办任务", notes = "必传参数: tenant: 租户, queryConditionBean : 分页")
    @PostMapping("/queryAllHistoryTasks")
    public ResultMsg<PageResult<ActHistoryTaskEntity>> queryHistoryTasks(
            @RequestBody @Validated({ValidatedGroup.TenantGroup.class,
                    ValidatedGroup.QueryConditionBean.class}) WorkFlowRequest workFlowRequest) {
        workFlowRequest.setUserUuid(null);
        return actWorkFlowService.queryHistoryTasks(workFlowRequest);
    }


    /**
     * 指派流程办理人
     */
    @ApiOperation(value = "指派流程办理人", notes = "必传参数: assignee: 流程办理人, businessKey: 业务键, tenant: 租户")
    @PostMapping("/changeUserAssignee")
    public ResultMsg changeUserAssignee(@RequestBody @Validated({ValidatedGroup.AssigneeGroup.class,
            ValidatedGroup.TenantGroup.class, ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.changeUserAssignee(workFlowRequest);
    }

    /**
     * 获取表单地址
     */
    @ApiOperation(value = "获取表单地址", notes = "必传参数: businessKey: 业务键, tenant: 租户")
    @PostMapping("/queryFormKey")
    public ResultMsg<String> queryFormKey(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.queryFormKey(workFlowRequest);
    }

    /**
     * 查询流程候选人集合
     */
    @ApiOperation(value = "查询流程候选人集合", notes = "必传参数: tenant: 租户, businessKey: 业务键")
    @PostMapping("/queryCandidateUsers")
    public ResultMsg<List<ActIdentityLinkEntity>> queryCandidateUsers(
            @RequestBody @Validated({ValidatedGroup.TenantGroup.class,
                    ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.queryCandidateUsers(workFlowRequest);
    }

    /**
     * 拾取用户任务
     * @return
     */
    @ApiOperation(value = "拾取用户任务", notes = "必传参数: tenant: 租户, businessKey: 业务键, assignee: 流程办理人")
    @PostMapping("/claimTask")
    public ResultMsg claimTask(
            @RequestBody @Validated({ValidatedGroup.TenantGroup.class, ValidatedGroup.AssigneeGroup.class,
                    ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.claimTask(workFlowRequest);
    }

    /**
     * 撤销用户任务
     * @return
     */
    @ApiOperation(value = "撤销用户任务", notes = "必传参数: tenant: 租户, businessKey: 业务键, userUuid: 办理人id ")
    @PostMapping("/backTask")
    public ResultMsg backTask(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class,ValidatedGroup.UserIdGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.backTask(workFlowRequest);
    }

    /**
     * 回退用户任务
     * @return
     */
    @ApiOperation(value = "回退用户任务", notes = "必传参数: tenant: 租户, businessKey: 业务键")
    @PostMapping("/rejectTask")
    public ResultMsg rejectTask(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.rejectTask(workFlowRequest);
    }
}
