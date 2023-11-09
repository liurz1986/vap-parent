package com.vrv.vap.xc.tools;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.xc.mapper.core.BaseAreaMapper;
import com.vrv.vap.xc.model.BaseAreaModel;
import com.vrv.vap.xc.pojo.BaseArea;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.toolkit.tools.AreaTools;

/**
 * 区域转换
 *
 * @author xw
 * @date 2018年5月3日
 */
public final class BaseAreaTools {

    private static final Log log = LogFactory.getLog(BaseAreaTools.class);

    /**
     * 缓存区域信息
     */
    private static List<BaseAreaModel> cache = null;

    /**
     * 周期
     */
    private static final long PERIOD = 10;

    private static ScheduledExecutorService excutor = new ScheduledThreadPoolExecutor(1,
            new BasicThreadFactory.Builder().namingPattern("example-schedule-pool-%d").daemon(true).build());

    public static void start() {
        log.debug("启动一个定时器,每隔" + PERIOD + "分钟同步一次区域信息");
        excutor.scheduleAtFixedRate(BaseAreaTools::init, 5, PERIOD * 60, TimeUnit.SECONDS);
    }

    public static void startOnce() {
        log.debug("缓存区域信息");
        init();
    }

    private static void init() {
        BaseAreaMapper mapper = VapXcApplication.getApplicationContext().getBean(BaseAreaMapper.class);
        /*BaseAreaExample example = new BaseAreaExample();
        example.setOrderByClause("sort");
        example.setMyStart(0);
        example.setMyCount(999);*/
        List<BaseAreaModel> list = new ArrayList<>();

        QueryWrapper<BaseArea> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("sort");
        mapper.selectList(queryWrapper).forEach(areaModel -> {
            BaseAreaModel baseAreaModel = new BaseAreaModel();
            baseAreaModel.setAreaCode(areaModel.getAreaCode());
            baseAreaModel.setAreaCodeSub(areaModel.getAreaCodeSub());
            baseAreaModel.setAreaName(areaModel.getAreaName());
            baseAreaModel.setDescription(areaModel.getDescription());
            baseAreaModel.setId(areaModel.getId());
            baseAreaModel.setIpRange(areaModel.getIpRange());
            baseAreaModel.setParentCode(areaModel.getParentCode());
            baseAreaModel.setSort(areaModel.getSort());
            //baseAreaModel.setAvailable(areaModel.getAvailable());
            if (areaModel.getIpRange() != null && !"".equals(areaModel.getIpRange())) {
                String[] ips = areaModel.getIpRange().split(",");
                List<Long[]> ipNumList = new ArrayList<>(ips.length);
                for (String ipr : ips) {
                    String[] ips2 = ipr.split("-");
                    ipNumList.add(new Long[]{AreaTools.ipToNum(ips2[0]), AreaTools.ipToNum(ips2[1])});
                }
                baseAreaModel.setIpNumList(ipNumList);
            } else {
                baseAreaModel.setIpNumList(new ArrayList<>(0));
            }

            list.add(baseAreaModel);
        });
        cache = list;
    }

    /**
     * 获取区域列表
     *
     * @return
     */
    public static List<BaseAreaModel> getAreaList() {
        if (null == cache) {
            init();
        }
        return cache;
    }

    /**
     * 根据ip获取区域
     *
     * @param ip
     * @return
     */
    public static Optional<BaseAreaModel> getBaseAreaByIp(String ip) {
        return filter(a -> {
            long ipNum = AreaTools.ipToNum(ip);
            for (Long[] ips : a.getIpNumList()) {
                if (AreaTools.isInRange(ipNum, ips[0], ips[1])) {
                    return true;
                }
            }
            return false;
        });
    }

    /**
     * 根据区域编码获取区域
     *
     * @param code
     * @return
     */
    public static Optional<BaseAreaModel> getBaseAreaByCode(String code) {
        return filter(a -> a.getAreaCode().trim().equals(code));
    }

    /**
     * 根据截断区域编码获取区域
     *
     * @param code
     * @return
     */
    public static Optional<BaseAreaModel> getBaseAreaByCodeSub(String code) {
        return filter(a -> a.getAreaCodeSub().trim().equals(code));
    }

    /**
     * 根据区域名称获取区域
     *
     * @param name
     * @return
     */
    public static Optional<BaseAreaModel> getBaseAreaByName(String name) {
        return filter(a -> a.getAreaName().trim().equals(name.trim()));
    }

    /**
     * 区域过滤
     *
     * @param predicate
     * @return
     */
    private static Optional<BaseAreaModel> filter(Predicate<BaseAreaModel> predicate) {
        if (null == cache) {
            init();
        }
        return cache.stream().filter(predicate).findFirst();
    }
}
