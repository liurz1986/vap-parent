package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.UserSecretInfoTotal;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
public interface UserSecretInfoTotalService extends IService<UserSecretInfoTotal> {
    int replaceInto(List<UserSecretInfoTotal> list);
}
