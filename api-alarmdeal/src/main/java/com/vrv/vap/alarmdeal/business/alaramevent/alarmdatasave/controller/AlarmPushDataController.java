package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.controller;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.StartFlinkTaskTestReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Column;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Tables;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSourceField;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.alarmdeal.frameworks.util.ShellExecuteScript;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年02月21日 14:19
 */
@RestController
@RequestMapping("/alarmpush")
public class AlarmPushDataController {
    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private DataSourceFegin dataSourceFegin;
    
    @Autowired
    private RedisUtil redisUtil;

    @RequestMapping(value = "/startFlinkTask",method = RequestMethod.POST)
    public Result<Boolean> startFlinkTask(@RequestBody StartFlinkTaskTestReq param){
        String id = param.getId();

        Map<String,List<String>> map = new HashMap<>();
        for(String ruleId : param.getRiskEventIds()){
            List<String> filterCodes = filterOperatorService.getStartFilterByRuleId(ruleId,false);
            if(CollectionUtils.isNotEmpty(filterCodes)){
                map.put(ruleId,filterCodes);
            }
        }
        String riskEventIds = String.join(",", param.getRiskEventIds());
        String type = param.getType();
        if(!map.isEmpty()){
            filterOperatorService.startOperatorJobGroup(id,map,type);
        }
        return ResultUtil.success(true);
    }

    @RequestMapping(value = "/queryFlinkMapData",method = RequestMethod.GET)
    public Result<Map<String, List<String>>> queryFlinkMapData(){
        Map<String, List<String>> result = new HashMap<>();
        ResultObjVO<List<DataSource>> dataSourceResult = dataSourceFegin.getSource();
        List<DataSource> dataSources = dataSourceResult.getData();
        Map<String, List<String>> flinkTaskMap = filterOperatorService.getFlinkTaskMap();
        for(Map.Entry<String,List<String>> entry : flinkTaskMap.entrySet()){
            String source = entry.getKey();
            String[] sourceArr = source.split("_");
            String sourceId = sourceArr[0];
            DataSource dataSource = dataSources.stream().filter(item->String.valueOf(item.getId()).equals(sourceId)).collect(Collectors.toList()).get(0);
            result.put(dataSource.getTitle().concat("(").concat(source).concat(")"),entry.getValue());
        }
        return ResultUtil.success(result);
    }

    @RequestMapping(value = "/queryFlinkMapData1",method = RequestMethod.GET)
    public Result<Map<String, List<String>>> queryFlinkMapData1(){
        Map<String, List<String>> flinkTaskMap = filterOperatorService.getFlinkTaskMap();
        return ResultUtil.success(flinkTaskMap);
    }

    @RequestMapping(value = "/clearFlinkMapData",method = RequestMethod.GET)
    public Result<Boolean> clearFlinkMapData(){
        Map<String, List<String>> flinkTaskMap = filterOperatorService.getFlinkTaskMap();
        flinkTaskMap.clear();
        return ResultUtil.success(true);
    }

    @RequestMapping(value = "/kill",method = RequestMethod.GET)
    public void kill(){
        List<String> pids = getFlinkMainPids();
        if(CollectionUtils.isNotEmpty(pids)){
            killPid(pids);
        }
    }

    /**
     * 查询 flink启动 main函数 pid
     * @return
     */
    public List<String> getFlinkMainPids(){
        String[] shell = {"bash","-c","ps -x | grep com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction | perl -nle 'print $1 if /^ *([0-9]+)/'"};
        List<String> contents = ShellExecuteScript.executeShellArrayByResult(shell);
        return contents;
    }

    /**
     * kill 调服务
     * @param pids
     */
    public void killPid(List<String> pids){
        String pidStr = String.join(" ",pids);
        String[] shell = {"bash","-c","kill -9 "+pidStr};
        ShellExecuteScript.executeShellByResultArray(shell);
    }

    @GetMapping("/getFilterEvent")
    public void getFilterEvent(){
        Gson gson = new Gson();
        List<DataSource> dataSources = dataSourceFegin.getSource().getData();
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("deleteFlag",true));
        List<FilterOperator> filterOperatorList = filterOperatorService.findAll();
        filterOperatorList.stream().forEach(item->{
            // 增加topicname
            String filterConfig = item.getFilterConfig();
            FilterConfigObject filterConfigObject = gson.fromJson(filterConfig,FilterConfigObject.class);
            Tables[][] tables = filterConfigObject.getTables();
            List<String> sourceIds = new ArrayList<>();
            for(int i=0;i<tables.length;i++){
                for(int j =0;j<tables[i].length;j++){
                    Tables tables2 = tables[i][j];
                    String name = tables2.getName();
                    String topicName = getTopicNameByName(dataSources,name);
                    String sourceId = getSourceIdByName(dataSources,name);
                    if(StringUtils.isBlank(topicName) && StringUtils.isNotBlank(name)){
                        topicName = getTopicNameByName(dataSources,name.substring(0,name.length()-1));
                        sourceId = getSourceIdByName(dataSources,name.substring(0,name.length()-1));
                    }
                    if(StringUtils.isNotBlank(topicName)){
                        tables2.setTopicName(topicName);
                    }
                    if(StringUtils.isNotBlank(sourceId)){
                        sourceIds.add(sourceId);
                        tables2.setEventTableId(sourceId);
                    }

                    List<Column> columnList = tables2.getColumn();
                    if(StringUtils.isNotBlank(sourceId)){
                        String finalSourceId = sourceId;
                        columnList.stream().forEach(column -> {
                            String id = getColumeIdByName(column.getName(), finalSourceId);
                            if(StringUtils.isNotBlank(id)){
                                column.setId(id);
                            }
                        });
                        Collections.sort(columnList, Comparator.comparing(Column::getOrder));
                        tables2.setColumn(columnList);
                    }
                }
            }
            filterConfigObject.setTables(tables);
            item.setFilterConfig(gson.toJson(filterConfigObject));
            sourceIds = sourceIds.stream().distinct().collect(Collectors.toList());
            // 修改数据源id
            if(CollectionUtils.isNotEmpty(sourceIds)){
                item.setSourceIds(gson.toJson(sourceIds));
            }
            filterOperatorService.save(item);
        });
    }

    public String getTopicNameByName(List<DataSource> dataSources,String name){
        if(StringUtils.isBlank(name)){
            return null;
        }
        List<DataSource> dataSources1 = dataSources.stream().filter(item->name.equals(item.getTopicAlias())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dataSources1)){
            DataSource dataSource = dataSources1.get(0);
            return dataSource.getTopicName();
        }
        return null;
    }

    public String getSourceIdByName(List<DataSource> dataSources,String name){
        if(StringUtils.isBlank(name)){
            return null;
        }
        List<DataSource> dataSources1 = dataSources.stream().filter(item->name.equals(item.getTopicAlias())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dataSources1)){
            DataSource dataSource = dataSources1.get(0);
            return String.valueOf(dataSource.getId());
        }
        return null;
    }

    public String getColumeIdByName(String name,String sourceId){
        List<DataSourceField> dataSourceFields = dataSourceFegin.getFieldBySourceId(sourceId).getData();
        List<DataSourceField> dataSources1 = dataSourceFields.stream().filter(item->name.equals(item.getField())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dataSources1)){
            DataSourceField dataSource = dataSources1.get(0);
            return String.valueOf(dataSource.getId());
        }
        return null;
    }

}
