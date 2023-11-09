package com.vrv.vap.admin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.vrv.vap.admin.mapper.BaseReportModelMapper;
import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.admin.service.BaseReportModelService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * Created by Main on 2019/07/24.
 */
@Service
@Transactional
public class BaseReportModelServiceImpl extends BaseServiceImpl<BaseReportModel> implements BaseReportModelService {
    @Resource
    private BaseReportModelMapper baseReportModelMapper;


    @Override
    public Integer batchDelete(String ids) {
        return this.deleteByIds(BaseReportModel.class,"id",Arrays.asList(ids.split(",")));
    }

    @Override
    public List<BaseReportModel> selectByIds(Collection<String> ids) {
        Example example = new Example(BaseReportModel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        return baseReportModelMapper.selectByExample(example);
    }

    @Override
    public BaseReportModel findById(String id) {
        Example example = new Example(BaseReportModel.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        List<BaseReportModel> modelList = baseReportModelMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(modelList)) {
            return modelList.get(0);
        }
        return null;
    }
}
