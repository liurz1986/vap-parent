package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.vrv.vap.admin.common.manager.ElasticSearchManager;
import com.vrv.vap.admin.common.util.ImportExcelUtil;
import com.vrv.vap.admin.mapper.DiscoverIndexMapper;
import com.vrv.vap.admin.model.DiscoverIndex;
import com.vrv.vap.admin.model.DiscoverIndexField;
import com.vrv.vap.admin.service.IndexService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * 索引接口实现类
 *
 * @author lilang
 * @date 2018年1月31日
 */
@Service
@Transactional
public class IndexServiceImpl extends BaseServiceImpl<DiscoverIndex> implements IndexService {

    @Resource
    private DiscoverIndexMapper discoverIndexMapper;
    @Value("${elk.vap.index}")
    private String SETTING_INDEX;
    @Value("${elk.vap.indexPattern}")
    private String INDEX_PATTERN;

    private static final Log log = LogFactory.getLog(IndexServiceImpl.class);

    @Override
    public List<DiscoverIndexField> queryFieldByIndexId(String title) {
        List<DiscoverIndexField> list = new ArrayList<>();
        List<String> fieldList = new ArrayList<String>();
        Response resp = this.getMapping(title);
        if (resp != null) {
            String responseStr;
            try {
                responseStr = EntityUtils.toString(resp.getEntity());
                Map<String, Object> responeMap = new HashMap<String, Object>();
                ObjectMapper mapper = new ObjectMapper();
                responeMap = mapper.readValue(responseStr, responeMap.getClass());
                for (String indexname : responeMap.keySet()) {
                    Map<String, Object> mappingsMap = (Map<String, Object>) responeMap.get(indexname);
                    Map<String, Object> typeMap = (Map<String, Object>) mappingsMap.get("mappings");
//                    for (String type : typeMap.keySet()) {
//                        Map<String, Object> propertiesMap = (Map<String, Object>) typeMap.get(type);
                    Map<String, Object> fieldsMap = (Map<String, Object>) typeMap.get("properties");

                    if (fieldsMap == null)
                        continue;
                    for (String fieldname : fieldsMap.keySet()) {
                        if (fieldList.contains(fieldname))
                            continue;

                        fieldList.add(fieldname);
                        DiscoverIndexField model = ConvertMapToModel((Map<String, Object>) fieldsMap.get(fieldname));
                        model.setName(fieldname);
                        list.add(model);
                    }
//                    }
                }
            } catch (ParseException e) {
                log.error("",e);
            } catch (IOException e) {
                log.error("",e);
            }
        }

        return list;
    }


    private Response getMapping(String keyFilter) {

        RestClient restClient = ElasticSearchManager.getClient().getLowLevelClient();
        String endpoint = "/" + keyFilter + "/_mapping";
        try {
            Request request = new Request("GET", endpoint);
            Response resp = restClient.performRequest(request);
            return resp;
        } catch (IOException e) {
            log.error("",e);
        }
        return null;
    }


    private DiscoverIndexField ConvertMapToModel(Map<String, Object> param) {
        return ConvertMapToModel(param, true, true);
    }

    private DiscoverIndexField ConvertMapToModel(Map<String, Object> param, boolean defaultDisplayed, boolean defaultFiltered) {
        DiscoverIndexField model = new DiscoverIndexField();
        if (param.containsKey("type")) {
            model.setType(param.get("type").toString());
        }
        Gson gson = new Gson();
        if (param.containsKey("properties")) {
            model.setType("json");
            model.setJsonFields(gson.toJson(param.get("properties")));
        }
        model.setDisplayed(defaultDisplayed);
        model.setFiltered(defaultFiltered);
        model.setIndexed(true);
        model.setAnalyzed(true);
        if (param.containsKey("index")) {
            String indextype = param.get("index").toString();
            if ("not_analyzed".equals(indextype)) {
                model.setAnalyzed(false);
            }
        }
        return model;
    }

    @Override
    public Integer importIndex(MultipartFile file) {
        int result = 0;
        List<DiscoverIndex> indexList = new ArrayList<>();

        List<List<Object>> listob = new ArrayList<>();
        if (!file.isEmpty()) {
            try (InputStream in = file.getInputStream()) {
                String fileName = file.getOriginalFilename();
                listob = ImportExcelUtil.getListByExcel(in, fileName);
            } catch (Exception e) {
                log.error("excel文件读取异常！");
                e.printStackTrace();
            }
            if (!CollectionUtils.isEmpty(listob)) {
                for (List<Object> lo : listob) {
                    if (lo.get(0) != null) {
                        DiscoverIndex discoverIndex = new DiscoverIndex();
                        discoverIndex.setIndexid(lo.get(0).toString());
                        discoverIndex.setIndexname(SETTING_INDEX);
                        discoverIndex.setType(INDEX_PATTERN);
                        discoverIndex.setDefaultindex(0);
                        discoverIndex.setTitle(lo.get(0).toString());
                        discoverIndex.setTitledesc(lo.get(1) != null ? lo.get(1).toString() : "");
                        discoverIndex.setTimefieldname(lo.get(2) != null ? lo.get(2).toString() : "");
                        discoverIndex.setCategory(lo.get(3) != null ? lo.get(3).toString() : "");
                        if (lo.get(4) != null) {
                            String indexFields = lo.get(4).toString();
                            discoverIndex.setIndexfields(this.generateIndexFields(indexFields));
                        }
                        discoverIndex.setDomainFieldName(lo.get(5) != null ? lo.get(5).toString() : "");
                        indexList.add(discoverIndex);
                    }
                }
            }
        }
        if (CollectionUtils.isNotEmpty(indexList)) {
            result = discoverIndexMapper.insertList(indexList);
        }
        return result;
    }

    /**
     * 转换构造索引字段
     * @param indexFields
     * @return
     */
    private String generateIndexFields(String indexFields) {
        List<DiscoverIndexField> fieldList = new ArrayList<>();
        Gson gson = new Gson();
        Map<String,Object> map = gson.fromJson(indexFields,Map.class);
        if (map.containsKey("properties")) {
            Map<String,Object> propMap = (Map<String, Object>) map.get("properties");
            if (propMap != null) {
                Set<String> keySet = propMap.keySet();
                if (CollectionUtils.isNotEmpty(keySet)) {
                    for (String key : keySet) {
                        DiscoverIndexField indexField = new DiscoverIndexField();
                        Map<String,Object> typeMap = (Map<String, Object>) propMap.get(key);
                        indexField.setName(key);
                        indexField.setType((String) typeMap.get("type"));
                        indexField.setDisplayed(true);
                        indexField.setIndexed(true);
                        indexField.setAnalyzed(true);
                        indexField.setFormat("");
                        indexField.setEntityId("");
                        indexField.setScript("");
                        indexField.setDoc_values(false);
                        indexField.setFiltered(true);
                        indexField.setLinkType("");
                        indexField.setTag("");
                        indexField.setLang("");
                        indexField.setScripted(false);
                        fieldList.add(indexField);
                    }
                }
            }
        }
        return gson.toJson(fieldList);
    }
}
