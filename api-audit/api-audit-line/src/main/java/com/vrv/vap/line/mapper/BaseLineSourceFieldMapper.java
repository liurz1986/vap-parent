package com.vrv.vap.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.line.model.BaseLineSourceField;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 基线数据源字段表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-21
 */
@Mapper
public interface BaseLineSourceFieldMapper extends BaseMapper<BaseLineSourceField> {
    int saveBatch4List(List<BaseLineSourceField> list);
}
