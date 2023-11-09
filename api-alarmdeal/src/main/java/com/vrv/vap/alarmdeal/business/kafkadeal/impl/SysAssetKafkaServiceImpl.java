package com.vrv.vap.alarmdeal.business.kafkadeal.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.SuperviseTaskService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.SuperviseTaskReceiveVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.BaseSynchKafkaVO;
import com.vrv.vap.alarmdeal.business.asset.util.QueUtil;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessVO;
import com.vrv.vap.alarmdeal.business.flow.core.service.BusinessTaskService;
import com.vrv.vap.alarmdeal.business.kafkadeal.SysAssetKafkaService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2023/4/3 14:20
 * @description:
 */
@Service
public class SysAssetKafkaServiceImpl implements SysAssetKafkaService {

    private static Logger logger = LoggerFactory.getLogger(SysAssetKafkaServiceImpl.class);

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Autowired
    private SuperviseTaskService superviseTaskService;

    @Autowired
    private BusinessTaskService businessTaskService;


    @Override
    public void thirdTriggerFlowExecute(String message) {
        logger.info("消费到第三方数据信息是：" + message.toString());
        BusinessVO businessVO = contructBusinessVO(message.toString());
        businessTaskService.completeTaskByThirdBusiness(businessVO);
    }

    @Override
    public void superviseTaskReceive(String message) {
        logger.info("-----SuperviseAnnounce主题消费到kafka数据为{}", message);
        Map<String, Object> downPullData = gson.fromJson(message,Map.class);
        Object notice_type = downPullData.get("notice_type");
        if(notice_type==null){
            logger.error("###########下发的数据不含notice_type，或者notice_type值为空，下发格式存在问题！################");
            return;
        }
        List<Map<String, Object>> data = (List) downPullData.get("data");
        if (data == null) {
            return;
        }
        Object push_time = downPullData.get("push_time");
        //下发的data格式是一个列表
        for (Map<String, Object> item : data) {
            SuperviseTaskReceiveVo superviseTaskReceiveVo = gson.fromJson(gson.toJson(item), SuperviseTaskReceiveVo.class);
            //业务数据，以前的字段是直接展开的，后面加的不再展开了，字段太多了，直接存json。
            superviseTaskReceiveVo.setBusiArgs(gson.toJson(item));
            //发送时间
            if(push_time!=null){
                superviseTaskReceiveVo.setSendTime(push_time.toString());
            }
            //处理类型
            superviseTaskReceiveVo.setNoticeType(notice_type.toString());
            //拉取（协办任务 协办反馈 下发预警）
            superviseTaskService.pullSuperviseTask(superviseTaskReceiveVo);
        }

    }



    @Override
    public void assetListen(String message) {
        BaseSynchKafkaVO assetKafkaVO = null;
        assetKafkaVO = JSONObject.parseObject(message.toString(), BaseSynchKafkaVO.class);
        logger.debug("外部同步数据kafka数据：" + JSON.toJSONString(assetKafkaVO));
        Object data = assetKafkaVO.getData();
        if (null == data) {
            logger.error("消费外部资产消息,data数据为空不处理");
        } else {
            String dataType = assetKafkaVO.getDataType();
            Object datas = assetKafkaVO.getData();
            // 资产数据放到队列中
            if ("asset".equalsIgnoreCase(dataType)) {
                QueUtil.assetRefQuePut(QueUtil.ASSET, datas);
            }
            // 数据信息管理放到队列中
            if ("file".equalsIgnoreCase(dataType)) {
                QueUtil.assetRefQuePut(QueUtil.DATAINFO, datas);
            }
            // 应用系统放到队列中
            if ("app".equalsIgnoreCase(dataType)) {
                QueUtil.assetRefQuePut(QueUtil.APP, datas);
            }
        }
    }

    /**
     * 构造第三方业务代码
     *
     * @param message
     * @return
     */
    private BusinessVO contructBusinessVO(String message) {
        if (StringUtils.isNotEmpty(message)) {
            BusinessVO businessVO = gson.fromJson(message, BusinessVO.class);
            return businessVO;
        } else {
            throw new RuntimeException("receive the third message is null!");
        }
    }
}
