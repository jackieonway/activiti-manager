/*
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.controller.activiti;

import com.github.jackieonway.activiti.common.ValidatedGroup;
import com.github.jackieonway.activiti.entity.actentity.*;
import com.github.jackieonway.activiti.entity.request.WorkFlowRequest;
import com.github.jackieonway.activiti.entity.vo.WorkFlowVo;
import com.github.jackieonway.activiti.service.ActWorkFlowService;
import com.github.jackieonway.activiti.utils.ResultMsg;
import com.github.jackieonway.activiti.utils.page.PageResult;
import io.swagger.annotations.*;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import java.io.IOException;
import java.util.List;


/**
 * @author Jackieonway
 * @version $id: ActWorkFlowController.java v 0.1 2020-03-18 16:23 Jackie Exp $$
 */
@CrossOrigin
@RestController
@RequestMapping("/workFlow")
@Api(value = "工作流相关接口", tags = "工作流相关接口")
public class ActWorkFlowController {

    @Resource
    private ActWorkFlowService actWorkFlowService;

    @ApiOperation(value = "根据流程图Zip压缩文件部署流程定义", notes = "必传参数: file : 流程图zip压缩包, " +
            "tenant: 租户,可选参数: category:流程分类")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenant", paramType = "query", value = "租户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "category", paramType = "query", value = "流程分类", required = false, dataType = "String")})
    @PostMapping(value = "/deployByZip",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResultMsg<WorkFlowVo> deployByZip(MultipartFile file, @RequestParam
    @NotBlank(message = "租户信息不能为空") String tenant, @RequestParam(required = false) String category) throws IOException {
        return actWorkFlowService.deployByZip(file, tenant, category);
    }

    @ApiOperation(value = "查询已部署流程", notes = "必传参数: queryConditionBean: 分页, tenant: 租户," +
            "可选参数: category:流程分类, deployName: 部署名称")
    @PostMapping("/queryAllDeployments")
    public ResultMsg<PageResult<ActDeployment>> queryAllDeployments(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.QueryConditionBean.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.queryAllDeployments(workFlowRequest);
    }

    @ApiOperation(value = "启动流程", notes = "必传参数: businessKey: 业务键, tenant: 租户, " +
            "processDefinationKey: 流程定义键,variables:流程变量(Map) ,可选参数: processName: 流程名称")
    @PostMapping("/startWorkFlow")
    public ResultMsg<WorkFlowVo> startWorkFlow(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.startWorkFlow(workFlowRequest);
    }

    @ApiOperation(value = "终止流程", notes = "必传参数: businessKey: 业务键, tenant: 租户, comment: 审批意见 ")
    @PostMapping("/stopWorkFlow")
    public ResultMsg stopWorkFlow(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class, ValidatedGroup.UpdateGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.stopWorkFlow(workFlowRequest);
    }

    @ApiOperation(value = "暂停流程", notes = "必传参数:  businessKey: 业务键, tenant: 租户")
    @PostMapping("/suspendWorkFlow")
    public ResultMsg suspendWorkFlow(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.suspendWorkFlow(workFlowRequest);
    }

    @ApiOperation(value = "激活流程", notes = "必传参数: businessKey: 业务键, tenant: 租户")
    @PostMapping("/activateWorkFlow")
    public ResultMsg activateWorkFlow(@RequestBody @Validated({ValidatedGroup.TenantGroup.class,
            ValidatedGroup.BusinessKeyGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.activateWorkFlow(workFlowRequest);
    }

    @ApiOperation(value = "执行流程", notes = "必传参数: userUuid: 办理用户Id,  businessKey: 业务键, " +
            "tenant: 租户, comment: 审批意见, variables:流程变量(Map) ,可选参数: assignee: 流程办理人")
    @PostMapping("/doTask")
    public ResultMsg doTask(@RequestBody @Validated({ValidatedGroup.TenantGroup.class, ValidatedGroup.UpdateGroup.class,
            ValidatedGroup.BusinessKeyGroup.class, ValidatedGroup.UserIdGroup.class}) WorkFlowRequest workFlowRequest) {
        return actWorkFlowService.doTask(workFlowRequest);
    }

    /**
     * 查看流程图
     */
    @ApiOperation(value = "查看流程图", notes = "必传参数: tenant: 租户， businessKey: 业务键")
    @GetMapping("/showWorkFlowImage/{tenant}/{businessKey}")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenant", paramType = "path", value = "租户", required = true, dataType = "String"),
            @ApiImplicitParam(name = "businessKey", paramType = "path", value = "业务键", required = true, dataType = "String")
    })
    public void showWorkFlowImage(@PathVariable(name = "tenant") String tenant,
                                  @PathVariable(name = "businessKey")   String businessKey,
                                  HttpServletResponse response) throws IOException {
        actWorkFlowService.showWorkFlowImage(businessKey, tenant, response);
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
     * 改变流程办理人
     */
    @ApiOperation(value = "改变流程办理人", notes = "必传参数: assignee: 流程办理人, businessKey: 业务键, tenant: 租户")
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
     * 查询未完成的流程集合
     */
    @ApiOperation(value = "查询未完成的流程集合", notes = "必传参数: tenant: 租户, " +
            "queryConditionBean: 分页,可选参数: processName: 流程名称")
    @PostMapping("/queryUnfinishedProcessInstance")
    public ResultMsg<PageResult<ActProcessInstanceEntity>> queryUnfinishedProcessInstance(
            @RequestBody @Validated({ValidatedGroup.TenantGroup.class,
                    ValidatedGroup.QueryConditionBean.class}) WorkFlowRequest workFlowRequest) {
        workFlowRequest.setUserUuid(null);
        return actWorkFlowService.queryUnfinishedProcessInstance(workFlowRequest);
    }

    /**
     * 查询未完成的流程集合
     */
    @ApiOperation(value = "查询已完成的流程集合", notes = "必传参数: tenant: 租户, " +
            "queryConditionBean: 分页,可选参数: processName: 流程名称")
    @PostMapping("/queryFinishedProcessInstance")
    public ResultMsg<PageResult<ActProcessInstanceEntity>> queryFinishedProcessInstance(
            @RequestBody @Validated({ValidatedGroup.TenantGroup.class,
                    ValidatedGroup.QueryConditionBean.class}) WorkFlowRequest workFlowRequest) {
        workFlowRequest.setUserUuid(null);
        return actWorkFlowService.queryFinishedProcessInstance(workFlowRequest);
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
