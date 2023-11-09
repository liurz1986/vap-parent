package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.Edge;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by CodeGenerator on 2018/07/12.
 */
public interface EdgeService extends BaseService<Edge> {

    /**
     * 根据搜索实体编号查询其相关探索关系
     * @param entityId
     * @return
     */
    List<Edge> queryEdgeBySearchEntityId(int entityId);
}
