package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.DepartVisitAppTotal;
import com.vrv.vap.xc.pojo.UserVisitAppDay;
import com.vrv.vap.xc.pojo.UserVisitAppTotal;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 每日人员访问应用情况统计表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-20
 */
@Mapper
public interface UserVisitAppDayMapper extends BaseMapper<UserVisitAppDay> {
    int saveBatch4List(List<UserVisitAppDay> list);

    List<UserVisitAppTotal> countAll();

    List<DepartVisitAppTotal> countDepart();
}
