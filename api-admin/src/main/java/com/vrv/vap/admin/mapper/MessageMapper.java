package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Message;
import com.vrv.vap.base.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    int markReads(@Param("ids") String[] ids);

}