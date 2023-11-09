package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.WorkbenchAuthorityMapper;
import com.vrv.vap.admin.model.WorkbenchAuthority;
import com.vrv.vap.admin.service.WorkbenchAuthorityService;
import com.vrv.vap.base.BaseServiceImpl;
import net.logstash.logback.encoder.org.apache.commons.lang.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WorkbenchAuthorityServiceImpl extends BaseServiceImpl<WorkbenchAuthority> implements WorkbenchAuthorityService {


    @Autowired
    WorkbenchAuthorityMapper workbenchAuthorityMapper;

    /**
     * 同过roleId查询用户工作台权限
     */

    public WorkbenchAuthority findByRoleId(String roleId) {
        Example example = new Example(WorkbenchAuthority.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("roleId",roleId);
        List<WorkbenchAuthority> authorityList = workbenchAuthorityMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(authorityList)) {
            return authorityList.get(0);
        }
        return null;
    }

    /**
     * 获取用户角色的code集合
     */
    public String getCodesByUserId(List<Integer> roleIds) {
        Map<String, Object> onlyMap = new HashMap<>();
        String codes = "";
        for (Integer roleId : roleIds) {
            WorkbenchAuthority workbenchAuthority = findByRoleId(String.valueOf(roleId));
            if (workbenchAuthority != null) {
                String[] codeList = workbenchAuthority.getCodes().replace("[", "").replace("]", "").split(",");
                if (codeList.length == 0) {
                    return "";
                }
                for (String code : codeList) {
                    if (!onlyMap.containsKey(code)) {
                        codes = codes + "," + code;
                    }
                }
            }
        }
        if (StringUtils.isNotEmpty(codes)) {
            codes = "[" + codes.substring(1) + "]";
        } else {
            codes = "[]";
        }
        return codes;
    }
}
