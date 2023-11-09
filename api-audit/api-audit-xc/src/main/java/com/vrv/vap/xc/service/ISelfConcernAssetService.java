package com.vrv.vap.xc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vrv.vap.xc.pojo.SelfConcernAsset;

import java.util.List;

public interface ISelfConcernAssetService extends IService<SelfConcernAsset> {

    List<SelfConcernAsset> findSelfConcernAsset(Integer userId);
}
