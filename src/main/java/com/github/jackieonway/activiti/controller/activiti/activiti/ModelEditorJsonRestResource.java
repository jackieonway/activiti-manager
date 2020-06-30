/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.jackieonway.activiti.controller.activiti.activiti;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Model;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author Jackie
 */
@CrossOrigin
@RestController
@RequestMapping("/service")
@Api(value = "工作流模型编辑接口", tags = "工作流模型编辑接口")
public class ModelEditorJsonRestResource implements ModelDataJsonConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelEditorJsonRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping(value = "/model/{tenant}/{modelId}/json", produces = "application/json")
    @ApiOperation(value = "根据模型id获取流程模型数据", notes = "必传参数: modelId: 模型id, tennat")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "modelId", paramType = "query", value = "模型id", required = true, dataType = "String"),
            @ApiImplicitParam(name = "tenant", paramType = "query", value = "租户", required = true, dataType = "String")
    })
    public ObjectNode getEditorJson(@PathVariable String tenant, @PathVariable String modelId) {
        ObjectNode modelNode = null;
        Model model = repositoryService.createModelQuery().modelId(modelId).modelTenantId(tenant).singleResult();
        if (model == null) {
            return modelNode;
        }
        try {
            if (StringUtils.isNotEmpty(model.getMetaInfo())) {
                modelNode = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            } else {
                modelNode = objectMapper.createObjectNode();
                modelNode.put(MODEL_NAME, model.getName());
            }
            modelNode.put(MODEL_ID, model.getId());
            ObjectNode editorJsonNode = (ObjectNode) objectMapper.readTree(
                    new String(repositoryService.getModelEditorSource(model.getId()), StandardCharsets.UTF_8));
            modelNode.set("model", editorJsonNode);

        } catch (IOException e) {
            LOGGER.error("Error creating model JSON", e);
            throw new ActivitiException("Error creating model JSON", e);
        }
        return modelNode;
    }
}
