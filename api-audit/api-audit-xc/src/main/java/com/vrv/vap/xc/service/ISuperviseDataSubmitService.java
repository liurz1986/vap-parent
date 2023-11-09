package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.SuperviseDataSubmit;

import java.util.Date;
import java.util.List;

public interface ISuperviseDataSubmitService extends IService<SuperviseDataSubmit> {
    /**
     * 根据类型查询线索信息
     *
     * @param dataType
     * @return
     */
    List<SuperviseDataSubmit> queryClueInfo(int dataType);

    /**
     * 根据时间和类型查询线索信息
     *
     * @param dataType
     * @return
     */
    List<SuperviseDataSubmit> queryClueInfoByTime(int dataType, Date startTime, Date endTime);
}
