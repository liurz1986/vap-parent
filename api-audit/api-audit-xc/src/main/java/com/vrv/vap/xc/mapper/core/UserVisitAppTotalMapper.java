package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.UserVisitAppTotal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 全量人员访问应用情况统计表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-20
 */
@Mapper
public interface UserVisitAppTotalMapper extends BaseMapper<UserVisitAppTotal> {

    int replaceInto(List<UserVisitAppTotal> list);
}
