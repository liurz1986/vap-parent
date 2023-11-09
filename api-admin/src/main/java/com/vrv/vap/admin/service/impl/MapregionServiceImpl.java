package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.MapregionMapper;
import com.vrv.vap.admin.model.Mapregion;
import com.vrv.vap.admin.service.MapregionService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/05/28.
 */
@Service
@Transactional
public class MapregionServiceImpl extends BaseServiceImpl<Mapregion> implements MapregionService {

    @Resource
    private MapregionMapper mapregionMapper;

    @Override
    public List<Mapregion> getMapregions() {
        return mapregionMapper.getMapregions();
    }
}




