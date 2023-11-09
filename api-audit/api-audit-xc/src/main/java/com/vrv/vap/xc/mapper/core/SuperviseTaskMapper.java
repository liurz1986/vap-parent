package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.model.EventTypeModel;
import com.vrv.vap.xc.pojo.SuperviseTask;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SuperviseTaskMapper extends BaseMapper<SuperviseTask> {
    @Select("SELECT notice_type                    AS name,\n" +
            "       SUM(IF(deal_status = 0, 1, 0)) AS untreated,\n" +
            "       SUM(IF(deal_status = 1, 1, 0)) AS processed\n" +
            "FROM supervise_task\n" +
            "WHERE deal_status < 2\n" +
            "GROUP BY notice_type")
    List<EventTypeModel> countByStatus();
}
