package com.vrv.vap.xc.mapper.core.custom;

import com.vrv.vap.xc.model.TaskModel;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface TaskMapper {

    /**
     * 获取定时任务
     */
    List<TaskModel> queryTasks();

    /**
     * 获取区域信息
     */
    List<Map<String, Object>> queryBaseArea();

}
