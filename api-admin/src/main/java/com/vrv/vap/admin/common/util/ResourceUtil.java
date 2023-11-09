package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.admin.service.ResourceService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ResourceUtil {

    private static Map<String, String> resourceDescMap = new HashMap<>();

    @Autowired
    private ResourceService resourceService;

    @PostConstruct
    public void initResourceDescMap() {
        List<Resource> resourceList = resourceService.findAll();
        if (CollectionUtils.isNotEmpty(resourceList)) {
            resourceList.stream().forEach(p -> {
                resourceDescMap.put(p.getPath(), p.getTitle());
            });
        }
    }

    public static Map<String, String> getResourceDescMap() {
        return resourceDescMap;
    }

}
