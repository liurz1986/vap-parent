package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.CollectorRuleCollection;
import com.vrv.vap.admin.vo.CollectorRuleCollectionVO;
import com.vrv.vap.base.BaseService;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *@author lilang
 *@date 2022/1/4
 *@description
 */
public interface CollectorRuleCollectionService extends BaseService<CollectorRuleCollection> {

    List<CollectorRuleCollectionVO> transformRuleCollection(List<CollectorRuleCollection> ruleCollectionList);

    void export(HttpServletResponse response, String content,Integer collectionId);

    Integer importRuleCollection(MultipartFile file);

    void updateVersion(Integer collectionId);
}
