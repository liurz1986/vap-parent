package com.vrv.vap.alarmdeal.business.flow.core.service;

import com.vrv.vap.alarmdeal.business.flow.core.model.ListenerConfig;
import com.vrv.vap.alarmdeal.business.flow.core.repository.ListenerConfigRepository;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ListenerConfigService extends BaseServiceImpl<ListenerConfig, String> {
    @Autowired
    private ListenerConfigRepository listenerConfigRepository;
    @Override
    public BaseRepository<ListenerConfig, String> getRepository() {
        return listenerConfigRepository;
    }
}
