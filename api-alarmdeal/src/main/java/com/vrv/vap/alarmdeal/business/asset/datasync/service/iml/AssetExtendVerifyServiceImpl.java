package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetExtendVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.repository.AssetExtendVerifyRepository;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetExtendVerifyService;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 待审扩展信息
 *
 * 2022-07-12
 */
@Service
public class AssetExtendVerifyServiceImpl extends BaseServiceImpl<AssetExtendVerify, String> implements AssetExtendVerifyService {


    @Autowired
    private AssetExtendVerifyRepository assetExtendVerifyRepository;
    @Override
    public BaseRepository<AssetExtendVerify, String> getRepository() {
        return this.assetExtendVerifyRepository;
    }
}
