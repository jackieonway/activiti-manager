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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
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
public class ModelSaveRestResource implements ModelDataJsonConstants {
    private Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

    @Autowired
    private RepositoryService repositoryService;

    @Value("${activiti.root.path}")
    private String rootPath;

    @Autowired
    private ObjectMapper objectMapper;

    @PutMapping(value = "/model/{modelId}/save")
    @ResponseStatus(value = HttpStatus.OK)
    public void saveModel(@PathVariable String modelId, @RequestParam("name") String name,
                          @RequestParam("json_xml") String jsonXml, @RequestParam("svg_xml") String svgXml,
                          @RequestParam("description") String description) {
        try {
            Model model = repositoryService.getModel(modelId);
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
//            transToFile(rootPath, "//xml//", name, ".bpmn20.xml", bpmnBytes);
            final byte[] result = outStream.toByteArray();
            // 写入流程图图片
//            transToFile(rootPath, "//pic//", name, ".png", result);
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
