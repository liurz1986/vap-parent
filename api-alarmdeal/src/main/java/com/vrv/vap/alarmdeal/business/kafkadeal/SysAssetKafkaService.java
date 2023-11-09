package com.vrv.vap.alarmdeal.business.kafkadeal;

/**
 * @author: 梁国露
 * @since: 2023/4/3 14:20
 * @description:
 */
public interface SysAssetKafkaService {
    void thirdTriggerFlowExecute(String message);

    void superviseTaskReceive(String message);

    void assetListen(String message);
}
