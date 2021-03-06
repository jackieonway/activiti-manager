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
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

/**
 * @author Jackie
 */
@CrossOrigin
@RestController
@RequestMapping("/service")
@Api(value = "工作流模型保存接口", tags = "工作流模型保存接口")
public class ModelSaveRestResource implements ModelDataJsonConstants {
    private static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

//    @Value("${activiti.root.path}")
//    private String rootPath;

    @Autowired
    private ObjectMapper objectMapper;

    @ApiOperation(value = "保存模型", notes = "必传参数: tenant: 租户, modelId: 模型id, json_xml: 流程XML的JSON形式数据, " +
            "svg_xml:流程XML的SVG形式数据, description: 工作流模型描述, name： 工作流模型名称")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenent", paramType = "query", value = "租户", required = true, dataType =
                    "String"),
            @ApiImplicitParam(name = "modelId", paramType = "query", value = "模型id", required = true, dataType =
                    "String"),
            @ApiImplicitParam(name = "json_xml", paramType = "query", value = "流程XML的JSON形式数据", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "svg_xml", paramType = "query", value = "流程XML的SVG形式数据", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "description", paramType = "query", value = "工作流模型描述", required = true,
                    dataType = "String"),
            @ApiImplicitParam(name = "name", paramType = "query", value = "工作流模型名称", required = true, dataType =
                    "String"),
    })
    @PutMapping(value = "/model/{tenant}/{modelId}/save")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@PathVariable String tenant, @PathVariable String modelId, @RequestParam("name") String name,
                          @RequestParam("json_xml") String jsonXml, @RequestParam("svg_xml") String svgXml,
                          @RequestParam("description") String description) {
        try {
            Model model = repositoryService.createModelQuery().modelId(modelId).modelTenantId(tenant).singleResult();
            ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
            modelJson.put(MODEL_NAME, name);
            modelJson.put(MODEL_DESCRIPTION, description);
            model.setMetaInfo(modelJson.toString());
            model.setName(name);
            repositoryService.saveModel(model);
            repositoryService.addModelEditorSource(model.getId(), jsonXml.getBytes(StandardCharsets.UTF_8));
            InputStream svgStream = new ByteArrayInputStream(svgXml.getBytes(StandardCharsets.UTF_8));
            TranscoderInput input = new TranscoderInput(svgStream);
            PNGTranscoder transcoder = new PNGTranscoder();
            // Setup output
            ByteArrayOutputStream outStream = new ByteArrayOutputStream();
            TranscoderOutput output = new TranscoderOutput(outStream);
            // Do the transformation
            transcoder.transcode(input, output);
//            BpmnJsonConverter jsonConverter = new BpmnJsonConverter();
//            JsonNode jsonNode = objectMapper.readTree(jsonXml);
//            BpmnModel bpmnModel = jsonConverter.convertToBpmnModel(jsonNode);
//            byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(bpmnModel);
            //写入流程图xml
//            transToFile(rootPath + "//" + tennet, "//xml//", name, ".bpmn20.xml", bpmnBytes);
            final byte[] result = outStream.toByteArray();
            // 写入流程图图片
//            transToFile(rootPath + "//" + tennet, "//pic//", name, ".png", result);
            repositoryService.addModelEditorSourceExtra(model.getId(), result);
            outStream.close();
        } catch (IOException | TranscoderException e) {
            LOGGER.error("Error saving model", e);
            throw new ActivitiException("Error saving model", e);
        }
    }

    /**
     * 将Bytes写到文件中
     *
     * @param rootPath 文件路径 如: <i>win: <b>D:\\xx\\</b> linux : <b>/app/file/</b></i>
     * @param busiPath 业余文件路径 如: <i><b>xml,pic</b></i>
     * @param fileName 文件名
     * @param extName  文件扩展名 如: <i><b>.xml , .jpg</b></i>
     * @param bytes    待写入文件的byte数组
     * @return 文件的相对路径
     * @author Jackie
     * @date 2019/10/30 20:51
     * @method-name com.github.jackieonway.activiti.controller.ModelSaveRestResource#transToFile
     * @see ModelSaveRestResource
     * @since 1.0
     */
    private String transToFile(String rootPath, String busiPath, String fileName, String extName, byte[] bytes) {
        File file = new File(rootPath + busiPath);
        String path = busiPath + fileName + extName;
        FileOutputStream fileOutputStream = null;
        try {
            if (!file.exists()) {
                file.mkdirs();
            }
            fileOutputStream = new FileOutputStream(new File(rootPath + path));
            fileOutputStream.write(bytes);
        } catch (IOException e) {
            LOGGER.error("写文件[{}]出错", fileName + extName, e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("写文件[{}]关闭流出错", fileName + extName, e);
                }
            }
        }
        return path;
    }

}
