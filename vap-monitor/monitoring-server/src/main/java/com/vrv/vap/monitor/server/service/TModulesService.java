package com.vrv.vap.monitor.server.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.monitor.server.model.TModules;
import com.vrv.vap.monitor.server.vo.TModulesVO;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/03/19.
 */
public interface TModulesService extends BaseService<TModules>{


    List<TModulesVO> getTmodules() ;

    String executeLinuxCmd(String cmd);

    String executeLinuxCmd2(String[] cmd);

}
