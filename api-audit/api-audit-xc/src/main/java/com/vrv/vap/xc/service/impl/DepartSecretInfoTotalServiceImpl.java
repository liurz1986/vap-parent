package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.DepartSecretInfoTotalMapper;
import com.vrv.vap.xc.pojo.DepartSecretInfoTotal;
import com.vrv.vap.xc.service.DepartSecretInfoTotalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
public class DepartSecretInfoTotalServiceImpl extends ServiceImpl<DepartSecretInfoTotalMapper, DepartSecretInfoTotal> implements DepartSecretInfoTotalService {

    @Autowired
    private DepartSecretInfoTotalMapper departSecretInfoTotalMapper;

}
