package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.mapper.EdgeMapper;
import com.vrv.vap.admin.model.Edge;
import com.vrv.vap.admin.service.EdgeService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by CodeGenerator on 2018/07/12.
 */
@Service
@Transactional
public class EdgeServiceImpl extends BaseServiceImpl<Edge> implements EdgeService {
    @Resource
    private EdgeMapper edgeMapper;

    @Override
    public List<Edge> queryEdgeBySearchEntityId(int entityId) {
        Example example = new Example(Edge.class);
        example.createCriteria().andEqualTo("searchEntityId", entityId);
        return this.findByExample(example);
    }
}
