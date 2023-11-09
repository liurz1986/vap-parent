package com.vrv.vap.alarmdeal.business.asset.service;

public interface TerminalAssetInstallService {
    /**
     * 发送统计情况给kafka
     */
    public void sendCountKafkaMsg();
}
