package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vrv.vap.xc.mapper.core.RptUserLoginHisMapper;
import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.mapper.core.RptUserLoginMapper;
import com.vrv.vap.xc.service.RptUserLoginService;
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
public class RptUserLoginServiceImpl extends ServiceImpl<RptUserLoginMapper, RptUserLogin> implements RptUserLoginService {

    @Autowired
    private RptUserLoginMapper rptUserLoginMapper;

    @Override
    public void replaceInto(List<RptUserLogin> list) {
        List<RptUserLogin> resultrecord = new ArrayList<>();
        list.forEach(e ->{
            RptUserLogin a = new RptUserLogin();
            BeanUtils.copyProperties(e, a);
            resultrecord.add(a);
        });
        rptUserLoginMapper.replaceInto(resultrecord);
    }
}
