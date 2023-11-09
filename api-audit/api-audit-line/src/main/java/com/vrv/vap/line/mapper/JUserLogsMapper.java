package com.vrv.vap.line.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.line.model.BaseLine;
import com.vrv.vap.line.model.JUserLogs;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 动态基线表 Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
@Mapper
public interface JUserLogsMapper extends BaseMapper<JUserLogs> {
    int saveBatch4List(List<JUserLogs> list);
}
