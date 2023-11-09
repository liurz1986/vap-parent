package com.vrv.vap.admin.service.impl;

import com.alibaba.nacos.common.utils.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.mapper.ServiceApiMapper;
import com.vrv.vap.admin.mapper.ServiceModuleMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.ServiceApiService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lilang
 * @date 2022/10/9
 * @description
 */
@Service
@Transactional
public class ServiceApiServiceImpl extends BaseServiceImpl<ServiceApi> implements ServiceApiService {

    private static final Logger logger = LoggerFactory.getLogger(ServiceApiServiceImpl.class);

    @Resource
    ServiceApiMapper serviceApiMapper;

    @Resource
    ServiceModuleMapper serviceModuleMapper;

    @Override
    public String syncServiceApi() {
        String result = "";
        List<ServiceModule> serviceModules = serviceModuleMapper.selectAll();
        for (ServiceModule module : serviceModules) {
            Boolean syncResult = this.syncServieApi(module);
            if (!syncResult) {
                result += module.getName() + "、";
            }
        }
        if (StringUtils.isNotEmpty(result)) {
            result = result.substring(0,result.length() - 1);
        }
        return result;
    }

    @Override
    public Boolean syncServieApi(ServiceModule module) {
        String syncUrl = module.getSyncUrl();
        Integer id = module.getId();
        try {
            String result = HTTPUtil.GET(syncUrl, null);
            if (result != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                SyncResult syncResult = objectMapper.readValue(result, SyncResult.class);
                if (syncResult != null) {
                    Map<String, Method> paths = syncResult.getPaths();
                    List<Tags> tags = syncResult.getTags();
                    Set<Map.Entry<String, Method>> entries = paths.entrySet();
                    for (Map.Entry<String, Method> entry : entries) {
                        String key = entry.getKey();
                        Method value = entry.getValue();
                        String replace = replace(key);
                        List<ServiceApi> list = initData(id, replace, value, tags);
                        List<ServiceApi> insertList = new ArrayList<>();
                        if (CollectionUtils.isNotEmpty(list)) {
                            for (ServiceApi serviceApi : list) {
                                List<ServiceApi> apiList = serviceApiMapper.select(serviceApi);
                                if (CollectionUtils.isEmpty(apiList)) {
                                    insertList.add(serviceApi);
                                }
                            }
                        }
                        if (CollectionUtils.isNotEmpty(insertList)) {
                            serviceApiMapper.insertList(insertList);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error("服务接口同步失败",e);
            return false;
        }
        module.setSyncTime(new Date());
        serviceModuleMapper.updateByPrimaryKeySelective(module);
        return true;
    }

    public String replace(String key) {
        String pattern = "\\{.*\\}";
        Pattern compile = Pattern.compile(pattern);
        Matcher matcher = compile.matcher(key);
        return matcher.replaceAll("*");
    }

    public List<ServiceApi> initData(Integer id, String key, Method method, List<Tags> tags) {
        List<ServiceApi> list = new ArrayList<>();
        ServiceApi serviceApi;
        Property delete = method.getDelete();
        if (Objects.nonNull(delete)) {
            serviceApi = new ServiceApi();
            serviceApi.setServiceId(id);
            serviceApi.setPath(key);
            serviceApi.setOperateKey(delete.getOperationId());
            serviceApi.setMethod("DELETE");
            serviceApi.setDescription(delete.getSummary());
            serviceApi.setTags(delete.getTags().get(0));
            list.add(serviceApi);
        }
        Property get = method.getGet();
        if (Objects.nonNull(get)) {
            serviceApi = new ServiceApi();
            serviceApi.setServiceId(id);
            serviceApi.setPath(key);
            serviceApi.setOperateKey(get.getOperationId());
            serviceApi.setMethod("GET");
            serviceApi.setDescription(get.getSummary());
            serviceApi.setTags(get.getTags().get(0));
            list.add(serviceApi);
        }
        Property post = method.getPost();
        if (Objects.nonNull(post)) {
            serviceApi = new ServiceApi();
            serviceApi.setServiceId(id);
            serviceApi.setPath(key);
            serviceApi.setOperateKey(post.getOperationId());
            serviceApi.setMethod("POST");
            serviceApi.setDescription(post.getSummary());
            serviceApi.setTags(post.getTags().get(0));
            list.add(serviceApi);
        }
        Property patch = method.getPatch();
        if (Objects.nonNull(patch)) {
            serviceApi = new ServiceApi();
            serviceApi.setServiceId(id);
            serviceApi.setPath(key);
            serviceApi.setOperateKey(patch.getOperationId());
            serviceApi.setMethod("PATCH");
            serviceApi.setDescription(patch.getSummary());
            serviceApi.setTags(patch.getTags().get(0));
            list.add(serviceApi);
        }
        Property put = method.getPut();
        if (Objects.nonNull(put)) {
            serviceApi = new ServiceApi();
            serviceApi.setServiceId(id);
            serviceApi.setPath(key);
            serviceApi.setOperateKey(put.getOperationId());
            serviceApi.setMethod("PUT");
            serviceApi.setDescription(put.getSummary());
            serviceApi.setTags(put.getTags().get(0));
            list.add(serviceApi);
        }
        return list;
    }
}
