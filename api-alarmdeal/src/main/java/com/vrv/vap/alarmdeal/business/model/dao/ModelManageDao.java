package com.vrv.vap.alarmdeal.business.model.dao;

import com.vrv.vap.alarmdeal.business.model.vo.ModelVersionVO;

import java.util.List;

public interface ModelManageDao {

    /**
     * 批量刪除
     * @param guids
     */
    public void deleteByGuids(List<String> guids);

    /**
     * 通过模型id，获取对应的版本信息
     * @param modelId
     * @return
     */
    public List<ModelVersionVO> queryModelVersions(String modelId);

    /**
     * 模型名称是不是存在
     * @param modelName
     * @return
     */
    public boolean existModelName(String modelName);
}
