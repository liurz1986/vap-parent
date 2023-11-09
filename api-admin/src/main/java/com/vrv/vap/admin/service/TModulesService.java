package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.TModules;
import com.vrv.vap.admin.vo.TModulesVO;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/03/19.
 */
public interface TModulesService extends BaseService<TModules> {


    List<TModulesVO> getTmodules() ;

    String executeLinuxCmd(String cmd);

    String executeLinuxCmd2(String[] cmd);

}
