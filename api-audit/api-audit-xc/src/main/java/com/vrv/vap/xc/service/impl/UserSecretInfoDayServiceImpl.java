package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.UserSecretInfoDayMapper;
import com.vrv.vap.xc.mapper.core.UserSecretInfoTotalMapper;
import com.vrv.vap.xc.pojo.UserSecretInfoDay;
import com.vrv.vap.xc.pojo.UserSecretInfoTotal;
import com.vrv.vap.xc.service.UserSecretInfoDayService;
import com.vrv.vap.xc.service.UserSecretInfoTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@Service
public class UserSecretInfoDayServiceImpl extends ServiceImpl<UserSecretInfoDayMapper, UserSecretInfoDay> implements UserSecretInfoDayService {

    @Autowired
    private UserSecretInfoDayMapper userSecretInfoDayMapper;

    @Override
    public int saveBatch4List(List<UserSecretInfoDay> list) {
        return userSecretInfoDayMapper.saveBatch4List(list);
    }

    @Override
    public List<UserSecretInfoTotal> countAll() {
        return userSecretInfoDayMapper.countAll();
    }

    @Override
    public List<Map<String, String>> countByDepart() {
        return userSecretInfoDayMapper.countByDepart();
    }
}
