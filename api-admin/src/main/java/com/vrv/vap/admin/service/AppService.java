package com.vrv.vap.admin.service;

import java.util.List;
import java.util.Map;
import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.AppRole;
import com.vrv.vap.admin.model.AppSort;
import com.vrv.vap.admin.vo.AppIconMenu;
import com.vrv.vap.base.BaseService;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by CodeGenerator on 2018/03/19.
 */
public interface AppService extends BaseService<App> {


    List<AppIconMenu> buildAppIconMenu(int userId, List<Integer> list) ;
	
    List<AppIconMenu>  getAllAppIconMenu();
    
    List<AppSort> getAppsByRoleId(AppRole appRole);

}
