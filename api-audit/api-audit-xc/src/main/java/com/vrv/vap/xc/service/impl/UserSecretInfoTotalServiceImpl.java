package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.RptUserLoginMapper;
import com.vrv.vap.xc.mapper.core.UserSecretInfoTotalMapper;
import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.pojo.UserSecretInfoTotal;
import com.vrv.vap.xc.service.RptUserLoginService;
import com.vrv.vap.xc.service.UserSecretInfoTotalService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-08-19
 */
@Service
public class UserSecretInfoTotalServiceImpl extends ServiceImpl<UserSecretInfoTotalMapper, UserSecretInfoTotal> implements UserSecretInfoTotalService {

    @Autowired
    private UserSecretInfoTotalMapper userSecretInfoTotalMapper;

    @Override
    public int replaceInto(List<UserSecretInfoTotal> list) {
        List<UserSecretInfoTotal> data = new ArrayList<>();
        list.forEach(e ->{
            UserSecretInfoTotal a = new UserSecretInfoTotal();
            BeanUtils.copyProperties(e, a);
            data.add(a);
        });
        return userSecretInfoTotalMapper.replaceInto(data);
    }
}
