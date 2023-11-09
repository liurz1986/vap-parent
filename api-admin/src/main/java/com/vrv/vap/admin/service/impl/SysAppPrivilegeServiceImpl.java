package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.SysAppPrivilegeMapper;
import com.vrv.vap.admin.model.SysAppPrivilege;
import com.vrv.vap.admin.service.SysAppPrivilegeService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/10/26.
 */
@Service
@Transactional
public class SysAppPrivilegeServiceImpl extends BaseServiceImpl<SysAppPrivilege> implements SysAppPrivilegeService {
    @Resource
    private SysAppPrivilegeMapper sysAppPrivilegeMapper;

    @Override
    public boolean deleteByAppIds(List<Integer> appIds) {
        Example example = new Example(SysAppPrivilege.class);
        example.createCriteria().andIn("appId", appIds);
        this.sysAppPrivilegeMapper.deleteByExample(example);
        return false;
    }

    @Override
    public boolean managerAppPrivilege(Integer appId, String[] addList, String[] delList) {
        List<SysAppPrivilege> list = new ArrayList<>();

        if (addList.length > 0) {
            for (String add : addList) {
                SysAppPrivilege appPrivilege = new SysAppPrivilege();
                appPrivilege.setAppId(appId);
                appPrivilege.setPrivilegeId(Integer.parseInt(add));
                list.add(appPrivilege);
            }
            this.save(list);
        }

        if (delList.length > 0) {
            Example example = new Example(SysAppPrivilege.class);
            example.createCriteria().andEqualTo("appId", appId)
                    .andIn("privilegeId", Arrays.asList(delList));
            this.sysAppPrivilegeMapper.deleteByExample(example);
        }
        return true;
    }

    @Override
    public void insertBuiltIn(Integer id) {
        sysAppPrivilegeMapper.insertBuiltIn(id);
    }
}
