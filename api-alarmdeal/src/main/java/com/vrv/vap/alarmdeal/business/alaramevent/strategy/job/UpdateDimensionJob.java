package com.vrv.vap.alarmdeal.business.alaramevent.strategy.job;

import com.vrv.vap.alarmdeal.frameworks.util.RedissonSingleUtil;
import com.vrv.vap.jpa.common.DateUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author: 梁国露
 * @since: 2023/3/20 14:43
 * @description:
 */
@Configuration
@EnableScheduling
@Component
public class UpdateDimensionJob {
    private Logger logger = LoggerFactory.getLogger(UpdateDimensionJob.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RedissonSingleUtil redissonSingleUtil;

    @Scheduled(cron = "${dimension.update.time}")
    public void updateDimension(){
        logger.warn("定时更新维表数据时间");
        // 获取前一天的时间
        String insertTime = DateUtil.format(DateUtil.addDay(new Date(),-1),DateUtil.Year_Mouth_Day);
        // 获取数据库中包含insert_time字段的维表名称
        List<String> tableNames = getTableNameForInsertTime();
        // 更新时间
        if(CollectionUtils.isNotEmpty(tableNames)){
            for(String tableName : tableNames){
                // 更新数据时间
                updateTime(tableName,insertTime);

                // 删除redis缓存
                redissonSingleUtil.deleteByPrex(tableName);
            }
        }
    }

    /**
     * 更新数据时间
     * @param tableName
     * @param time
     */
    private void updateTime(String tableName,String time){
        String sql = "update {0} set insert_time = {1} where filter_code is not null and rule_code is not null;";
        sql = sql.replace("{0}",tableName).replace("{1}","'"+time+"'");
        jdbcTemplate.execute(sql);
    }

    /**
     * 查询 数据库中有insert_time字段的baseline开头的表
     * @return
     */
    public List<String> getTableNameForInsertTime(){
        List<String> result = new ArrayList<>();
        String sql = "SELECT DISTINCT TABLE_NAME as tableName FROM INFORMATION_SCHEMA.COLUMNS WHERE COLUMN_NAME IN ('insert_time') AND TABLE_SCHEMA='ajb_vap' and TABLE_NAME like '%baseline%';";
        List<Map<String,Object>> tableNames = jdbcTemplate.queryForList(sql);
        for(Map<String,Object> map : tableNames){
            result.add(String.valueOf(map.get("tableName")));
        }
        return result;
    }
}
