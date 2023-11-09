package com.vrv.vap.alarmdeal.business.asset.service.impl;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.service.AssetAlarmService;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.AlarmEventMsgVO;
import com.vrv.vap.exportAndImport.excel.util.DateUtils;
import com.vrv.vap.jpa.common.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

/**
 * 资产触发告警事件
 *
 * 1.实际上就是针对终端资产
 * 2.目前涉及：资产编辑、资产数据同步
 * 2022-07-11
 */
@Service
public class AssetAlarmServiceImpl implements AssetAlarmService {
    Logger logger = LoggerFactory.getLogger(AssetAlarmServiceImpl.class);

    public static final  String OSTYPESAVE = "1";

    public static final  String OSTYPEEDIT = "2";
    @Value("${asset.alarm.config}")
    private String alarmConfig;

    @Value("${asset.alarm.topic:offline-data-collect-common}")
    private String alarmTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    /**
     * 资产变动触发告警事件
     *
     * 1.双系统事件
     *   主审---新增---操作系统多个--发kafka
     *   主审---修改---操作系统多个--操作系统变化--发kafka
     * 2.系统重装事件
     *   主审---编辑---操作系统安装时间变化--发kafka
     *
     */
    @Override
    public void assetChangeSendAlarmEvnet(AlarmEventMsgVO msg){
        try{
            // syncSource为空不处理
            String syncSource = msg.getSyncSource();
            if(StringUtils.isEmpty(syncSource)){
                return;
            }
            // 判断是不是终端
            boolean isHost = AssetValidateUtil.isHost(msg.getTypeTreeCode());
            if(!isHost){
                logger.info("非终端不处理");
                return;
            }
            if(StringUtils.isEmpty(alarmConfig)){
                logger.info("没有配置触发资产变化发告警事件的条件！");
                return;
            }
            String[] configs = alarmConfig.split(",");
            List<String> alarmConfigs = Arrays.asList(configs);
            if(!alarmConfigs.contains(syncSource)){
                logger.info("不在资产变动发告警的范围内！");
                return;
            }
            // 双系统事件
            osListHandle(msg);
            // 系统重装事件
            osSetuptimeHandle(msg);

        }catch(Exception e){
            logger.error("资产变动触发告警事件异常",e);
        }

    }

    /**
     * 资产变动触发告警事件(批量异步处理)
     *
     * 1.双系统事件
     *   主审---新增---操作系统多个--发kafka
     *   主审---修改---操作系统多个--操作系统变化--发kafka
     * 2.系统重装事件
     *   主审---编辑---操作系统安装时间变化--发kafka
     */
    @Override
    public void assetChangeSendAlarmEvnets(List<AlarmEventMsgVO> datas){
         new Thread(new Runnable() {
             @Override
             public void run() {
                 for(AlarmEventMsgVO data : datas){
                     assetChangeSendAlarmEvnet(data);
                 }
             }
         }).start();
    }

    /**
     * 双系统处理
     *
     * 主审---新增---操作系统多个--发kafka
     * 主审---修改---操作系统多个--操作系统变化--发kafka
     * @param msg
     */
    private void osListHandle(AlarmEventMsgVO msg) {
        // 操作系统多个
        String osList = msg.getOsList();
        if(StringUtils.isEmpty(osList)){
            return;
        }
        String[] osLists = osList.split(",");
        // 单个不处理
        List<String> osListDatas = Arrays.asList(osLists);
        if(osListDatas.size() == 1){
            return;
        }
        // 编辑状态，比较是否变化
        if(OSTYPEEDIT.equals(msg.getOsType())){
            if(msg.getOsList().equals(msg.getOsListOld())){
                // 没变化不处理
                return;
            }
        }
        // 发kafka消息
        excSendKafkaMsg(msg.getIp(),"多系统和虚拟机","8","DT033");
    }


    /**
     * 系统重装事件处理
     *
     * 主审---编辑---操作系统安装时间变化--发kafka
     * @param msg
     */
    private void osSetuptimeHandle(AlarmEventMsgVO msg) {
        // 为空不处理
        if(null == msg.getOsSetuptime()){
            return;
        }
        // 只处理编辑状态
        if(!OSTYPEEDIT.equals(msg.getOsType())){
            return;
        }
       // 判断安装时间是否变化
        Date currentOsSetupTime = msg.getOsSetuptime();
        Date OsSetuptimeOld = msg.getOsSetuptimeOld();
        String currentOsSetupTimeStr = "";
        String OsSetuptimeOldStr = "";
        if(null != currentOsSetupTime){
            currentOsSetupTimeStr = DateUtils.date2Str(currentOsSetupTime,"yyyy-MM-dd"); //转成日期
        }
        if(null != OsSetuptimeOld){
            OsSetuptimeOldStr= DateUtils.date2Str(OsSetuptimeOld,"yyyy-MM-dd"); //转成日期
        }
        if(currentOsSetupTimeStr.equals(OsSetuptimeOldStr)){
            // 没变化不处理
            return;
        }
        // 发kafka消息
        excSendKafkaMsg(msg.getIp(),"重装系统","11","DT033");
    }

    private void excSendKafkaMsg(String ip, String descripTion, String opType, String logType) {
        Map<String,Object> message = new HashMap<String,Object>();
        message.put("dev_ip",ip);
        message.put("event_time",DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss"));
        message.put("op_type",opType);
        message.put("op_description",descripTion);
        message.put("report_log_type",logType);
        String msg = JSON.toJSONString(message);
        kafkaTemplate.send(alarmTopic,msg);
    }
}

