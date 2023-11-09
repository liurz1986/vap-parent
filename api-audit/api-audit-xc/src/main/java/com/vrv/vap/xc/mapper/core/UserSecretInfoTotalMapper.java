package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.UserSecretInfoTotal;
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
public interface UserSecretInfoTotalMapper extends BaseMapper<UserSecretInfoTotal> {

    int replaceInto(List<UserSecretInfoTotal> list);
}
