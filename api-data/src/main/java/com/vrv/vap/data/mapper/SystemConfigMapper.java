package com.vrv.vap.data.mapper;

import com.vrv.vap.base.BaseMapper;
import com.vrv.vap.data.model.SystemConfig;
import org.apache.ibatis.annotations.Mapper;


/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.mapper
 * @Author tongliang@VRV
 * @CreateTime 2019/03/11 15:01
 * @Description (密码复杂度及时效性配置的Mapper接口)
 * @Version
 */
@Mapper
public interface SystemConfigMapper extends BaseMapper<SystemConfig> {

}
