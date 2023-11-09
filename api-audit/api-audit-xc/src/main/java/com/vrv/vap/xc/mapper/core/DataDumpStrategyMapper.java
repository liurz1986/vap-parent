package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.DataDumpStrategy;
import java.util.List;

/**
 * <p>
 * 数据备份策略 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
public interface DataDumpStrategyMapper extends BaseMapper<DataDumpStrategy> {
    List<DataDumpStrategy> selectExistDataList(DataDumpStrategy record);

}
