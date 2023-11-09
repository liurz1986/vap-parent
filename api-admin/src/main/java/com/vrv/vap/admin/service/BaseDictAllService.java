package com.vrv.vap.admin.service;

import java.util.List;
import java.util.Map;

import com.vrv.vap.admin.model.BaseDictAll;
import com.vrv.vap.admin.vo.BaseDictAllTreeVO;
import com.vrv.vap.base.BaseService;

/**
 *@author qinjiajing E-mail:
 * 创建时间 2018年9月25日 下午6:41:23
 * 类说明：BaseDictAllService
 */
public interface BaseDictAllService extends BaseService<BaseDictAll> {
	/**
	 * 字典树
	 * @return
	 */
	public List<BaseDictAllTreeVO> getTree();
	/**
	 * 根据根节点名称查
	 * @return
	 */
	public List<BaseDictAllTreeVO> findByRootName(String name);

	void generateDicMap();

	Map<String, Map<String, String>> getDicCodeToValueMap();

	Map<String, Map<String, String>> getDicValueToCodeMap();

	void cacheDict();

	void sendChangeMessage();
}
