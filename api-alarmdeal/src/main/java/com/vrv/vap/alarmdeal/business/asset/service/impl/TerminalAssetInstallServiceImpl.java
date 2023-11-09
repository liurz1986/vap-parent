package com.vrv.vap.alarmdeal.business.asset.service.impl;
import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.TerminalAssetInstallService;
import com.vrv.vap.jpa.common.UUIDUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 终端设置统计审计客户端安装情况
 * 2021 - 08 -24
 *
 * 改为数据同步时触发，发kafka消息  2022-07-13
 * 自动入库、单条入库、批量入库三种场景发送kafka消息
 * 存在数据不准确性风险
 * 增加资产编辑场景发送kafka消息  2022-1-30
 *
 *
 *
 */
@Service
public class TerminalAssetInstallServiceImpl implements TerminalAssetInstallService {
    Logger logger = LoggerFactory.getLogger(TerminalAssetInstallServiceImpl.class);
    @Autowired
    private AssetService assetService;


    @Value("${asset.terminal.install.count.topic:asset_terminal_install_count}")
    private String alarmTopic;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送统计情况
     */
    @Override
    public void sendCountKafkaMsg(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    List<Map<String, Object>> data =  assetService.terminalAssetInstallCount();
                    int isinstall = 0;
                    int uninstall = 0;
                    if( null != data && data.size() > 0){
                        for(Map<String, Object> param : data){
                            if(null !=param.get("typeName")&&"isinstall".equals(param.get("typeName"))){
                                isinstall = Integer.valueOf(String.valueOf(param.get("number")));
                            }
                            if(null !=param.get("typeName")&&"uninstall".equals(param.get("typeName"))){
                                uninstall = Integer.valueOf(String.valueOf(param.get("number")));
                            }
                        }
                    }
                    Map<String,Object> message = new HashMap<String,Object>();
                    message.put("is_install",isinstall);
                    message.put("un_install",uninstall);
                    message.put("guid", UUIDUtils.get32UUID());
                    // 获取未安装列表数据 2023-09-26
                    List<Map<String,Object>> list = assetService.getUnInstallList();
                    message.put("not_instald_list", list);
                    String msg = JSON.toJSONString(message);
                    kafkaTemplate.send(alarmTopic,msg);
                }catch(Exception e){
                    logger.error("终端设置统计审计客户端安装情况发kafka异常",e);
                }
            }
        }).start();
    }

}
