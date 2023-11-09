package com.vrv.vap.server.push.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.server.push.model.Message;
import com.vrv.vap.server.push.vo.MessageVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageMapper extends BaseMapper<Message> {

    int insertByRole(@Param("group") MessageVO group);

}