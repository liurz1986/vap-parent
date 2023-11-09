package com.vrv.vap.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.line.model.BaseLine;
import org.apache.ibatis.annotations.Mapper;

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
@Mapper
public interface BaseLineMapper extends BaseMapper<BaseLine> {
    int saveBatch4List(List<BaseLine> list);

    List<Map<String,Object>> selectLineData(Map<String,Object> map);

    int countData(Map<String,Object> map);

    int queryMaxSummary(Map<String,Object> map);

    int updateSummary(Map<String,Object> map);
}
