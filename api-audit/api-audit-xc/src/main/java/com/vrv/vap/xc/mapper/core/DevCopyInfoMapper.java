package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.DevCopyInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 设备拷贝数据量表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-23
 */
@Mapper
public interface DevCopyInfoMapper extends BaseMapper<DevCopyInfo> {

    int saveBatch4List(List<DevCopyInfo> list);
}
