package com.vrv.vap.xc.mapper.rds2;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.WhiteList;

/**
 * <p>
 * XC-数据源管理 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
public interface WhiteListMapper extends BaseMapper<WhiteList> {

    int queryWhiteListStatistic(String number);

}
