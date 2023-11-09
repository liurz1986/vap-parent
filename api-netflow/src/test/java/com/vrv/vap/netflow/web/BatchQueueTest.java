package com.vrv.vap.netflow.web;


import com.vrv.vap.netflow.common.config.BatchQueueProperties;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wh1107066
 * @date 2023/8/17
 */
public class BatchQueueTest {

    public void addTest() {
        BatchQueueProperties batchQueueProperties = new BatchQueueProperties();
        batchQueueProperties.setBatchCapability(100);
        batchQueueProperties.setMaxQueueSize(3000);
        batchQueueProperties.setBatchTime(60000);

        Map<String, Object> map = new HashMap<>();

    }

    /**
     * Redis中的中文乱码转换
     * <p>
     * cd $VAP_WORK_DIR/redis/src
     * ./redis-cli -a 'vrv@1234' -p 6379 -h 192.168.120.152
     * 192.168.120.152:6379> keys _BASEINFO*
     * 1) "_BASEINFO:BASE_DICT_ALL:ALL"
     * 192.168.120.152:6379> type _BASEINFO:ASSET:IP
     * Hash
     * get _BASEINFO:ASSET:ALL
     *
     * @throws Exception
     */
    @Test
    public void asciiTest() throws Exception {

        String content = "xe5\\x85\\xb6\\xe4\\xbb\\x96\\xe8\\xbf\\x90\\xe7\\xbb\\xb4\\xe7\\xbb\\x88\\xe7\\xab\\xaf";
        //通常乱码的情况是 两个不兼容的编码相互转换
        byte[] utf8Bytes = content.getBytes("utf-8");
        String utf8String = new String(utf8Bytes, "utf-8");
        System.out.println(utf8String);


    }
}