package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.RptUserLogin;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@Mapper
public interface RptUserLoginMapper extends BaseMapper<RptUserLogin> {

    void replaceInto(List<RptUserLogin> list);
}
