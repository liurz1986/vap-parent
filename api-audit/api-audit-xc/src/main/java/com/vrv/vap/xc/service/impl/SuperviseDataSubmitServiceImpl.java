package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.SuperviseDataSubmitMapper;
import com.vrv.vap.xc.pojo.SuperviseDataSubmit;
import com.vrv.vap.xc.service.ISuperviseDataSubmitService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SuperviseDataSubmitServiceImpl extends ServiceImpl<SuperviseDataSubmitMapper, SuperviseDataSubmit> implements ISuperviseDataSubmitService {

    /**
     * 查询线索信息
     *
     * @param dataType
     * @return
     */
    public List<SuperviseDataSubmit> queryClueInfo(int dataType) {
        QueryWrapper<SuperviseDataSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("data_type", dataType);
        return this.list(queryWrapper);
    }

    public List<SuperviseDataSubmit> queryClueInfoByTime(int dataType, Date startTime, Date endTime) {
        QueryWrapper<SuperviseDataSubmit> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("data_type", dataType);
        queryWrapper.between("create_time", startTime, endTime);
        return this.list(queryWrapper);
    }
}
