package com.vrv.vap.alarmdeal.business.flow.define.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.itextpdf.text.log.Logger;
import com.itextpdf.text.log.LoggerFactory;
import com.vrv.vap.alarmdeal.business.flow.core.service.FlowService;
import com.vrv.vap.alarmdeal.business.flow.define.controller.DeploymentResponse;
import com.vrv.vap.alarmdeal.business.flow.define.vo.BpmnVO;
import com.vrv.vap.alarmdeal.business.flow.define.vo.DeployInfoVO;
import com.vrv.vap.alarmdeal.business.flow.processdef.exception.FlowException;
import com.vrv.vap.alarmdeal.business.flow.processdef.vo.ModelVO;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.PathUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.ResponseException;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.activiti.bpmn.converter.BpmnXMLConverter;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.editor.constants.ModelDataJsonConstants;
import org.activiti.editor.language.json.converter.BpmnJsonConverter;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.Model;
import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModelService {
	private static Logger logger = LoggerFactory.getLogger(ModelService.class);
	@Autowired
	RepositoryService repositoryService;
	@Autowired
	ObjectMapper objectMapper;
	@Autowired
	MapperUtil mapper;
	@Autowired
	private FlowService flowService;

	@Value("${bpmn.filePath}")
	private String bpmnFilePath; //bpmn放置的路径

	public String newModel(String name, String description, int revision, String key){
		Model model = repositoryService.newModel();
		ObjectNode modelNode = objectMapper.createObjectNode();
		modelNode.put(ModelDataJsonConstants.MODEL_NAME, name);
		modelNode.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		modelNode.put(ModelDataJsonConstants.MODEL_REVISION, revision);

		model.setName(name);
		model.setKey(key);
		model.setMetaInfo(modelNode.toString());

		repositoryService.saveModel(model);
		String id = model.getId();

		// 完善ModelEditorSource
		ObjectNode editorNode = objectMapper.createObjectNode();
		editorNode.put("id", "canvas");
		editorNode.put("resourceId", "canvas");
		ObjectNode stencilSetNode = objectMapper.createObjectNode();
		stencilSetNode.put("namespace", "http://b3mn.org/stencilset/bpmn2.0#");
		editorNode.put("stencilset", stencilSetNode);
		try {
			byte[] bytes = editorNode.toString().getBytes("utf-8");
			repositoryService.addModelEditorSource(id, bytes);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Unsupported encoding type:"+e);
		}
		return id;
	}

	public void saveModel(String modelId, String name, String description, String json_xml, String svg_xml)
			throws IOException, TranscoderException {

		Model model = repositoryService.getModel(modelId);

		ObjectNode modelJson = (ObjectNode) objectMapper.readTree(model.getMetaInfo());
		modelJson.put(ModelDataJsonConstants.MODEL_NAME, name);
		modelJson.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		model.setMetaInfo(modelJson.toString());
		model.setName(name);

		repositoryService.saveModel(model);

		repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes("utf-8"));

		InputStream svgStream = new ByteArrayInputStream(svg_xml.getBytes("utf-8"));
		TranscoderInput input = new TranscoderInput(svgStream);

		PNGTranscoder transcoder = new PNGTranscoder();
		// Setup output
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		TranscoderOutput output = new TranscoderOutput(outStream);

		// Do the transformation
		transcoder.transcode(input, output);
		final byte[] result = outStream.toByteArray();
		repositoryService.addModelEditorSourceExtra(model.getId(), result);
		outStream.close();
	}
	
	public Model copyModel(String modelId) {
		Model model = repositoryService.getModel(modelId);
		Model newModel = repositoryService.newModel();
		newModel.setName(model.getName());
		newModel.setMetaInfo(model.getMetaInfo());
		newModel.setKey(model.getKey());
		repositoryService.saveModel(newModel);
		byte[] modelEditorSource = repositoryService.getModelEditorSource(modelId);
		repositoryService.addModelEditorSource(newModel.getId(), modelEditorSource);
		byte[] modelEditorSourceExtra = repositoryService.getModelEditorSourceExtra(modelId);
		repositoryService.addModelEditorSourceExtra(newModel.getId(), modelEditorSourceExtra);
		
		return newModel;
	}
	
	
	/**
	 * 获得流程处理节点人员处理类型
	 * @param modelId
	 * @return
	 */
	public List<Map<String,Object>> getProcessDealPersonType(String modelId){
		List<Map<String,Object>> map_list = new ArrayList<>();
		Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            throw new ResponseException(1001, "模型数据为空，请先设计流程并成功保存，再进行发布。");
        }

		JsonNode modelNode = null;
		try {
			modelNode = new ObjectMapper().readTree(bytes);
		} catch (IOException e) {
			throw new RuntimeException("IO异常："+e);
		}
		List<JsonNode> list = modelNode.findValues("tasklisteners");
        for (JsonNode jsonNode : list) {
        	List<JsonNode> fields_json_node_list = jsonNode.findValues("fields");
        	for (JsonNode filed_jsonNode : fields_json_node_list) {
        		Map<String,Object> map = new HashMap<>();
        		String jsonNode_Str = filed_jsonNode.toString();
				List<ModelVO> model_list = null;
				try {
					model_list = JsonMapper.fromJsonString2List(jsonNode_Str, ModelVO.class);
				} catch (IOException e) {
					throw new RuntimeException("json解析异常："+e);
				}
				for (ModelVO modelVO : model_list) {
        			String name = modelVO.getName();
        			if(name.equals("candidateType")||name.equals("candidate")){
        				map.put(name, modelVO.getImplementation());
        			}
				}
        		if(map.size()!=0){
        			map_list.add(map);        			
        		}
			}
		}
		return map_list;
	}
	
	
	public DeploymentResponse deployModel(String modelId) throws JsonProcessingException, IOException {
		Model modelData = repositoryService.getModel(modelId);
        byte[] bytes = repositoryService.getModelEditorSource(modelData.getId());

        if (bytes == null) {
            throw new ResponseException(1001, "模型数据为空，请先设计流程并成功保存，再进行发布。");
        }

        JsonNode modelNode = new ObjectMapper().readTree(bytes);

        BpmnModel model = new BpmnJsonConverter().convertToBpmnModel(modelNode);
        if(model.getProcesses().size()==0){
        	throw new ResponseException(1002, "数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);

        //发布流程
        String processName = modelData.getName() + ".bpmn20.xml";
        Deployment deployment = repositoryService.createDeployment()
                .name(modelData.getName())
                .addString(processName, new String(bpmnBytes, "UTF-8"))
                .deploy();
        modelData.setDeploymentId(deployment.getId());
        repositoryService.saveModel(modelData);
        
        return new DeploymentResponse(deployment);
	}

	/**
	 * 生成bpmn文件，如果是启动状态
	 * @param bpmnVO
	 * @return
	 */
	public String generateJbpmFile(BpmnVO bpmnVO) {
		String bpmnFilePath = getBpmnFilePath(bpmnVO);
		return bpmnFilePath;
	}

	/**
	 * 获得Bpmn文件路径
	 * @param bpmnVO
	 */
	public String getBpmnFilePath(BpmnVO bpmnVO) {
		String bpmnXml = bpmnVO.getBpmnXml();
		String name = bpmnVO.getName();
		name = name+ UUIDUtils.get32UUID()+".bpmn";
		try {
			byte[] bytes = bpmnXml.getBytes("UTF-8");
			FileUtil.saveFile(bytes, bpmnFilePath, name);
		} catch (Exception e){
			logger.error("xml文件编译错误", e);
			throw new FlowException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"xml文件编译错误");
		}
		String bpmnPath = PathUtil.combine(bpmnFilePath, name);
		return bpmnPath;
	}

	/**
	 * 发布编辑流程
	 * @param deployInfoVO
	 * @return
	 */
	public String deployJbpmFile(DeployInfoVO deployInfoVO){
		String name = deployInfoVO.getBpmnName();
		String resourceName = PathUtil.combine(name+UUIDUtils.get32UUID(), ".bpmn20.xml");
		String jbpmPath = deployInfoVO.getBpmnPath();
		DeploymentResponse deploymentResponse = flowService.deployProcess(resourceName, jbpmPath);
		String deployId = deploymentResponse.getId();
		return deployId;
	}
}
