/*
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.jackieonway.activiti.entity.actentity.*;
import com.github.jackieonway.activiti.handler.ServiceException;
import com.github.jackieonway.activiti.service.ActWorkFlowService;
import com.github.jackieonway.activiti.utils.*;
import com.github.jackieonway.activiti.utils.page.PageResult;
import com.github.jackieonway.activiti.utils.page.QueryConditionBean;
import com.github.jackieonway.activiti.config.images.CustomProcessDiagramGenerator;
import com.github.jackieonway.activiti.config.images.WorkflowConstants;
import com.github.jackieonway.activiti.entity.request.WorkFlowRequest;
import com.github.jackieonway.activiti.entity.vo.WorkFlowVo;
import com.github.jackieonway.activiti.handler.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.engine.*;
import org.activiti.engine.form.TaskFormData;
import org.activiti.engine.history.*;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmActivity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.impl.pvm.process.TransitionImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.DeploymentBuilder;
import org.activiti.engine.repository.DeploymentQuery;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.Execution;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.runtime.ProcessInstanceQuery;
import org.activiti.engine.task.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.ZipInputStream;

/**
 * @author Jackie
 * @version $id: ActWorkFlowServiceImpl.java v 0.1 2020-02-20 10:13 Jackie Exp $$
 */
@Service
@Slf4j
public class ActWorkFlowServiceImpl implements ActWorkFlowService {

    private static final String CLAIM_TASK_FAIL = "拾取任务失败";
    @Resource
    private RepositoryService repositoryService;

    @Resource
    private RuntimeService runtimeService;

    @Resource
    private HistoryService historyService;

    @Resource
    private ProcessEngineConfiguration processEngineConfiguration;

    @Resource
    private TaskService taskService;

    @Resource
    private FormService formService;

    @Value("#{${activiti.tenants}}")
    private Map<String, String> tenants;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg<WorkFlowVo> deployByZip(MultipartFile file, String tenant, String category) throws IOException {
        checkTenantExists(tenant);
        if (Objects.isNull(file)) {
            log.info("租户[{}]流程定义压缩文件为空", tenant);
            return ResponseUtils.fail("请上传流程定义压缩文件");
        }
        String originalFilename = file.getOriginalFilename();
        String deployName;
        if (StringUtils.isBlank(originalFilename)) {
            deployName = CommonUtil.uuidString();
        } else {
            deployName = originalFilename.substring(0, originalFilename.lastIndexOf('.'));
        }
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment().name(deployName);
        if (StringUtils.isNotBlank(category)) {
            deploymentBuilder.category(category);
        }
        Deployment deployment = deploymentBuilder.tenantId(tenant)
                .addZipInputStream(new ZipInputStream(file.getInputStream())).deploy();
        WorkFlowVo workFlowVo = new WorkFlowVo();
        workFlowVo.setDeployId(deployment.getId());
        workFlowVo.setDeployName(deployment.getName());
        return ResponseUtils.success(workFlowVo);
    }

    @Override
    public ResultMsg<PageResult<ActDeployment>> queryAllDeployments(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        QueryConditionBean queryConditionBean = workFlowRequest.getQueryConditionBean();
        PageResult<ActDeployment> pageResult =
                PageResult.newEmptyResult(queryConditionBean.getPageNum(), queryConditionBean.getPageSize());
        DeploymentQuery deploymentQuery =
                repositoryService.createDeploymentQuery().deploymentTenantId(workFlowRequest.getTenant());
        if (StringUtils.isNotBlank(workFlowRequest.getDeployName())) {
            deploymentQuery.deploymentNameLike(concatLike(workFlowRequest.getDeployName()));
        }
        if (StringUtils.isNotBlank(workFlowRequest.getCategory())) {
            deploymentQuery.deploymentCategory(workFlowRequest.getCategory());
        }
        List<Deployment> deployments = deploymentQuery.orderByDeploymenTime().desc()
                .listPage(queryConditionBean.getStartIndex(), queryConditionBean.getPageSize());
        if (CollectionUtils.isEmpty(deployments)) {
            return ResponseUtils.success(pageResult);
        }
        pageResult.setTotalCount(deploymentQuery.count());
        pageResult.setList(convertList(deployments, ActDeployment.class));
        return ResponseUtils.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg<WorkFlowVo> startWorkFlow(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        if (Objects.nonNull(processInstance)) {
            log.info("租户[{}]业务[{}]已经在审批或审批完成", workFlowRequest.getTenant(), workFlowRequest.getBusinessKey());
            return ResponseUtils.fail("当前业务已经在审批或审批完成");
        }
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(workFlowRequest.getProcessDefinationKey())
                .processDefinitionTenantId(workFlowRequest.getTenant()).list();
        if (CollectionUtils.isEmpty(list)) {
            log.info("租户[{}]业务[{}]启动工作流失败，流程定义不存在",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey());
            return ResponseUtils.fail("流程不存在请核对后再试");
        }
        ProcessInstance process =
                runtimeService.startProcessInstanceByKeyAndTenantId(workFlowRequest.getProcessDefinationKey(),
                workFlowRequest.getBusinessKey(), workFlowRequest.getVariables(), workFlowRequest.getTenant());
        if (StringUtils.isNotBlank(workFlowRequest.getProcessName())) {
            runtimeService.setProcessInstanceName(process.getProcessInstanceId(), workFlowRequest.getProcessName());
        }
        WorkFlowVo workFlowVo = new WorkFlowVo();
        workFlowVo.setProcessDefinationId(process.getProcessDefinitionId());
        workFlowVo.setProcessInstanceId(process.getProcessInstanceId());
        workFlowVo.setProcessId(process.getId());
        return ResponseUtils.success(workFlowVo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg stopWorkFlow(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        runtimeService.deleteProcessInstance(processInstance.getId(), workFlowRequest.getComment());
        return ResponseUtils.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg suspendWorkFlow(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        runtimeService.suspendProcessInstanceById(processInstance.getId());
        return ResponseUtils.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg activateWorkFlow(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        runtimeService.activateProcessInstanceById(processInstance.getId());
        return ResponseUtils.success();
    }

    @Override
    public void showWorkFlowImage(String businessKey, String tenant, HttpServletResponse response) throws IOException {
        if (StringUtils.isBlank(businessKey)) {
            log.info("参数[tenant]为空");
            throw new ServiceException("参数[tenant]为空");
        }
        if (StringUtils.isBlank(businessKey)) {
            log.info("租户[{}]参数[businessKey]为空", tenant);
            throw new ServiceException("参数[businessKey]为空");
        }
        checkTenantExists(tenant);
        HistoricProcessInstance processInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey).processInstanceTenantId(tenant).singleResult();
        if (Objects.isNull(processInstance)) {
            log.info("租户[{}]业务[{}]流程图不存在", tenant, businessKey);
            throw new ServiceException("流程图不存在");
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processInstance.getProcessDefinitionId());
        ProcessDefinitionEntity definitionEntity = (ProcessDefinitionEntity) repositoryService
                .getProcessDefinition(processInstance.getProcessDefinitionId());
        List<HistoricActivityInstance> highLightedActivitList = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInstance.getId()).activityTenantId(tenant)
                .orderByHistoricActivityInstanceStartTime().asc().list();
        // 高亮环节id集合
        List<String> highLightedActivitis = new ArrayList<>();
        // 高亮线路id集合
        List<String> highLightedFlows = getHighLightedFlows(definitionEntity, highLightedActivitList);
        for (HistoricActivityInstance tempActivity : highLightedActivitList) {
            String activityId = tempActivity.getActivityId();
            highLightedActivitis.add(activityId);
        }

        Set<String> currIds = runtimeService.createExecutionQuery()
                .processInstanceId(processInstance.getId()).executionTenantId(tenant).list()
                .stream().map(Execution::getActivityId).collect(Collectors.toSet());

        CustomProcessDiagramGenerator diagramGenerator = (CustomProcessDiagramGenerator) processEngineConfiguration
                .getProcessDiagramGenerator();
        InputStream imageStream = diagramGenerator.generateDiagram(bpmnModel, "png", highLightedActivitis,
                highLightedFlows, "宋体", "宋体", "宋体",
                null, 1.0,
                new Color[]{WorkflowConstants.COLOR_NORMAL, WorkflowConstants.COLOR_CURRENT}, currIds);
        // 输出资源内容到相应对象
        int lenght = 1024;
        byte[] b = new byte[lenght];
        int len;
        while ((len = imageStream.read(b, 0, lenght)) != -1) {
            response.getOutputStream().write(b, 0, len);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg doTask(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        Task task = getTask(workFlowRequest.getBusinessKey(), workFlowRequest.getTenant());
        if (Objects.isNull(task)) {
            log.info("租户[{}]业务[{}]的执行任务失败，任务不存在",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey());
            return ResponseUtils.fail("没有待办任务需要完成");
        }
        if (!workFlowRequest.getUserUuid().equalsIgnoreCase(task.getAssignee())) {
            log.info("租户[{}]的业务键[{}]流程审批人为[{}],传入审批人为[{}]", workFlowRequest.getTenant(),
                    workFlowRequest.getBusinessKey(), task.getAssignee(), workFlowRequest.getUserUuid());
            return ResponseUtils.fail("当前办理人不能办理该业务");
        }
        //设批注人
        Authentication.setAuthenticatedUserId(task.getAssignee());
        taskService.addComment(task.getId(), task.getProcessInstanceId(), workFlowRequest.getComment());
        taskService.complete(task.getId(), workFlowRequest.getVariables());
        ProcessInstance processInstance1 = runtimeService.createProcessInstanceQuery()
                .processInstanceId(task.getProcessInstanceId())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        boolean flag = Objects.isNull(processInstance1)
                || StringUtils.isBlank(workFlowRequest.getAssignee())
                || CollectionUtils.isEmpty(workFlowRequest.getCandidateUsers())
                /*|| CollectionUtils.isEmpty(workFlowRequest.getCandidateGroups())*/;
        if (flag) {
            return ResponseUtils.success();
        }
        task = taskService.createTaskQuery().processInstanceId(processInstance1.getId())
                .taskTenantId(workFlowRequest.getTenant()).singleResult();
        //设置下一流程的办理人
        setAssignee(workFlowRequest, task);
//        setCandidateGroups(workFlowRequest, task);
        setCandidateUsers(workFlowRequest, task);
        return ResponseUtils.success();
    }

    @Override
    public ResultMsg<PageResult<ActTaskEntity>> queryToDoTasks(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        QueryConditionBean queryConditionBean = workFlowRequest.getQueryConditionBean();
        PageResult<ActTaskEntity> pageResult =
                PageResult.newEmptyResult(queryConditionBean.getPageNum(), queryConditionBean.getPageSize());
        //查询所有指定人的待办任务
        TaskQuery taskQuery = taskService.createTaskQuery();
        if (StringUtils.isNotBlank(workFlowRequest.getUserUuid())) {
            taskQuery.taskAssigneeLike(concatLike(workFlowRequest.getUserUuid()));
        }
        if (!CollectionUtils.isEmpty(workFlowRequest.getCandidateGroups())) {
            taskQuery.taskCandidateGroupIn(workFlowRequest.getCandidateGroups());
        }
        taskQuery.taskTenantId(workFlowRequest.getTenant());
        if (StringUtils.isNotBlank(workFlowRequest.getBusinessKey())) {
            taskQuery.processInstanceBusinessKeyLike(concatLike(workFlowRequest.getBusinessKey()));
        }
        List<Task> list = taskQuery.orderByTaskCreateTime().desc()
                .listPage(queryConditionBean.getStartIndex(), queryConditionBean.getPageSize());
        if (CollectionUtils.isEmpty(list)) {
            return ResponseUtils.success(pageResult);
        }
        pageResult.setTotalCount(taskQuery.count());
        List<ActTaskEntity> actTaskEntities = convertList(list, ActTaskEntity.class);
        actTaskEntities.forEach(actTaskEntity -> {
            ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                    .processInstanceId(actTaskEntity.getProcessInstanceId()).singleResult();
            actTaskEntity.setBusinessKey(processInstance.getBusinessKey());
        });
        pageResult.setList(actTaskEntities);
        return ResponseUtils.success(pageResult);
    }

    @Override
    public ResultMsg<PageResult<ActCommentEntity>> queryComments(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        HistoricProcessInstance historicProcessInstance = historyService.createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        List<Comment> comments = taskService.getProcessInstanceComments(historicProcessInstance.getId());
        QueryConditionBean queryConditionBean = workFlowRequest.getQueryConditionBean();
        PageResult<ActCommentEntity> pageResult = PageResult.newEmptyResult(
                queryConditionBean.getPageNum(), queryConditionBean.getPageSize());
        if (CollectionUtils.isEmpty(comments)) {
            return ResponseUtils.success(pageResult);
        }
        List<ActCommentEntity> commentEntities = new ArrayList<>();
        Integer startIndex = queryConditionBean.getStartIndex();
        int endIndex = startIndex + queryConditionBean.getPageSize();
        for (int i = startIndex; i < comments.size() && i < endIndex; i++) {
            commentEntities.add(BeanConvertUtils.convert(comments.get(i), ActCommentEntity.class));
        }
        pageResult.setList(commentEntities);
        pageResult.setTotalCount(Long.valueOf(String.valueOf(comments.size())));
        return ResponseUtils.success(pageResult);
    }

    @Override
    public ResultMsg<PageResult<ActHistoryTaskEntity>> queryHistoryTasks(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        HistoricTaskInstanceQuery historicTaskInstanceQuery = historyService.createHistoricTaskInstanceQuery();
        if (StringUtils.isNotBlank(workFlowRequest.getUserUuid())) {
            historicTaskInstanceQuery.taskAssigneeLike(concatLike(workFlowRequest.getUserUuid()));
        }
        historicTaskInstanceQuery.taskTenantId(workFlowRequest.getTenant());
        if (StringUtils.isNotBlank(workFlowRequest.getBusinessKey())) {
            historicTaskInstanceQuery.processInstanceBusinessKeyLike(concatLike(workFlowRequest.getBusinessKey()));
        }
        QueryConditionBean queryConditionBean = workFlowRequest.getQueryConditionBean();
        List<HistoricTaskInstance> historicTaskInstances =
                historicTaskInstanceQuery.orderByTaskCreateTime().desc().listPage(
                queryConditionBean.getStartIndex(), queryConditionBean.getPageSize());
        PageResult<ActHistoryTaskEntity> pageResult =
                PageResult.newEmptyResult(queryConditionBean.getPageNum(), queryConditionBean.getPageSize());
        if (CollectionUtils.isEmpty(historicTaskInstances)) {
            return ResponseUtils.success(pageResult);
        }
        pageResult.setTotalCount(historicTaskInstanceQuery.count());
        List<ActHistoryTaskEntity> actHistoryTaskEntities = convertList(historicTaskInstances,
                ActHistoryTaskEntity.class);
        actHistoryTaskEntities.forEach(actHistoryTaskEntity -> {
            HistoricProcessInstance historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery()
                            .processInstanceId(actHistoryTaskEntity.getProcessInstanceId()).singleResult();
            actHistoryTaskEntity.setBusinessKey(historicProcessInstance.getBusinessKey());
            List<Comment> taskComments = taskService.getTaskComments(actHistoryTaskEntity.getId());
            if (!CollectionUtils.isEmpty(taskComments)) {
                actHistoryTaskEntity.setComments(convertList(taskComments, ActCommentEntity.class));
            }
        });
        pageResult.setList(actHistoryTaskEntities);
        return ResponseUtils.success(pageResult);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg changeUserAssignee(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .taskTenantId(workFlowRequest.getTenant()).singleResult();
        setAssignee(workFlowRequest, task);
        setCandidateUsers(workFlowRequest, task);
        return ResponseUtils.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg claimTask(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .taskTenantId(workFlowRequest.getTenant()).singleResult();
        if (StringUtils.isNotBlank(task.getAssignee())) {
            log.info("租户[{}]业务[{}]的任务[{}]拾取失败，已有审批用户[{}]",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey(), task.getId(), task.getAssignee());
            return ResponseUtils.fail(CLAIM_TASK_FAIL);
        }
        ResultMsg<List<ActIdentityLinkEntity>> listResultMsg = this.queryCandidateUsers(workFlowRequest);
        List<ActIdentityLinkEntity> dataList = ResultMsgUtils.getDataList(listResultMsg);
        if (CollectionUtils.isEmpty(dataList)) {
            log.info("拾取任务失败,租户[{}]业务键[{}]没有候选人",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey());
            return ResponseUtils.fail("拾取任务失败,没有候选人");
        }
        List<String> users = dataList.stream().map(ActIdentityLinkEntity::getUserId).collect(Collectors.toList());
        if (!users.contains(workFlowRequest.getAssignee())) {
            log.info("拾取任务失败,租户[{}]业务键[{}]没有候选人[{}]", workFlowRequest.getTenant(),
                    workFlowRequest.getBusinessKey(), workFlowRequest.getAssignee());
            return ResponseUtils.fail(CLAIM_TASK_FAIL);
        }

        taskService.claim(task.getId(), workFlowRequest.getAssignee());
        return ResponseUtils.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResultMsg backTask(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .taskTenantId(workFlowRequest.getTenant()).singleResult();
        if (!workFlowRequest.getUserUuid().equals(task.getAssignee())) {
            log.info("租户[{}]业务[{}]的任务[{}]回滚失败，回退用户id[{}]与已有办理人id不一致[{}]",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey(),
                    task.getId(), workFlowRequest.getUserUuid(), task.getAssignee());
            return ResponseUtils.fail("回退任务失败");
        }
        ResultMsg<List<ActIdentityLinkEntity>> listResultMsg = this.queryCandidateUsers(workFlowRequest);
        if (CollectionUtils.isEmpty(ResultMsgUtils.getDataList(listResultMsg))) {
            log.info("租户[{}]业务[{}]的任务[{}]回退失败，候选人列表为空",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey(), task.getId());
            return ResponseUtils.fail("当前任务不支持回退");
        }
        taskService.setAssignee(task.getId(), null);
        return ResponseUtils.success();
    }

    @Override
    public ResultMsg<List<ActIdentityLinkEntity>> queryCandidateUsers(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(workFlowRequest.getBusinessKey())
                .processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        Task task = taskService.createTaskQuery().processInstanceId(processInstance.getProcessInstanceId())
                .taskTenantId(workFlowRequest.getTenant()).singleResult();
        List<IdentityLink> identityLinksForTask = taskService.getIdentityLinksForTask(task.getId());
        if (CollectionUtils.isEmpty(identityLinksForTask)) {
            return ResponseUtils.success(Collections.emptyList());
        }
        return ResponseUtils.success(convertList(identityLinksForTask, ActIdentityLinkEntity.class));
    }

    @Override
    public ResultMsg<String> queryFormKey(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        Task task = getTask(workFlowRequest.getBusinessKey(), workFlowRequest.getTenant());
        if (Objects.isNull(task)) {
            log.info("租户[{}]业务[{}]的查询自定义表单失败，任务不存在",
                    workFlowRequest.getTenant(), workFlowRequest.getBusinessKey());
            return ResponseUtils.fail("没有获取到自定义表单");
        }
        TaskFormData formData = formService.getTaskFormData(task.getId());
        //获取Form key的值
        if (Objects.nonNull(formData)) {
            return ResponseUtils.success(formData.getFormKey());
        }
        return ResponseUtils.success(StringUtils.EMPTY);
    }

    @Override
    public ResultMsg<PageResult<ActProcessInstanceEntity>> queryUnfinishedProcessInstance(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        QueryConditionBean queryConditionBean = workFlowRequest.getQueryConditionBean();
        HistoricProcessInstanceQuery unfinishedProcessInstanceQuery =
                historyService.createHistoricProcessInstanceQuery();
        if (StringUtils.isNotBlank(workFlowRequest.getProcessName())) {
            unfinishedProcessInstanceQuery.processInstanceNameLike(concatLike(workFlowRequest.getProcessName()));
        }
        unfinishedProcessInstanceQuery.processInstanceTenantId(workFlowRequest.getTenant()).unfinished();
        List<HistoricProcessInstance> historicProcessInstances = unfinishedProcessInstanceQuery
                .orderByProcessInstanceStartTime().desc()
                .listPage(queryConditionBean.getStartIndex(), queryConditionBean.getPageSize());
        return getPageResultResultMsg(queryConditionBean, unfinishedProcessInstanceQuery, historicProcessInstances);
    }

    @Override
    public ResultMsg<PageResult<ActProcessInstanceEntity>> queryFinishedProcessInstance(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        QueryConditionBean queryConditionBean = workFlowRequest.getQueryConditionBean();
        HistoricProcessInstanceQuery finishedProcessInstanceQuery = historyService.createHistoricProcessInstanceQuery();
        if (StringUtils.isNotBlank(workFlowRequest.getProcessName())) {
            finishedProcessInstanceQuery.processInstanceNameLike(concatLike(workFlowRequest.getProcessName()));
        }
        finishedProcessInstanceQuery.processInstanceTenantId(workFlowRequest.getTenant()).finished();
        List<HistoricProcessInstance> historicProcessInstances = finishedProcessInstanceQuery
                .orderByProcessInstanceStartTime().desc().list();
        return getPageResultResultMsg(queryConditionBean, finishedProcessInstanceQuery, historicProcessInstances);
    }

    private ResultMsg<PageResult<ActProcessInstanceEntity>> getPageResultResultMsg(QueryConditionBean queryConditionBean,
                                                       HistoricProcessInstanceQuery finishedProcessInstanceQuery,
                                                       List<HistoricProcessInstance> historicProcessInstances) {
        PageResult<ActProcessInstanceEntity> pageResult =
                PageResult.newEmptyResult(queryConditionBean.getPageNum(), queryConditionBean.getPageSize());
        if (CollectionUtils.isEmpty(historicProcessInstances)) {
            return ResponseUtils.success(pageResult);
        }
        pageResult.setTotalCount(finishedProcessInstanceQuery.count());
        pageResult.setList(convertList(historicProcessInstances, ActProcessInstanceEntity.class));
        return ResponseUtils.success(pageResult);
    }

    @Override
    public ResultMsg rejectTask(WorkFlowRequest workFlowRequest) {
        checkTenantExists(workFlowRequest.getTenant());
        ProcessInstanceQuery processInstanceQuery = runtimeService.createProcessInstanceQuery();
        processInstanceQuery.processInstanceBusinessKey(workFlowRequest.getBusinessKey());
        ProcessInstance processInstance =
                processInstanceQuery.processInstanceTenantId(workFlowRequest.getTenant()).singleResult();
        ProcessDefinitionEntity processDefinitionEntity =
                (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processInstance.getProcessDefinitionId());
        Task task = getTask(workFlowRequest.getBusinessKey(), workFlowRequest.getTenant());
        String taskDefinitionKey = task.getTaskDefinitionKey();
        ActivityImpl currentActivityImpl = processDefinitionEntity.findActivity(taskDefinitionKey);
        List<PvmActivity> activities1 = new ArrayList<>();
        getUserTask(currentActivityImpl, activities1);
        for (PvmActivity pvmActivity : activities1) {
            if (!rollBackToAssignWorkFlow(processInstance, processDefinitionEntity,
                    task, pvmActivity.getId(), currentActivityImpl)) {
                throw new BusinessException("回退任务失败");
            }
        }
        return ResponseUtils.success();
    }

    private void getUserTask(PvmActivity currentActivityImpl, List<PvmActivity> activityList) {
        List<PvmTransition> incomingTransitions = currentActivityImpl.getIncomingTransitions();
        if (CollectionUtils.isEmpty(incomingTransitions)) {
            return;
        }
        for (PvmTransition incomingTransition : incomingTransitions) {
            PvmActivity source = incomingTransition.getSource();
            if (!"userTask".equals(source.getProperty("type"))) {
                getUserTask(source, activityList);
            } else {
                activityList.add(source);
            }
        }
    }

    /**
     * 回退到指定节点
     *
     * @param processInstance  流程实例
     * @param processDefinitionEntity  流程定义
     * @param currTask  当前任务
     * @param destTaskkey  指定节点
     * @param currentActivityImpl 当前活动节点
     * @return 结果
     */
    private boolean rollBackToAssignWorkFlow(ProcessInstance processInstance,
                                             ProcessDefinitionEntity processDefinitionEntity, Task currTask,
                                             String destTaskkey, ActivityImpl currentActivityImpl) {
        try {
            // 清除当前活动的出口
            //新建一个节点连线关系集合
            //获取出口节点的关系
            List<PvmTransition> pvmTransitionList = currentActivityImpl.getOutgoingTransitions();
            List<PvmTransition> oriPvmTransitionList = new ArrayList<>(pvmTransitionList);
            pvmTransitionList.clear();

            // 建立新出口
            List<TransitionImpl> newTransitions = new ArrayList<>();
            ActivityImpl nextActivityImpl = processDefinitionEntity.findActivity(destTaskkey);
            TransitionImpl newTransition = currentActivityImpl
                    .createOutgoingTransition();
            newTransition.setDestination(nextActivityImpl);
            newTransitions.add(newTransition);
            // 完成任务
            List<Task> tasks = taskService.createTaskQuery()
                    .processInstanceId(processInstance.getId())
                    .taskDefinitionKey(currTask.getTaskDefinitionKey()).list();
            for (Task task1 : tasks) {
                taskService.complete(task1.getId(), processInstance.getProcessVariables());
                historyService.deleteHistoricTaskInstance(task1.getId());
            }
            // 恢复方向
            for (TransitionImpl transitionImpl : newTransitions) {
                currentActivityImpl.getOutgoingTransitions().remove(transitionImpl);
            }
            pvmTransitionList.addAll(oriPvmTransitionList);
            return true;
        } catch (Exception e) {
            log.error("失败", e);
            return false;
        }
    }

    private Task getTask(String getBusinessKey, String tenant) {
        List<ProcessInstance> list = runtimeService.createProcessInstanceQuery()
                .processInstanceBusinessKey(getBusinessKey).processInstanceTenantId(tenant)
                .orderByProcessInstanceId().desc()
                .list();
        if (CollectionUtils.isEmpty(list)) {
            throw new ServiceException("当前业务不存在或审批完成");
        }
        ProcessInstance processInstance = list.get(0);
        TaskQuery taskQuery = taskService.createTaskQuery().processInstanceId(processInstance.getId());
        return taskQuery.singleResult();
    }

    private String concatLike(String likeStr) {
        if (StringUtils.isNotBlank(likeStr)) {
            return "%" + likeStr + "%";
        }
        return StringUtils.EMPTY;
    }

    private <T, E> List<E> convertList(List<T> list, Class<E> clazz) {
        List<E> eList = new ArrayList<>(list.size());
            list.forEach(t -> {
            try {
                E e = clazz.newInstance();
                BeanUtils.copyProperties(t, e);
                eList.add(e);
            } catch (InstantiationException | IllegalAccessException ex1) {
                throw new ServiceException("创建 [" + clazz + "] 失败");
            }
        });
        return eList;
    }


    private void checkTenantExists(String tenant) {
        if (!tenants.containsKey(tenant)) {
            log.info("租户[{}]不存在于[{}]", tenant, JSON.toJSONString(tenants));
            throw new BusinessException("非法操作");
        }
    }

    private void setCandidateUsers(WorkFlowRequest workFlowRequest, Task task) {
        if (CollectionUtils.isEmpty(workFlowRequest.getCandidateUsers())) {
            return;
        }
        workFlowRequest.getCandidateUsers().forEach(user -> {
            taskService.addUserIdentityLink(task.getId(), user, IdentityLinkType.CANDIDATE);
            taskService.addUserIdentityLink(task.getProcessInstanceId(), user, IdentityLinkType.PARTICIPANT);
        });
    }

    private void setAssignee(WorkFlowRequest workFlowRequest, Task task) {
        if (StringUtils.isBlank(workFlowRequest.getAssignee())) {
            return;
        }
        taskService.setAssignee(task.getId(), workFlowRequest.getAssignee());
    }

    /**
     * 获取需要高亮的线
     *
     * @param processDefinitionEntity   流程定义实例
     * @param historicActivityInstances 历史活动实例集合
     * @return 集合
     */
    private List<String> getHighLightedFlows(ProcessDefinitionEntity processDefinitionEntity,
                                             List<HistoricActivityInstance> historicActivityInstances) {
        // 用以保存高亮的线flowId
        List<String> highFlows = new ArrayList<>();
        // 对历史流程节点进行遍历
        for (int i = 0; i < historicActivityInstances.size() - 1; i++) {
            ActivityImpl activityImpl = processDefinitionEntity
                    // 得到节点定义的详细信息
                    .findActivity(historicActivityInstances.get(i).getActivityId());
            // 用以保存后需开始时间相同的节点
            List<ActivityImpl> sameStartTimeNodes = new ArrayList<>();
            ActivityImpl sameActivityImpl1 = processDefinitionEntity
                    .findActivity(historicActivityInstances.get(i + 1).getActivityId());
            // 将后面第一个节点放在时间相同节点的集合里
            sameStartTimeNodes.add(sameActivityImpl1);
            // 取出节点的所有出去的线
            List<PvmTransition> pvmTransitions = activityImpl.getOutgoingTransitions();
            for (PvmTransition pvmTransition : pvmTransitions) {
                // 对所有的线进行遍历
                ActivityImpl pvmActivityImpl = (ActivityImpl) pvmTransition.getDestination();
                // 如果取出的线的目标节点存在时间相同的节点里，保存该线的id，进行高亮显示
                if (sameStartTimeNodes.contains(pvmActivityImpl)) {
                    highFlows.add(pvmTransition.getId());
                }
            }
        }
        return highFlows;
    }
}
