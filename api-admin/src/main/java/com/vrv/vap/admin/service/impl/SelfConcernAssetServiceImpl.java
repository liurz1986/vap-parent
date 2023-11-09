package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.SelfConcernAssetMapper;
import com.vrv.vap.admin.model.SelfConcernAsset;
import com.vrv.vap.admin.service.SelfConcernAssetService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by The VAP Team on 2023-09-14.
 */
@Service
@Transactional
public class SelfConcernAssetServiceImpl extends BaseServiceImpl<SelfConcernAsset> implements SelfConcernAssetService {
    @Resource
    private SelfConcernAssetMapper selfConcernAssetMapper;

    @Override
    public List<SelfConcernAsset> getByUseId(Integer userId) {
        Example example=new Example(SelfConcernAsset.class);
        example.createCriteria().andEqualTo("userId",userId).andEqualTo("type",2);
        List<SelfConcernAsset> selfConcernAssets = selfConcernAssetMapper.selectByExample(example);
        return selfConcernAssets;
    }
}
