package com.github.jackieonway.activiti.controller.activiti.activiti;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.jackieonway.activiti.utils.ResponseUtils;
import com.github.jackieonway.activiti.utils.ResultMsg;
import com.github.jackieonway.activiti.utils.page.PageResult;
import com.github.jackieonway.activiti.utils.page.QueryConditionBean;
import com.sun.xml.internal.fastinfoset.stax.factory.StAXInputFactory;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
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
        if (CollectionUtils.isEmpty(models)) {
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
            @ApiImplicitParam(name = "modelId", paramType = "query", value = "模型id", required = true, dataType =
                    "String")})
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

    @ApiOperation(value = "导入流程文件", notes = "必传参数: file: 流程定义文件 支持： bpmn20.xml 和 bpmn文件格式")
    @PostMapping(value = "/importBpmn")
    public void importBpmn(@RequestParam("file") MultipartFile uploadfile) {
        InputStreamReader in = null;
        try {
            String fileName = uploadfile.getOriginalFilename();
            if (!(fileName.endsWith(".bpmn20.xml") || fileName.endsWith(".bpmn"))) {
                log.error("activiti import error , file name is [{}], but is not \".bpmn20.xml\" or \".bpmn\" : [{}]"
                        , fileName);
                return;
            }
            XMLInputFactory xif = new StAXInputFactory();
            in = new InputStreamReader(new ByteArrayInputStream(uploadfile.getBytes()), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(in);
            BpmnModel bpmnModel = new BpmnXMLConverter().convertToBpmnModel(xtr);

            if (bpmnModel.getMainProcess() == null || bpmnModel.getMainProcess().getId() == null) {
                log.error("activiti import error ,activiti can not find main process");
                return;
            }
            if (bpmnModel.getLocationMap().isEmpty()) {
                log.error("activiti import error ,activiti can not find location map");
                return;
            }
            String processName;
            if (StringUtils.isNotEmpty(bpmnModel.getMainProcess().getName())) {
                processName = bpmnModel.getMainProcess().getName();
            } else {
                processName = bpmnModel.getMainProcess().getId();
            }
            Model modelData;
            modelData = repositoryService.newModel();
            ObjectNode modelObjectNode = new ObjectMapper().createObjectNode();
            modelObjectNode.put(ModelDataJsonConstants.MODEL_NAME, processName);
            modelObjectNode.put(ModelDataJsonConstants.MODEL_REVISION, 1);
            modelData.setMetaInfo(modelObjectNode.toString());
            modelData.setName(processName);
            repositoryService.saveModel(modelData);
            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
            ObjectNode editorNode = jsonConverter.convertToJson(bpmnModel);
            repositoryService.addModelEditorSource(modelData.getId(),
                    editorNode.toString().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            log.error("activiti import error , reason : [{}]", e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("activiti import error , reason : [{}]", e.getMessage(), e);
                }
            }
        }
    }

}
