package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.asset.vo.AlarmEventMsgVO;

import java.util.List;

/**
 * asset.alarm.config
 */
public interface AssetAlarmService {

    /**
     * 资产变动触发告警事件(单个)
     *
     * 1.双系统事件
     *   主审---新增---操作系统多个--发kafka
     *   主审---修改---操作系统多个--操作系统变化--发kafka
     * 2.系统重装事件
     *   主审---编辑---操作系统安装时间变化--发kafka
     */

    public void assetChangeSendAlarmEvnet(AlarmEventMsgVO msg);


    /**
     * 资产变动触发告警事件(批量异步处理)
     *
     * 1.双系统事件
     *   主审---新增---操作系统多个--发kafka
     *   主审---修改---操作系统多个--操作系统变化--发kafka
     * 2.系统重装事件
     *   主审---编辑---操作系统安装时间变化--发kafka
     */
    public void assetChangeSendAlarmEvnets(List<AlarmEventMsgVO> datas);
}
