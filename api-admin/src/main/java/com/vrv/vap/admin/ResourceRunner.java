package com.vrv.vap.admin;

import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.mapper.ServiceApiMapper;
import com.vrv.vap.admin.mapper.SystemConfig2Mapper;
import com.vrv.vap.admin.model.ServiceApiData;
import com.vrv.vap.admin.model.SystemConfig2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

@Component
public class ResourceRunner implements CommandLineRunner {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ServiceApiMapper serviceApiMapper;

    @Resource
    private SystemConfig2Mapper systemConfig2Mapper;

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static final String API_ALL_KEY = "_API_ALL";
    private static final String AUTH_STRATEGY_KEY = "_AUTH_STRATEGY";

    // 垂直越权开关
    private static final String AUTHORITY_EXCEED_STRATEGY_SWITCH = "authority_exceed_strategy_switch";
    // 未维护接口开关
    private static final String AUTHORITY_EXCEED_STRATEGY_UNKNOWN = "authority_exceed_strategy_unknown";
    // 白名单列表
    private static final String AUTHORITY_EXCEED_STRATEGY_WHITELIST = "authority_exceed_strategy_whitelist";

    @Override
    public void run(String... args) throws Exception {
        List<ServiceApiData> resultList = getServiceApiInfo();
        if (CollectionUtils.isNotEmpty(resultList)) {
            String result = gson.toJson(resultList);
            stringRedisTemplate.opsForValue().set(API_ALL_KEY, result);
            initStrategyConfig();
        }
    }

    /**
     * 获取所有模块接口URI地址
     *
     * @return List
     */
    public List<ServiceApiData> getServiceApiInfo() {
        return serviceApiMapper.getServiceApiInfo();
    }

    public void initStrategyConfig() {
        Example example = new Example(SystemConfig2.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("confId", Arrays.asList(AUTHORITY_EXCEED_STRATEGY_SWITCH, AUTHORITY_EXCEED_STRATEGY_UNKNOWN, AUTHORITY_EXCEED_STRATEGY_WHITELIST));
        List<SystemConfig2> systemConfig2s = systemConfig2Mapper.selectByExample(example);
        Map<String, Object> switchMap = new HashMap<>();
        List<String> whiteList = new ArrayList<>();
        for (SystemConfig2 systemConfig2 : systemConfig2s) {
            String confId = systemConfig2.getConfId();
            String confValue = systemConfig2.getConfValue();
            String[] split = confValue.split(",");
            if (split.length > 1) {
                whiteList.addAll(Arrays.asList(split));
                switchMap.put(confId, whiteList);
            } else {
                switchMap.put(confId, Integer.parseInt(confValue) != 0);
            }
        }
        String switchJson = gson.toJson(switchMap);
        stringRedisTemplate.opsForValue().set(AUTH_STRATEGY_KEY, switchJson);
    }
}
