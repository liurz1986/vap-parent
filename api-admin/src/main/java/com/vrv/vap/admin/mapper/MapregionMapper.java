package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Mapregion;
import com.vrv.vap.base.BaseMapper;

import java.util.List;

public interface MapregionMapper  extends BaseMapper<Mapregion> {
    List<Mapregion> getMapregions();
}
