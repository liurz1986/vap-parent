package com.vrv.vap.admin.service;


import com.vrv.vap.admin.model.Mapregion;
import com.vrv.vap.base.BaseService;

import java.util.List;

public interface MapregionService extends BaseService<Mapregion> {
    List<Mapregion>  getMapregions();
}
