package com.vrv.vap.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.line.model.BaseLineScore;

import java.util.List;

/**
 * <p>
 * 动态基线表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
public interface BaseLineScoreMapper extends BaseMapper<BaseLineScore> {
    int saveBatch4List(List<BaseLineScore> list);
}
