/*
 * Jackie.
 * Copyright (c)) 2019 - 2020 All Right Reserved
 */
package com.github.jackieonway.activiti.service;

import com.github.jackieonway.activiti.entity.actentity.*;
import com.github.jackieonway.activiti.utils.ResultMsg;
import com.github.jackieonway.activiti.utils.page.PageResult;
import com.github.jackieonway.activiti.entity.request.WorkFlowRequest;
import com.github.jackieonway.activiti.entity.vo.WorkFlowVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Jackie
 * @version $id: ActWorkFlowService.java v 0.1 2020-02-20 10:09 Jackie Exp $$
 */
public interface ActWorkFlowService {

    /**
     * 根据流程图Zip压缩文件部署流程定义
     *
     * @param file     流程定义压缩文件
     * @param tenant   租户信息
     * @param category 分类信息
     * @return 部署结果
     * @author Jackie
     * @date 2020/2/20 10:36
     * @method-name com.gsww.approval.service.ActWorkFlowService#deployByZip
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<WorkFlowVo> deployByZip(MultipartFile file, String tenant, String category) throws IOException;


    /**
     * 查询所有已部署流程图
     *
     * @param workFlowRequest 工作流请求
     * @return 部署流程图集合
     * @author Jackie
     * @date 2020/4/9 11:49
     * @method-name com.gsww.activiti.service.ActWorkFlowService#queryAllDeployments
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<PageResult<ActDeployment>> queryAllDeployments(WorkFlowRequest workFlowRequest);

    /**
     * 启动工作流
     *
     * @param workFlowRequest 工作流请求
     * @return 启动结果
     * @author Jackie
     * @date 2020/2/20 10:37
     * @method-name com.gsww.approval.service.ActWorkFlowService#startWorkFlow
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<WorkFlowVo> startWorkFlow(WorkFlowRequest workFlowRequest);

    /**
     * 终止正在运行的流程
     *
     * @param workFlowRequest 工作流请求
     * @return 终止结果
     * @author Jackie
     * @date 2020/4/9 11:50
     * @method-name com.gsww.activiti.service.ActWorkFlowService#stopWorkFlow
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg stopWorkFlow(WorkFlowRequest workFlowRequest);

    /**
     * 暂停已运行的工作流
     *
     * @param workFlowRequest 工作流请求
     * @return 暂停结果
     * @author Jackie
     * @date 2020/4/9 11:56
     * @method-name com.gsww.activiti.service.ActWorkFlowService#suspendWorkFlow
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg suspendWorkFlow(WorkFlowRequest workFlowRequest);

    /**
     * 激活已暂停的工作流
     *
     * @param workFlowRequest 工作流请求
     * @return 激活结果
     * @author Jackie
     * @date 2020/4/9 11:55
     * @method-name com.gsww.activiti.service.ActWorkFlowService#activateWorkFlow
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg activateWorkFlow(WorkFlowRequest workFlowRequest);

    /**
     * 展示工作流流程图
     *
     * @param businessKey 业务键
     * @param response    Http响应
     * @author Jackie
     * @date 2020/2/21 15:10
     * @method-name com.gsww.approval.service.ActWorkFlowService#showWorkFlowImage
     * @see ActWorkFlowService
     * @since 1.0
     */
    void showWorkFlowImage(String businessKey, String tenant, HttpServletResponse response) throws IOException;

    /**
     * 执行任务流程
     *
     * @param workFlowRequest 工作流请求
     * @return 结果
     * @author Jackie
     * @date 2020/2/21 15:10
     * @method-name com.gsww.approval.service.ActWorkFlowService#doTask
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg doTask(WorkFlowRequest workFlowRequest);

    /**
     * 查询当前用户的待办任务
     *
     * @param workFlowRequest 工作流请求
     * @return 分页结果集合
     * @author Jackie
     * @date 2020/2/21 15:12
     * @method-name com.gsww.approval.service.ActWorkFlowService#queryToDoTasks
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<PageResult<ActTaskEntity>> queryToDoTasks(WorkFlowRequest workFlowRequest);

    /**
     * 查询审批信息
     *
     * @param workFlowRequest 工作流请求
     * @return 分页结果集合
     * @author Jackie
     * @date 2020/2/25 11:52
     * @method-name com.gsww.approval.service.ActWorkFlowService#queryComments
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<PageResult<ActCommentEntity>> queryComments(WorkFlowRequest workFlowRequest);

    /**
     * 查询当前用户的审批记录
     *
     * @param workFlowRequest 工作流请求
     * @return 分页结果集合
     * @author Jackie
     * @date 2020/2/25 11:51
     * @method-name com.gsww.approval.service.ActWorkFlowService#queryHistoryTasks
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<PageResult<ActHistoryTaskEntity>> queryHistoryTasks(WorkFlowRequest workFlowRequest);

    /**
     * 改变审批人
     *
     * @param workFlowRequest 工作流请求
     * @return 结果
     * @author Jackie
     * @date 2020/2/26 17:18
     * @method-name com.gsww.approval.service.ActWorkFlowService#changeUserAssignee
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg changeUserAssignee(WorkFlowRequest workFlowRequest);

    /**
     * 拾取用户任务
     *
     * @param workFlowRequest 工作流请求
     * @return 结果
     * @author Jackie
     * @date 2020/2/26 17:18
     * @method-name com.gsww.approval.service.ActWorkFlowService#claim
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg claimTask(WorkFlowRequest workFlowRequest);

    /**
     * 撤销用户任务
     *
     * @param workFlowRequest 工作流请求
     * @return 结果
     * @author Jackie
     * @date 2020/2/26 17:18
     * @method-name com.gsww.approval.service.ActWorkFlowService#claim
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg backTask(WorkFlowRequest workFlowRequest);

    /**
     * 查询候选人列表
     *
     * @param workFlowRequest 工作流请求
     * @return 结果
     * @author Jackie
     * @date 2020/2/26 17:18
     * @method-name com.gsww.approval.service.ActWorkFlowService#queryCandidateUsers
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<List<ActIdentityLinkEntity>> queryCandidateUsers(WorkFlowRequest workFlowRequest);

    /**
     * 获取表单地址
     *
     * @param workFlowRequest 工作流请求
     * @return 表单地址
     * @author Jackie
     * @date 2020/4/9 10:50
     * @method-name com.gsww.activiti.service.ActWorkFlowService#queryFormKey
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<String> queryFormKey(WorkFlowRequest workFlowRequest);

    /**
     * 获取未完成的流程
     *
     * @param workFlowRequest 工作流请求
     * @return 未完成的工作流程集合
     * @author Jackie
     * @date 2020/4/10 9:39
     * @method-name com.gsww.activiti.service.ActWorkFlowService#queryUnfinishedProcessInstance
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<PageResult<ActProcessInstanceEntity>> queryUnfinishedProcessInstance(WorkFlowRequest workFlowRequest);

    /**
     * 获取已完成的流程
     *
     * @param workFlowRequest 工作流请求
     * @return 已完成的工作流集合
     * @author Jackie
     * @date 2020/4/10 9:40
     * @method-name com.gsww.activiti.service.ActWorkFlowService#queryFinishedProcessInstance
     * @see ActWorkFlowService
     * @since 1.0
     */
    ResultMsg<PageResult<ActProcessInstanceEntity>> queryFinishedProcessInstance(WorkFlowRequest workFlowRequest);

    /**
     * 回退用户任务
     * @param workFlowRequest 工作流请求
     * @return  结果
     * @author  Jackie
     * @date  2020/5/9 15:09
     * @since 1.0
     * @method-name com.gsww.activiti.service.ActWorkFlowService#rejectTask
     * @see ActWorkFlowService
     */
    ResultMsg rejectTask(WorkFlowRequest workFlowRequest);
}
