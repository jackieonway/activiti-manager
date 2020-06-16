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
 * @version $id: ActWorkFlowInstanceController.java v 0.1 2020-03-18 16:23 Jackie Exp $$
 */
@CrossOrigin
@RestController
@RequestMapping("/workFlow/instance")
@Api(value = "工作流流程实例相关接口", tags = "工作流流程实例相关接口")
public class ActWorkFlowInstanceController {

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

}
