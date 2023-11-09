package com.vrv.vap.xc.mapper.core;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.vrv.vap.xc.pojo.UserSecretInfoDay;
import com.vrv.vap.xc.pojo.UserSecretInfoTotal;
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
public interface UserSecretInfoDayMapper extends BaseMapper<UserSecretInfoDay> {

    int saveBatch4List(List<UserSecretInfoDay> list);

    List<UserSecretInfoTotal> countAll();

    List<Map<String,String>> countByDepart();

}
