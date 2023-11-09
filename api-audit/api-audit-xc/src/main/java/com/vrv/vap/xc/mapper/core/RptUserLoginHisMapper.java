package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.pojo.RptUserLoginHis;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@Mapper
public interface RptUserLoginHisMapper extends BaseMapper<RptUserLoginHis> {

    void saveBatch4List(List<Map<String, Object>> list);

    List<RptUserLogin> countAll();
}
