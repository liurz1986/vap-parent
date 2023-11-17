package com.vrv.vap.admin.service.impl;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.vo.EventTaVo;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.vrv.vap.admin.model.BaseDictAll;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.vo.BaseDictAllTreeVO;

import tk.mybatis.mapper.entity.Example;

/**
 * @author qinjiajing E-mail: 创建时间 2018年9月25日 下午6:41:52 类说明：BaseDictAllServiceImpl
 */
@Service
public class BaseDictAllServiceImpl extends BaseServiceImpl<BaseDictAll> implements BaseDictAllService {

	private static final Logger log = LoggerFactory.getLogger(BaseDictAllServiceImpl.class);

	private Map<String, Map<String, String>> dicCodeToValueMap = new HashMap<>();

	private Map<String, Map<String, String>> dicValueToCodeMap = new HashMap<>();

	private static final String CACHE_DICT_KEY = "_BASEINFO:BASE_DICT_ALL:ALL";

	@Autowired
	private KafkaSenderService kafkaSenderService;

	@Autowired
	StringRedisTemplate redisTemplate;

	@Override
	public List<BaseDictAllTreeVO> getTree() {
		List<BaseDictAll> findAll = findAll();
		BaseDictAllTreeVO baseDictAllTreeVO = null;
		List<BaseDictAllTreeVO> mapList = new ArrayList<>();
		for(BaseDictAll baseDictAll:findAll) {
			baseDictAllTreeVO = new BaseDictAllTreeVO();
			BeanUtils.copyProperties(baseDictAll, baseDictAllTreeVO);
			mapList.add(baseDictAllTreeVO);
		}
		return buildTree(mapList);
	}

	@Override
	public List<BaseDictAllTreeVO> findByRootName(String name) {
		Example example = new Example(BaseDictAll.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orCondition("parent_type='0' and code_value like '%"+name+"%'");
        List<BaseDictAll> list= findByExample(example);
        List<BaseDictAll> result = new ArrayList<>();
        for(BaseDictAll baseDictAll:list) {
			result.addAll(getOneTree(baseDictAll.getType()));

		}
        BaseDictAllTreeVO baseDictAllTreeVO = null;
        List<BaseDictAllTreeVO> mapList = new ArrayList<>();
		for(BaseDictAll baseDictAll:result) {
			baseDictAllTreeVO = new BaseDictAllTreeVO();
			BeanUtils.copyProperties(baseDictAll, baseDictAllTreeVO);
			mapList.add(baseDictAllTreeVO);
		}
        return buildTree(mapList);
	}

	@Override
	public void generateDicMap() {
		Map<String, String> codeToValueMap;
		Map<String, String> valueToCodeMap;
		List<BaseDictAll> baseDictAllList = findAll();
		Map<String, List<BaseDictAll>> groupMap = baseDictAllList.stream().collect(Collectors.groupingBy(BaseDictAll::getParentType));
		for (BaseDictAll parentDic : groupMap.get("0")) {
			String codeValue = parentDic.getCodeValue();
			String type = parentDic.getType();
			if (groupMap.containsKey(type)) {
				codeToValueMap = new HashMap<>();
				valueToCodeMap = new HashMap<>();
				for (BaseDictAll childrenDic : groupMap.get(type)) {
					codeToValueMap.put(childrenDic.getCode(),childrenDic.getCodeValue());
					valueToCodeMap.put(childrenDic.getCodeValue(),childrenDic.getCode());
				}
				dicCodeToValueMap.put(codeValue, codeToValueMap);
				dicValueToCodeMap.put(codeValue, valueToCodeMap);
			}
		}
	}

	@Override
	public Map<String, Map<String, String>> getDicCodeToValueMap() {
		return dicCodeToValueMap;
	}

	@Override
	public Map<String, Map<String, String>> getDicValueToCodeMap() {
		return dicValueToCodeMap;
	}


	private List<BaseDictAll> getOneTree(String type){
		Example example = new Example(BaseDictAll.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.orCondition("parent_type='"+type+"' or type = '"+type+"'");
        List<BaseDictAll> list= findByExample(example);
        return list;
	}

	private List<BaseDictAllTreeVO> buildTree(List<BaseDictAllTreeVO> treeNodes){
		List<BaseDictAllTreeVO> result = new ArrayList<>();
		Map<String, BaseDictAllTreeVO> tmp = new HashMap<>();
		for (BaseDictAllTreeVO node : treeNodes) {
			tmp.put(node.getType(), node);
			node.setChildren(new ArrayList<BaseDictAllTreeVO>());
		}
		for (BaseDictAllTreeVO cNode : treeNodes) {
			String parentId = cNode.getParentType();
			BaseDictAllTreeVO parentNode = tmp.get(parentId);
			if (parentNode != null) {
				parentNode.getChildren().add(cNode);
			} else {
				result.add(cNode);
			}
		}

		return result;
	}

	@Override
	public void cacheDict() {
		List<BaseDictAll> dictAllList = this.findAll();
		redisTemplate.opsForValue().set(CACHE_DICT_KEY, JSON.toJSONString(dictAllList));
	}

	@Override
	public void sendChangeMessage() {
		Map<String,Object> result = new HashMap<>();
		result.put("item","dict");
		result.put("time", System.currentTimeMillis());
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			String content = objectMapper.writeValueAsString(result);
			kafkaSenderService.send("vap_base_data_change_message",null,content);
		} catch (Exception e) {
			log.error("",e);
		}
	}

	@Override
	public List<EventTaVo> queryEventDict(List<EventTaVo> eventTaVos) {
		for (EventTaVo eventTaVo:eventTaVos){
            if (eventTaVo.getFieldValue().startsWith("[")){
				List<Map<String, String>> maps = parseStringToListOfMaps(eventTaVo.getFieldValue());
				for (Map<String, String> map:maps){
					Set<Map.Entry<String, String>> entries = map.entrySet();
					for (Map.Entry<String, String> m:entries){
						String value=getFieldValueByCode(m.getKey(),m.getValue());
						if (StringUtils.isNotBlank(value)){
							map.put(m.getKey(),value);
						}
					}
				}
				eventTaVo.setFieldValue(maps.toString());
			}else {
				String value=getFieldValueByCode(eventTaVo.getFieldName(),eventTaVo.getFieldValue());
			    if (StringUtils.isNotBlank(value)){
					eventTaVo.setFieldValue(value);
				}
			}
		}
		return eventTaVos;
	}

	private String getFieldValueByCode(String fieldName, String fieldValue) {
		Example example=new Example(BaseDictAll.class);
		example.createCriteria().andEqualTo("codeValue",fieldName).andEqualTo("parentType",0);
		List<BaseDictAll> byExample = findByExample(example);
		if (byExample.size()>0){
			BaseDictAll baseDictAll = byExample.get(0);
			Example exampleCh=new Example(BaseDictAll.class);
			exampleCh.createCriteria().andEqualTo("parentType",baseDictAll.getType())
					.andEqualTo("code",fieldValue);
			List<BaseDictAll> baseDictAlls = findByExample(exampleCh);
			if (baseDictAlls.size()>0){
				return baseDictAlls.get(0).getCodeValue();
			}
		}
		return null;
	}

	public static List<Map<String, String>> parseStringToListOfMaps(String input) {
		List<Map<String, String>> list = new ArrayList<>();
		Pattern pattern = Pattern.compile("\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(input);
		while (matcher.find()) {
			Map<String, String> map = new HashMap<>();
			String[] keyValuePairs = matcher.group(1).split(",");
			for (String keyValuePair : keyValuePairs) {
				String[] keyValue = keyValuePair.split("=");
				map.put(keyValue[0].trim(), keyValue[1].trim());
			}
			list.add(map);
		}
		return list;
	}
}
