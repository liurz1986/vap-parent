package com.vrv.vap.monitor.server.service.impl;

import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.monitor.server.model.CollectorDataAccess;
import com.vrv.vap.monitor.server.service.CollectorDataAccessService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CollectorDataAccessServiceImpl extends BaseServiceImpl<CollectorDataAccess> implements CollectorDataAccessService {
}
