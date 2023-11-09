package com.vrv.vap.xc.mapper;

import com.vrv.vap.xc.model.LineModel;
import com.vrv.vap.xc.pojo.BaseLine;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 动态基线表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
public interface BaseLineMapper extends BaseMapper<BaseLine> {
    int saveBatch4List(List<BaseLine> list);

    List<Map<String,Object>> queryLineBysql(LineModel model);
    List<Map<String,Object>> querySummaryBysql(LineModel model);
}
