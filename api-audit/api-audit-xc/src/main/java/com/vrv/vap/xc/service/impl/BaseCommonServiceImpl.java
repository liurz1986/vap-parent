package com.vrv.vap.xc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.toolkit.model.UserInfoModel;
import com.vrv.vap.toolkit.tools.SessionTools;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.xc.constants.FieldLibrary;
import com.vrv.vap.xc.mapper.core.ConfLookupMapper;
import com.vrv.vap.xc.mapper.core.ObjectAnalyseConfigMapper;
import com.vrv.vap.xc.pojo.ConfLookup;
import com.vrv.vap.xc.pojo.ObjectAnalyseConfig;
import com.vrv.vap.xc.service.BaseCommonService;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.tools.LogAssistTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.vo.ConfLookupQuery;
import com.vrv.vap.xc.vo.ObjectAnalyseConfigQuery;
import java.util.List;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by lilei on 2021/08/06.
 */
@Service
@Transactional
public class BaseCommonServiceImpl implements BaseCommonService {

    @Autowired
    private ConfLookupMapper confLookupMapper;
    @Autowired
    private ObjectAnalyseConfigMapper objectAnalyseConfigMapper;

    @Override
    public VList<ConfLookup> getConfLookup(ConfLookupQuery record) {
        Page<ConfLookup> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<ConfLookup> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);

        return VoBuilder.vl(confLookupMapper.selectPage(page,queryWrapper));
    }

    @Override
    public int updateConfLookup(ConfLookup record) {
        ConfLookup old = confLookupMapper.selectById(record.getId());
        if (old != null) {
            //审计变化
            String changes = LogAssistTools.compareDesc(old, record);
            record.setExtendDesc(changes);
        }
        return confLookupMapper.updateById(record);
    }

    @Override
    public VData<List<ObjectAnalyseConfig>> queryObjectAnalyseConfig(ObjectAnalyseConfigQuery param) {
        Optional<UserInfoModel> user = SessionTools.getUserInfo();
        user.ifPresent(userInfoModel -> param.setUserAccount(userInfoModel.getAccount()));
        QueryWrapper<ObjectAnalyseConfig> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, param);
        return VoBuilder.vd(objectAnalyseConfigMapper.selectList(queryWrapper));
    }

    @Override
    public Result updateObjectAnalyseConfig(ObjectAnalyseConfig param) {
        Optional<UserInfoModel> user = SessionTools.getUserInfo();
        ObjectAnalyseConfigQuery query = new ObjectAnalyseConfigQuery();
        ObjectAnalyseConfig newConfig = new ObjectAnalyseConfig();
        if(StringUtils.isNotEmpty(param.getValue())){
            newConfig.setValue(FieldLibrary.transConfig(param.getValue()));
        }
        query.setType(param.getType());
        if (user.isPresent()) {
            param.setUserAccount(user.get().getAccount());
            query.setUserAccount(user.get().getAccount());
        }
        if (queryObjectAnalyseConfig(query).getData().size() > 0) {
            ObjectAnalyseConfig data = queryObjectAnalyseConfig(query).getData().get(0);
            ObjectAnalyseConfig old = new ObjectAnalyseConfig();
            if(StringUtils.isNotEmpty(data.getValue())){
                old.setValue(FieldLibrary.transConfig(data.getValue()));
            }
            param.setExtendDesc(LogAssistTools.compareDesc(old, newConfig));
            objectAnalyseConfigMapper.updateById(param);
        } else {
            ObjectAnalyseConfig old = new ObjectAnalyseConfig();
            param.setExtendDesc(LogAssistTools.compareDesc(old, newConfig));
            objectAnalyseConfigMapper.insert(param);
        }
        return VoBuilder.success();
    }

}
