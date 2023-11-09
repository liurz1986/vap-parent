package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.asset.model.SafeSecretProduce;
import com.vrv.vap.alarmdeal.business.asset.repository.SafeSecretProduceRepository;
import com.vrv.vap.alarmdeal.business.asset.service.SafeSecretProduceService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 安装的保密产品处理
 *
 * @author vrv
 */
@Service
public class SafeSecretProduceServiceImpl extends BaseServiceImpl<SafeSecretProduce, String> implements SafeSecretProduceService {

    @Autowired
    private SafeSecretProduceRepository safeSecretProduceRepository;
    @Override
    public BaseRepository<SafeSecretProduce, String> getRepository() {
        return safeSecretProduceRepository;
    }
}
