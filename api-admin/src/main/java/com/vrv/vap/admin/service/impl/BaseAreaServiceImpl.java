package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.BaseAreaMapper;
import com.vrv.vap.admin.model.BaseArea;
import com.vrv.vap.admin.service.BaseAreaService;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;


/**
 * Created by Main on 2019/07/24.
 */
@Service
@Transactional
public class BaseAreaServiceImpl extends BaseServiceImpl<BaseArea> implements BaseAreaService {
    @Resource
    private BaseAreaMapper baseAreaMapper;


    @Override
    public BaseArea findByCode(String areaCode) {
        Example example = new Example(BaseArea.class);
        example.setOrderByClause(" sort ASC");
        example.createCriteria().andEqualTo("areaCode",areaCode);
        List<BaseArea> areaList = baseAreaMapper.selectByExample(example);
        if (!CollectionUtils.isEmpty(areaList)) {
            return areaList.get(0);
        }
        return null;
    }

    @Override
    public List<BaseArea> findSubAreaByCode(String areaCode) {
        Example example = new Example(BaseArea.class);
        if(StringUtils.isEmpty(areaCode)){
            Example.Criteria criteria = example.createCriteria();
            criteria.orIsNull("parentCode");
            criteria.orEqualTo("parentCode","");
        }else{
            Example.Criteria criteria = example.createCriteria();
            criteria.orEqualTo("parentCode",areaCode);
        }
        return this.findByExample(example);
    }
}
