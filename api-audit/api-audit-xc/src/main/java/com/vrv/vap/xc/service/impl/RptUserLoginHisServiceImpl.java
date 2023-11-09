package com.vrv.vap.xc.service.impl;

import com.vrv.vap.xc.mapper.core.RptUserLoginHisMapper;
import com.vrv.vap.xc.pojo.RptUserLogin;
import com.vrv.vap.xc.pojo.RptUserLoginHis;
import com.vrv.vap.xc.service.RptUserLoginHisService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
public class RptUserLoginHisServiceImpl extends ServiceImpl<RptUserLoginHisMapper, RptUserLoginHis> implements RptUserLoginHisService {

    @Autowired
    private RptUserLoginHisMapper rptUserLoginHisMapper;

    @Override
    public void saveBatch4List(List<Map<String, Object>> list) {
        rptUserLoginHisMapper.saveBatch4List(list);
    }

    @Override
    public List<RptUserLogin> countAll() {
        return rptUserLoginHisMapper.countAll();
    }
}
