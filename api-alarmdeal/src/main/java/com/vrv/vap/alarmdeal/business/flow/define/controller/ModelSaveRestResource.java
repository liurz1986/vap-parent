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
package com.vrv.vap.alarmdeal.business.flow.define.controller;

import com.vrv.vap.alarmdeal.business.flow.define.service.ModelService;
import com.vrv.vap.alarmdeal.business.flow.define.vo.BpmnVO;
import com.vrv.vap.alarmdeal.business.flow.define.vo.DeployInfoVO;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.engine.ActivitiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Tijs Rademakers
 */
@RestController("mymodelSaveRestResource")
@RequestMapping("my")
public class ModelSaveRestResource implements ModelDataJsonConstants {
  
  protected static final Logger LOGGER = LoggerFactory.getLogger(ModelSaveRestResource.class);

  @Autowired
  private ModelService modelService;
  
  @RequestMapping(value="/model/{modelId}/save", method = RequestMethod.PUT)
  @ResponseStatus(value = HttpStatus.OK)
  @ApiOperation("模型保存")
  @SysRequestLog(description="模型保存", actionType = ActionType.ADD,manually = false)
  public void saveModel(@PathVariable String modelId
          , String name, String description
          , String json_xml, String svg_xml) {
    try {
    	modelService.saveModel(modelId, name, description, json_xml, svg_xml);
      
    } catch (Exception e) {
      throw new ActivitiException("Error saving model", e);
    }
  }

  /**
   * 根据流程生成对应的Jbpm文件 2022-4-20
   * @param bpmnVO
   */
  @ApiOperation("根据流程生成对应的bpmn文件,返回值为bpmn文件路径")
  @RequestMapping(value="/model/generateBpmnFile", method = RequestMethod.POST)
  @SysRequestLog(description="根据流程生成对应的bpmn文件", actionType = ActionType.ADD,manually = false)
  public Result<String> generateJbpmFile(@RequestBody BpmnVO bpmnVO) {
    String bpmnPath = modelService.generateJbpmFile(bpmnVO);
    return  ResultUtil.success(bpmnPath);
  }



  /**
   * 发布流程    2022-4-20
   * @param deployInfoVO
   */
  @ApiOperation("发布流程文件")
  @RequestMapping(value="/model/deploy", method = RequestMethod.POST)
  @SysRequestLog(description="发布流程文件", actionType = ActionType.UPDATE,manually = false)
  public Result<String> generateJbpmFile(@RequestBody DeployInfoVO deployInfoVO) {
    String deployId = modelService.deployJbpmFile(deployInfoVO);
    return  ResultUtil.success(deployId);
  }

}
