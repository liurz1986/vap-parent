package com.vrv.vap.admin.mapper;

import com.vrv.vap.admin.model.Edge;
import com.vrv.vap.base.BaseMapper;

import java.util.List;

public interface EdgeMapper extends BaseMapper<Edge> {
    List<Edge> queryEdgeBySearchEntityId(int entityId);
}