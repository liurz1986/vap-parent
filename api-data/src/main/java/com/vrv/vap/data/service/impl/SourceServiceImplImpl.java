package com.vrv.vap.data.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.data.component.ESManager;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.constant.FieldLibrary;
import com.vrv.vap.data.constant.SOURCE_TYPE;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.mapper.SourceMapper;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.service.BaseCacheServiceImpl;
import com.vrv.vap.data.service.SourceService;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class SourceServiceImplImpl extends BaseCacheServiceImpl<Source> implements SourceService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    private final int MAX_CHECK_TIME = 30;    // 检查字段时， 最大记录检查次数

    @Resource
    private SourceMapper sourceMapper;


    @Autowired
    private Cache<Integer, List<SourceField>> sourceFieldCache;

    @Autowired
    private ESManager esManager;

    private final String SQL_COLUMNS = "SHOW FULL COLUMNS FROM `%s`";

    @Autowired
    private StringRedisTemplate redisTpl;

    private static final String SEVEN = "7";

    @Override
    public List<SourceField> fetchTypes(Source source) throws ApiException {
        switch (source.getType()) {
            case SOURCE_TYPE.ELASTIC_BUILT:
                return this.fetchElastic(source);
            case SOURCE_TYPE.MYSQL_BUILT:
                return this.fetchMysql(source);
        }
        logger.error("Only Support BuiltIn ElasticSearch And Mysql ");
        return SYSTEM.EMPTY_LIST;
    }

//    @Autowired


    /**
     * 获取 MySql 表里面的字段
     */
    private List<SourceField> fetchMysql(Source source) {
        String sql = String.format(SQL_COLUMNS, source.getName());
        List<Map<String, Object>> result = new ArrayList<>();
        try {
            result = sourceMapper.execQuery(sql);
        } catch (Exception e) {
            logger.error("表不存在",e);
        }
        List<SourceField> fields = new ArrayList<>();
        for (int i = 0, _i = result.size(); i < _i; i++) {
            Map<String, Object> row = result.get(i);
            String field = row.get("Field").toString();
            String fieldType = row.get("Type").toString();
            String origin = fieldType.toLowerCase();
            int idx = origin.indexOf('(');
            if (idx > 0) {
                origin = origin.substring(0, idx);
            }
            String type = FieldLibrary.toElasticType(origin);
            Object comment = row.get("Comment");
            SourceField sf = new SourceField(source.getId(), field, origin, type, (short) i, comment == null ? null : comment.toString());
            Object extra = row.get("Extra");
            if (extra != null && StringUtils.isNotEmpty(extra.toString())) {
                sf.setShow(false);
            }
            fields.add(sf);
        }
        return fields;
    }


    /**
     * 获取 Elastic Search 索引里面的字段
     */
    private List<SourceField> fetchElastic(Source source) throws ApiException {
        try {
            String endpoint = "/" + source.getName() + "/_mapping";
            Response response = ESManager.sendGet(endpoint);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.getEntity().getContent());
            List<SourceField> fieldList = new ArrayList<>();
            Iterator<String> indexes = node.fieldNames();
            int checkTimes = 0;
            short sort = 0;
            Set<String> exists = new HashSet<>();
            String version = redisTpl.opsForValue().get(SYSTEM.ES_VERSION);
            if (StringUtils.isEmpty(version)) version = SEVEN;
            while (indexes.hasNext() && checkTimes < MAX_CHECK_TIME) {
                checkTimes++;
                String indexName = indexes.next();
                Iterator<String> logTypes = node.at("/" + indexName + "/mappings").fieldNames();
                while (logTypes.hasNext()) {
                    String logType = logTypes.next();
                    JsonNode typeRoot = node.at("/" + indexName + "/mappings/" + logType + "/properties");
                    if (version.compareTo(SEVEN) >= 0) {
                        typeRoot = node.at("/" + indexName + "/mappings/" + logType);
                    }
                    Iterator<String> fields = typeRoot.fieldNames();
                    while (fields.hasNext()) {
                        String field = fields.next();
                        if (exists.contains(field)) {
                            continue;
                        }
                        String origin = typeRoot.at("/" + field + "/type").asText();
                        fieldList.add(new SourceField(source.getId(), field, origin, origin, sort, null));
                        exists.add(field);
                        sort++;
                    }
                }
            }
            return fieldList;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ErrorCode.ERROR_QUERY_WRONG_TYPE.getResult().getCode(),ErrorCode.ERROR_QUERY_WRONG_TYPE.getResult().getMessage(), e);
        }
    }

    @Override
    public Integer deleteById(Integer id) {
        int count = super.deleteById(id);
        if (count > 0) {
            sourceFieldCache.remove(id);
        }
        return count;
    }

    @Override
    public Integer deleteByIds(String ids) {
        int count = super.deleteByIds(ids);
        if (count > 0) {
            String[] ptns = ids.split(",");
            for (String str : ptns) {
                int id = Integer.parseInt(str.trim());
                sourceFieldCache.remove(id);
            }
        }
        return count;
    }

    @Override
    public List<Source> findAllByRoleIds(List<Integer> roleIds) {
        return sourceMapper.findAllbyRoleIds(roleIds);
    }


}
