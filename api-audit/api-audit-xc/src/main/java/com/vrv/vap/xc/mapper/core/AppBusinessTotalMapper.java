package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.AppBusinessTotal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 主机业务数据汇总表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-23
 */
@Mapper
public interface AppBusinessTotalMapper extends BaseMapper<AppBusinessTotal> {
    int replaceInto(List<AppBusinessTotal> list);
}
