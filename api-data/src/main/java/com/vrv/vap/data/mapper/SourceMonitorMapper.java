package com.vrv.vap.data.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.data.model.SourceMonitor;
import org.apache.ibatis.annotations.Select;

public interface SourceMonitorMapper extends BaseMapper<SourceMonitor> {

    @Select("SELECT source_id as sourceId,health,data_count,data_size as dataSize,index_size as indexSize,shards,indices,time FROM data_source_monitor WHERE source_id = #{sourceId} ORDER BY id DESC LIMIT 0,1")
    SourceMonitor findLastBySourceId(Integer sourceId);
}