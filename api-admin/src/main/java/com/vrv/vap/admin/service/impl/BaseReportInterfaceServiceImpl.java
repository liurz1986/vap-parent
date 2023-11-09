package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.util.Md5Util;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.mapper.BaseReportInterfaceMapper;
import com.vrv.vap.admin.model.BaseReportInterface;
import com.vrv.vap.admin.service.BaseReportInterfaceService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Service
public class BaseReportInterfaceServiceImpl extends BaseServiceImpl<BaseReportInterface> implements BaseReportInterfaceService {
    @Autowired
    private BaseReportInterfaceMapper mapper;

    @Override
    public BaseReportInterface add(BaseReportInterface record) {
        //判断指标是否存在
        Example example = new Example(BaseReportInterface.class);
        example.createCriteria().andEqualTo("url",record.getUrl()).andEqualTo("params",record.getParams());
        List<BaseReportInterface> olds = mapper.selectByExample(example);
        //计算md5
        String md5 = Md5Util.string2Md5(JSON.toJSONString(record));
        if(CollectionUtils.isNotEmpty(olds)){
            //指标存在
            BaseReportInterface oldInterface = olds.get(0);
            //比较md5，相同则不更新，不同则更新
            if(!md5.equals(oldInterface.getMd5())){
                //更新指标
                record.setId(oldInterface.getId());
                record.setMd5(md5);
                record.setTime(new Date());
                mapper.updateByPrimaryKeySelective(record);
            }
        }else{
            //指标不存在新增
            record.setId(Uuid.uuid());
            record.setTime(new Date());
            record.setMd5(md5);
            mapper.insertSelective(record);
        }
        return record;
    }

    @Override
    public List<BaseReportInterface> queryByParam(BaseReportInterface record) {
        Example example = new Example(BaseReportInterface.class);
        if(record != null && StringUtils.isNotEmpty(record.getType())){
            example.createCriteria().andEqualTo("type",record.getType());
        }
        return this.findByExample(example);
    }

    @Override
    public List<BaseReportInterface> selectByIds(Collection<String> ids) {
        Example example = new Example(BaseReportInterface.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("id",ids);
        return this.mapper.selectByExample(example);
    }

    @Override
    public BaseReportInterface findById(String id) {
        Example example = new Example(BaseReportInterface.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id",id);
        List<BaseReportInterface> reportList = this.mapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(reportList)) {
            return reportList.get(0);
        }
        return null;
    }
}
