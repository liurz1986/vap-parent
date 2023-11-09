package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SelfConcernAsset;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by The VAP Team on 2023-09-14.
 */
public interface SelfConcernAssetService extends BaseService<SelfConcernAsset>{

    List<SelfConcernAsset> getByUseId(Integer userId);
}
