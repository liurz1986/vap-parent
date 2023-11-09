package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.vo.SyncAssetVO;

/**
 * @author lilang
 * @date 2022/4/22
 * @description
 */
public interface BaseDataProducerService {

    void produce(SyncBaseData syncBaseData);

    void sendData(Object object, String dataType, String topicName);

    void saveLog(SyncBaseData syncBaseData, Integer totalCount, Integer status, String description);

    void completePersonInfo(SyncAssetVO syncAssetVO, String userName);

    void completeDomain(SyncAssetVO syncAssetVO);

    String getUrlPrefix(SyncBaseData syncBaseData);
}
