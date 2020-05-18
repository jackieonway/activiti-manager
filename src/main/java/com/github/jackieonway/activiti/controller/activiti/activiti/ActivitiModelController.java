package com.github.jackieonway.activiti.controller.activiti.activiti;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jackieonway.activiti.utils.ResponseUtils;
import com.github.jackieonway.activiti.utils.ResultMsg;
import com.github.jackieonway.activiti.utils.page.PageResult;
import com.github.jackieonway.activiti.utils.page.QueryConditionBean;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@CrossOrigin
@RestController
@Api(value = "工作流模型相关接口", tags = "工作流模型相关接口")
public class ActivitiModelController {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 新建一个空模型
     */
    @ApiOperation(value = "新建一个空模型")
    @GetMapping("/create")
    public void newModel(HttpServletResponse response) throws IOException {
        //初始化一个空模型
        Model model = repositoryService.newModel();
        //设置一些默认信息
        String name = "new-process";
        String description = "";
        int revision = 1;
        String key = "process";
        ObjectNode modelNode = objectMapper.createObjectNode();
        modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
        modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
        modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);
        model.setName(name);
        model.setKey(key);
        model.setMetaInfo(modelNode.toString());
        repositoryService.saveModel(model);
        String id = model.getId();
        //完善ModelEditorSource
        ObjectNode editorNode = objectMapper.createObjectNode();
        editorNode.put("id", "canvas");
        editorNode.put("resourceId", "canvas");
        ObjectNode stencilSetNode = objectMapper.createObjectNode();
        stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
        editorNode.set("stencilset", stencilSetNode);
        repositoryService.addModelEditorSource(id, editorNode.toString().getBytes(StandardCharsets.UTF_8));
        response.sendRedirect("/modeler.html?modelId=" + id);
    }

    /**
     * 获取所有模型
     */
    @ApiOperation(value = "获取所有模型", notes = "必传参数: queryConditionBean: 分页")
    @GetMapping("/modelList")
    @ResponseBody
    public PageResult<Model> modelList(QueryConditionBean queryConditionBean) {
        PageResult<Model> pageResult =
                PageResult.newEmptyResult(queryConditionBean.getPageNum(), queryConditionBean.getPageSize());
        List<Model> models = repositoryService.createModelQuery().listPage(queryConditionBean.getStartIndex(),
                queryConditionBean.getPageSize());
        if (CollectionUtils.isEmpty(models)){
            return pageResult;
        }
        pageResult.setTotalCount(repositoryService.createModelQuery().count());
        pageResult.setList(models);
        return pageResult;
    }

    /**
     * 根据模型id发布模型为流程定义
     * http://localhost:8080/deploy?modelId=1
     */
    @ApiOperation(value = "根据模型id发布模型为流程定义", notes = "必传参数: modelId: 模型id")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", paramType = "query", value = "模型id", required = true, dataType = "String")})
    @GetMapping("/deploy")
    @ResponseBody
    public ResultMsg deploy(String modelId) throws IOException {
        Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());
        byte[] pngBytes = repositoryService.getModelEditorSourceExtra(modelData.getId());
        if (bytes == null && pngBytes == null) {
            return ResponseUtils.fail("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }
        JsonNode modelNode = new ObjectMapper().readTree(bytes);
        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if (CollectionUtils.isEmpty(model.getProcesses())) {
            return ResponseUtils.fail("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        String processPngName = modelData.getName() + ".png";
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addInputStream(processName, new ByteArrayInputStream(bpmnBytes))
                .addInputStream(processPngName, new ByteArrayInputStream(pngBytes))
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        return ResponseUtils.success();
    }
}
