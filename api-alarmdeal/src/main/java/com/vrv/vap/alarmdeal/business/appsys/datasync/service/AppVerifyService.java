package com.vrv.vap.alarmdeal.business.appsys.datasync.service;

import com.vrv.vap.alarmdeal.business.appsys.datasync.model.AppSysManagerVerify;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppSysManagerSynchVo;
import com.vrv.vap.alarmdeal.business.appsys.datasync.vo.AppVerifySearchVO;
import com.vrv.vap.jpa.baseservice.BaseService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;

import java.util.List;

public interface AppVerifyService extends BaseService<AppSysManagerVerify, Integer> {

    /**
     * 编辑入库
     * @param appSysManagerVerify
     * @return
     */
    public Result<String> saveEditdData(AppSysManagerVerify appSysManagerVerify);

    /**
     * 查询
     * @param appVerifySearchVO
     * @return
     */
    public PageRes<AppSysManagerVerify> query(AppVerifySearchVO appVerifySearchVO);

    /**
     * 忽略
     * @param id
     * @return
     */
    public Result<String> neglect(Integer id);

    /**
     * 单条数据入库
     * @param id
     * @return
     */
    public Result<String> saveApp(Integer id);

    /**
     * 批量入库
     * @return
     */
    public Result<String> batchSaveApp();


}
