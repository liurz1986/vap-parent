package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.SuperviseTaskMapper;
import com.vrv.vap.xc.model.EventTypeModel;
import com.vrv.vap.xc.pojo.SuperviseTask;
import com.vrv.vap.xc.service.ISuperviseTaskService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class SuperviseTaskServiceImpl extends ServiceImpl<SuperviseTaskMapper, SuperviseTask> implements ISuperviseTaskService {

    @Resource
    private SuperviseTaskMapper superviseTaskMapper;

    public List<EventTypeModel> countByStatus(){
        return superviseTaskMapper.countByStatus();
    }

}
