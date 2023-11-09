package com.vrv.vap.admin.web;

import com.github.pagehelper.Page;
import com.vrv.vap.admin.model.AlarmItem;
import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.admin.service.AlarmCollectionService;
import com.vrv.vap.admin.service.AlarmItemGroupService;
import com.vrv.vap.admin.vo.AlarmItemGroupVO;
import com.vrv.vap.admin.vo.AlarmItemVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
* @BelongsPackage com.vrv.vap.admin.web
* @Author CodeGenerator
* @CreateTime 2021/08/04
* @Description (AlarmItemCollection相关接口)
* @Version
*/
@RestController
@Api(value = "告警数据汇聚")
@RequestMapping("/alarm/item")
public class AlarmCollectionController extends ApiController {

    @Autowired
    private AlarmCollectionService alarmCollectionService;

    @Autowired
    private AlarmItemGroupService alarmItemGroupService;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("alarmType","{\"501\":\"操作系统告警\",\"502\":\"数据库监控告警\",\"503\":\"中间件告警\",\"504\":\"接收模块告警\",\"505\":\"分析模块告警\"," +
                "\"506\":\"管理平台告警\",\"507\":\"流量采集器告警\",\"508\":\"关键服务守护告警\",\"509\":\"日志解析失败告警\",\"510\":\"ES监控告警\"}");
        transferMap.put("alarmLevel","{\"1\":\"低\",\"2\":\"中\",\"3\":\"高\"}");
        transferMap.put("alarmStatus", "{\"0\":\"未处理\",\"1\":\"已处理\",\"2\":\"已忽略\"}");
    }

    /**
    * 获取所有数据--AlarmItemCollection
    */
    @ApiOperation(value = "获取所有告警数据")
    @GetMapping
    public VData< List<AlarmItem>> getAllAlarmItems() {
        List<AlarmItem> list = alarmCollectionService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加告警数据")
    @PutMapping
    @SysRequestLog(description="添加告警数据", actionType = ActionType.ADD)
    public Result addAlarmItem(@RequestBody AlarmItem alarmItem) {
        AlarmItemGroup alarmItemGroup = new AlarmItemGroup();
        BeanUtils.copyProperties(alarmItem,alarmItemGroup);
        AlarmItemGroup group = alarmItemGroupService.findOne(alarmItemGroup);
        if (group == null) {
            alarmItemGroupService.save(group);
        }
        int result = alarmCollectionService.save(alarmItem);
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改告警数据", hidden = false)
    @PatchMapping
    @SysRequestLog(description="修改告警数据", actionType = ActionType.UPDATE)
    public Result updateAlarmItem(@RequestBody AlarmItem alarmItem) {
        AlarmItem alarmItemSec = alarmCollectionService.findById(alarmItem.getId());
        int result = alarmCollectionService.update(alarmItem);
        if (result == 1) {
            alarmCollectionService.deleteDealedGroup(alarmItem);
            SyslogSenderUtils.sendUpdateAndTransferredField(alarmItemSec,alarmItem,"修改告警数据",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除告警数据")
    @DeleteMapping
    @SysRequestLog(description="删除告警数据", actionType = ActionType.DELETE)
    public Result delAlarmItem(@RequestBody DeleteQuery deleteQuery) {
        List<AlarmItem> alarmItemList = alarmCollectionService.findByids(deleteQuery.getIds());
        int result = alarmCollectionService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            alarmItemList.forEach(alarmItem -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(alarmItem,"删除告警数据",transferMap);
            });
        }
        return this.result(result == 1);
    }

    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询告警数据（分页）")
    @PostMapping
    @SysRequestLog(description="查询告警数据", actionType = ActionType.SELECT)
    public VList<AlarmItem> queryAlarmItems(@RequestBody AlarmItemGroupVO alarmItemGroupVO) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(alarmItemGroupVO,"查询告警数据",transferMap);
        alarmItemGroupVO.setOrder_("alarmTime");
        alarmItemGroupVO.setBy_("desc");
        Example example = this.pageQuery(alarmItemGroupVO, AlarmItem.class);
        Example.Criteria criteria;
        if( example.getOredCriteria().size()>0){
            criteria = example.getOredCriteria().get(0);
        }else {
            criteria = example.createCriteria();
        }

        if (StringUtils.isEmpty(alarmItemGroupVO.getIds())) {
            if (alarmItemGroupVO.getUpdateStatus() != null) {
                criteria.andEqualTo("alarmStatus", alarmItemGroupVO.getUpdateStatus());
            }
            return this.vList(alarmCollectionService.findByExample(example));
        }

        List<Integer> idList = Arrays.stream(alarmItemGroupVO.getIds().split(",")).map(p -> Integer.parseInt(p)).collect(Collectors.toList());
        criteria.andIn("id", idList);
        List<AlarmItem> alarmItems = alarmCollectionService.findByExample(example);
        return this.vList(alarmItems);
    }

    /**
     * 查询（分页）
     */
    @ApiOperation(value = "查询告警数据（分页）")
    @PostMapping("/pages")
    @SysRequestLog(description="查询告警数据", actionType = ActionType.SELECT)
    public VList<AlarmItem> queryAlarmPages(@RequestBody AlarmItemVO alarmItemVO) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(alarmItemVO,"查询告警数据",transferMap);
        alarmItemVO.setOrder_("alarmTime");
        alarmItemVO.setBy_("desc");
        Example example = this.pageQuery(alarmItemVO, AlarmItem.class);
        List<AlarmItem> alarmItems = alarmCollectionService.findByExample(example);
        return this.vList(alarmItems);
    }


    /**
     * 分组查询告警数据
     **/
    @ApiOperation(value = "分组查询告警数据")
    @PostMapping("/group")
    @SysRequestLog(description="查询分组告警数据", actionType = ActionType.SELECT)
    public VList<AlarmItemGroup> getAlarmItemGroups(@RequestBody AlarmItemVO alarmItemVO) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(alarmItemVO,"查询分组告警数据",transferMap);
        AlarmItemVO alarmVo = new AlarmItemVO();
        BeanUtils.copyProperties(alarmItemVO,alarmVo);
        return this.vList(alarmCollectionService.getAlarmItemsByGroup(alarmVo));
    }

    /**
     * 批量修改
     **/
    @ApiOperation(value = "批量修改告警数据")
    @PatchMapping("/batch/update")
    @SysRequestLog(description="批量修改告警数据", actionType = ActionType.UPDATE,manually = false)
    public Result updateAlarmItems(@RequestBody List<AlarmItemGroupVO> alarmItemGroupVOList) {
        if (CollectionUtils.isNotEmpty(alarmItemGroupVOList)) {
            for (AlarmItemGroupVO alarmItemGroupVO : alarmItemGroupVOList) {
                AlarmItemGroupVO alarmVo = new AlarmItemGroupVO();
                BeanUtils.copyProperties(alarmItemGroupVO,alarmVo);
                alarmCollectionService.updateAlarmItems(alarmVo);
            }
        }
        return this.result(true);
    }

    /**
     * 批量修改
     **/
    @ApiOperation(value = "查询历史处理数据")
    @PostMapping("/get/history")
    @SysRequestLog(description="查询历史处理数据", actionType = ActionType.SELECT)
    public VList<AlarmItem> getHistory(@RequestBody Query query) {
        Example example = this.pageQuery(query, AlarmItem.class);
        Example.Criteria criteria;
        if( example.getOredCriteria().size()>0){
            criteria = example.getOredCriteria().get(0);
        }else {
            criteria = example.createCriteria();
        }
        criteria.andNotEqualTo("alarmStatus", 0);
        List<AlarmItem> alarmItems = alarmCollectionService.findByExample(example);
        return this.vList(alarmItems);
    }

    /**
     * 告警趋势
     **/
    @ApiOperation(value = "告警趋势")
    @PostMapping("/trend")
    public Result getAlarmTrend(@RequestBody AlarmItemVO alarmItemVO) {
        AlarmItemVO alarmVo = new AlarmItemVO();
        BeanUtils.copyProperties(alarmItemVO,alarmVo);
        List<Map> mapList = alarmCollectionService.getAlarmTrend(alarmVo);
        if (CollectionUtils.isNotEmpty(mapList)) {
            return this.vList(mapList,mapList.size());
        }
        return this.result(false);
    }

    /**
     * 查询当日告警数量
     **/
    @ApiOperation(value = "查询当日告警数量")
    @GetMapping("/today")
    public VData getAlarmsToday() {
        AlarmItemVO alarmItemVO = new AlarmItemVO();
        alarmItemVO.setStartTime(new SimpleDateFormat("yyyy-MM-dd 00:00:00").format(new Date()));
        Example example = this.pageQuery(alarmItemVO, AlarmItem.class);
        return this.vData(((Page<AlarmItem>)alarmCollectionService.findByExample(example)).getTotal());
    }

    /**
     * 查询告警数量
     **/
    @ApiOperation(value = "查询告警数量")
    @GetMapping("/countByStatus")
    public VData getAlarmsCount() {
        Map<String, Object> resultMap = new HashMap<>();
        List<AlarmItem> alarmItemList = alarmCollectionService.findAll();
        if (CollectionUtils.isEmpty(alarmItemList)) {
            resultMap.put("totalCount", 0);
            resultMap.put("dealCount", 0);
            resultMap.put("NotDealCount", 0);
        } else {
            Map<Integer, List<AlarmItem>> alarmItemMap = alarmItemList.stream().collect(Collectors.groupingBy(AlarmItem::getAlarmStatus));
            resultMap.put("totalCount", alarmItemList.size());
            resultMap.put("dealCount", alarmItemMap.get(1) == null ? 0 : alarmItemMap.get(1).size());
            resultMap.put("NotDealCount", alarmItemMap.get(0) == null ? 0 : alarmItemMap.get(0).size());
        }

        return this.vData(resultMap);
    }
}