package com.vrv.vap.netflow.common.batch;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.flumeavrostarter.sender.FlumeDataSender;
import com.vrv.vap.netflow.common.config.BatchQueueProperties;
import com.vrv.vap.netflow.common.config.TypeDictionaryProperties;
import com.vrv.vap.netflow.common.enums.NetFlowLogTypeEnum;
import com.vrv.vap.netflow.common.util.FileUtils;
import com.vrv.vap.netflow.common.util.Uuid;
import com.vrv.vap.netflow.service.NetFlowFieldAnalysisService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;

@Service
public class FileConsumer implements Consumer<List<Map<String, Object>>>, InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(FileConsumer.class);
    private static final ThreadFactory namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("consumer-pool-%d").build();
    private static final ExecutorService executorService = new ThreadPoolExecutor(100, 2048,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<Runnable>(2048), namedThreadFactory, new ThreadPoolExecutor.AbortPolicy());
    @Resource
    private FlumeDataSender flumeDataSender;
    @Resource
    private NetFlowFieldAnalysisService netFlowFieldAnalysisService;
    @Resource
    private TypeDictionaryProperties typeDictionaryProperties;
    @Resource
    private BatchQueueProperties batchQueueProperties;
    private static final Integer DATA_TYPE_FILE = 9;
    private Map<String, String> logTypeDic;

    @Override
    public void afterPropertiesSet() throws Exception {
        logTypeDic = typeDictionaryProperties.getLogTypeDic();
    }

    /**
     * 新增元素到集合中
     *
     * @param monitorField 监视器中的字段定义
     * @param flumeField   flume中的字段名称定义
     * @param monitorMap   监视器中的map
     */
    private void increaseElement(String monitorField, String flumeField, Map monitorMap) {
        if (StringUtils.isEmpty(monitorField)) {
            throw new RuntimeException(String.format("monitorField为空, %s", monitorField));
        }
        if (StringUtils.isEmpty(flumeField)) {
            throw new RuntimeException(String.format("flumeField为空, %s", flumeField));
        }
        if (monitorMap.containsKey(monitorField)) {
            monitorMap.put(flumeField, monitorMap.get(monitorField));
        }
    }

    @Override
    public void accept(List<Map<String, Object>> mlist) {
        logger.info(String.format("accept开始时间：%s, 消费数量：%s", DateUtils.dateTimeNow() , mlist.size()));
        if (logTypeDic.isEmpty()) {
            logTypeDic = typeDictionaryProperties.getLogTypeDic();
        }
        Assert.notNull(mlist, "accept list不能为空");
        ListIterator<Map<String, Object>> iterator = mlist.listIterator();
        while (iterator.hasNext()) {
            Map<String, Object> sourceMap = iterator.next();
            increaseElement("time", "event_time", sourceMap);
            Integer logType = (Integer) (sourceMap.get("log_type"));
            if (logTypeDic.containsKey(logType.toString())) {
                increaseElement("log_type", "report_log_type", sourceMap);
            }
            sourceMap.put("guid", Uuid.uuid());
            // session_id处理
            if (NetFlowLogTypeEnum.HTTP_PROTOCOL_TYPE.getType().equals(logType)) {
                netFlowFieldAnalysisService.handleSessionId(sourceMap);
            }
            // 文件传输增加file_hash
            if (DATA_TYPE_FILE.equals(logType) && sourceMap.containsKey("file_md5")) {
                sourceMap.put("file_hash", sourceMap.get("file_md5"));
            }
            try {
                netFlowFieldAnalysisService.swapFieldValues(sourceMap);
                netFlowFieldAnalysisService.handlerOrg(sourceMap);
                netFlowFieldAnalysisService.handlerDev(sourceMap);
                netFlowFieldAnalysisService.handlerPerson(sourceMap);
                //http和ssl 3，7，99
                netFlowFieldAnalysisService.handlerApp(sourceMap);
            } catch (Exception exception) {
                String jsonString = JSONObject.toJSONString(sourceMap);
                logger.error(String.format("accept解析失败:%s", jsonString), exception);
            }
        }
        // 提交avro数据到flume中
//        List<Map<String, Object>> dstList = CollectionUtil.newCopyOnWriteArrayList(mlist);
        executorService.execute(new SendFlumeDataThread(mlist));
        logger.info(String.format("accept结束时间：%s, 消费数量：%s", DateUtils.dateTimeNow() , mlist.size()));
    }

    /**
     * 发送数据到flume中，flume进行数据解析和存储操作
     */
    private class SendFlumeDataThread implements Runnable {
        private List<Map<String, Object>> logList;

        public SendFlumeDataThread(List<Map<String, Object>> logList) {
            this.logList = logList;
        }

        @Override
        public void run() {
            try {
                flumeDataSender.sendBatch(logList);
            } catch (Exception channelException) {
                // TODO 发送失败的需要做补偿，可以写入文件再次发送,作为容错机制处理. liujinhui
                String jsonString = JSON.toJSONString(logList);
                String uuid = Uuid.uuid();
                String fileName = uuid + "." + batchQueueProperties.getFileSuffix();
                String filePath = batchQueueProperties.getFileFolder() + File.separator + fileName;
                FileUtils.writeFile(jsonString, filePath);
                logger.error("时间毫秒：{}, flume发送失败后，本地文件数据存储文件名称：{}", System.currentTimeMillis(), filePath);
                logger.error("发送flume的channel异常，异常信息:", channelException);
            } finally {
                if(logList != null && !logList.isEmpty()) {
                    logList.clear();
                }
            }
        }
    }
}
