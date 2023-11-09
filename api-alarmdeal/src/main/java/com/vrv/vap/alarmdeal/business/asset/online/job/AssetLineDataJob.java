package com.vrv.vap.alarmdeal.business.asset.online.job;
import com.vrv.vap.alarmdeal.business.asset.online.service.AssetOnLineSynchService;
import com.vrv.vap.alarmdeal.business.asset.online.util.OnLineQueUtil;
import com.vrv.vap.alarmdeal.business.asset.online.vo.AssetOnLineVO;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 定时获取资产在线数据
 *
 * 2022 - 08
 */
@Component
@Order(value = 14)
public class AssetLineDataJob implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(AssetLineDataJob.class);

    @Autowired
    private AssetOnLineSynchService assetOnLineSynchService;


    private List<AssetOnLineVO> assetOnLineVOs = new ArrayList<>();
    /**
     * 开始时间
     */
    private Date startTime = new Date();

    @Override
    public void run(String... args) throws Exception {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // 死循环，一直处理
//                while (true) {
//                    try {
//                        AssetOnLineVO data = OnLineQueUtil.assetOnlineTake(); //获取队列数据
//                        if(null != data){
//                            assetOnLineVOs.add(data);
//                        }
//                        // 每500条处理一次或者10s处理一次(首次没有到500条10秒处理一次、或处理完后10秒钟没有处理执行处理)
//                        boolean isFiveMin = getTimeResult();
//                        if (isFiveMin || assetOnLineVOs.size() >= 500){
//                            if (CollectionUtils.isNotEmpty(assetOnLineVOs)) {
//                                assetOnLineSynchService.excSynchData(assetOnLineVOs);
//                                startTime = new Date();
//                                assetOnLineVOs.clear();
//                            }
//                        }
//                    } catch (Exception e) {
//                        logger.error("资产在线数据处理异常", e);
//                    }
//                }
//            }
//        }).start();
    }
    /**
     * 获得时间
     *
     * @return
     */
    private boolean getTimeResult() {
        Date endTime = new Date();
        long timeSpan = (endTime.getTime() - startTime.getTime()) / 1000;
        if (timeSpan > 10) {
            startTime = new Date();
            return true;
        } else {
            return false;
        }
    }
}