package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.model.EventTypeModel;
import com.vrv.vap.xc.pojo.SuperviseTask;

import java.util.List;

public interface ISuperviseTaskService extends IService<SuperviseTask> {

    List<EventTypeModel> countByStatus();

}
