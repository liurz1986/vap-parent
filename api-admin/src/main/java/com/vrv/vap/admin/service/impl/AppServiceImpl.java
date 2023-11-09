package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.AppMapper;
import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.AppRole;
import com.vrv.vap.admin.model.AppSort;
import com.vrv.vap.admin.service.AppService;
import com.vrv.vap.admin.vo.AppIconMenu;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.annotation.Resource;

/**
 * Created by CodeGenerator on 2018/03/19.
 */
@Service
@Transactional
public class AppServiceImpl extends BaseServiceImpl<App> implements AppService {
    @Resource
    private AppMapper appMapper;


    private AppIconMenu buildTree(List<App> appList, AppIconMenu menu) {
        for (App app : appList) {
            if (app.getParentId() == menu.getAppid()) {
                AppIconMenu sub = new AppIconMenu();
                sub.setTitle(app.getName());
                sub.setAppid(app.getId());
                sub.setFolder(app.getFolder());
                sub.setIcon(app.getIcon());
                sub.setType(app.getType());
                sub.setPath(app.getUrl());
                sub = buildTree(appList, sub);
                menu.appendChild(sub);
            }
        }
        return menu;
    }

    private void appendTo(List<AppIconMenu> list, AppSort app) {
        AppIconMenu menu = new AppIconMenu();
        menu.setAppid(app.getId());
        menu.setTitle(app.getName());
        menu.setIcon(app.getIcon());
        menu.setType(app.getType());
        menu.setPath(app.getUrl());
        menu.setSort(app.getSort());
        if (app.getParentId() == 0) {
            list.add(menu);
        } else {
            for (AppIconMenu appIconMenu : list) {
                if (appIconMenu.getAppid() == app.getParentId()) {
                    appIconMenu.appendChild(menu);
                    break;
                }
            }
        }


    }


    @Override
    public List<AppIconMenu> buildAppIconMenu(int userId, List<Integer> list) {
        List<AppIconMenu> appIconMenuList = new ArrayList<AppIconMenu>();

        //角色默认的应用
        List<AppSort> roleApps = appMapper.getAppsByRoleIds(list);
//        List<>
        //用户自行管理的应用,本期不做，先注释
//        List<AppUser> userApps = appUserMapper.selectByExample(new Example(AppUser.class).createCriteria().andEqualTo("userId",userId));

        // 根据角色应用和用户应用拼装出最终应用列表,先拼第一层，再拼第二层
        // 先进行排序后再组装，组装出来后，就不用再次排序了
        roleApps.sort(Comparator.comparingInt(AppSort::getSort));
        for (AppSort app : roleApps) {
            if (app.getParentId() == 0) {
                this.appendTo(appIconMenuList, app);
            }
        }
        for (AppSort app : roleApps) {
            if (app.getParentId() != 0) {
                this.appendTo(appIconMenuList, app);
            }
        }

        return appIconMenuList;
    }


    @Override
    public List<AppIconMenu> getAllAppIconMenu() {
        List<App> appList = appMapper.getAll();
        AppIconMenu menu = new AppIconMenu();
        menu.setAppid(0);
        buildTree(appList, menu);
        return menu.getChildren();
    }

    @Override
    public List<AppSort> getAppsByRoleId(AppRole appRole) {
        return appMapper.getAppsByRoleId(appRole.getRoleId());
    }
}
