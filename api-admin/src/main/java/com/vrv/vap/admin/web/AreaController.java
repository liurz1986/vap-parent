package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.condition.VapOrZhyCondition;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.YmlUtils;
import com.vrv.vap.admin.model.BaseArea;
import com.vrv.vap.admin.model.BaseAreaIpSegment;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.service.BaseAreaIpSegmentService;
import com.vrv.vap.admin.service.BaseAreaService;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.vo.AreaUserQuery;
import com.vrv.vap.admin.vo.BaseAreaIpSegmentVo;
import com.vrv.vap.admin.vo.BaseAreaVo;
import com.vrv.vap.admin.vo.IpRangeVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping(path = "/area")
@Conditional(VapOrZhyCondition.class)
public class AreaController extends ApiController {
    private static final String AREA_CODE = "areaCode";
    private static Logger logger = LoggerFactory.getLogger(AreaController.class);

    @Autowired
    private BaseAreaService baseAreaService;
    @Autowired
    private BaseAreaIpSegmentService baseAreaIpSegmentService;
    @Autowired
    UserService userService;


    @Value("${collector.configPath}")
    private String collectorConfigPath;


    /**
     * 获取第一层级区域
     */
    @ApiOperation(value = "获取第一层级区域")
    @GetMapping
    public VData<List<BaseArea>> getAllArea() {
        return this.vData( baseAreaService.findSubAreaByCode(null));
    }

    /**
     * 获取所有地区
     * @return
     */
    @ApiOperation(value = "获取所有地区")
    @GetMapping(value = "/all")
    public VData<List<BaseArea>> queryAllArea() {
        return this.vData(baseAreaService.findAll());
    }


    /**
     * 获取区域下级
     */
    @ApiOperation(value = "获取区域下级")
    @GetMapping(value ="/{areaCode}")
    public VData<List<BaseArea>> getSubArea(@PathVariable(AREA_CODE) String areaCode) {

        return this.vData(baseAreaService.findSubAreaByCode(areaCode));
    }




    /**
     * 条件查询地区
     * 支持分页查询、条件查询 、任意字段排序
     */
    @ApiOperation(value = "条件查询地区")
    @PostMapping
    @SysRequestLog(description = "条件查询地区", actionType = ActionType.SELECT)
    public VList<BaseArea> queryAreas(@RequestBody BaseAreaVo baseAreaVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pagination(baseAreaVo, BaseArea.class);
        return this.vList(baseAreaService.findByExample(example));
    }

    /**
     * 添加地区
     */
    @ApiOperation(value = "添加地区")
    @PutMapping
    @SysRequestLog(description = "添加地区", actionType = ActionType.ADD)
    public Result add(@RequestBody BaseArea baseArea){
        int result = baseAreaService.save(baseArea);
        if(result == 1){
            SyslogSenderUtils.sendAddSyslog(baseArea, "添加地区");
            return this.vData(baseArea);
        }
        return this.result( false);
    }

    /**
     * 修改地区
     */
    @ApiOperation(value = "修改地区")
    @PatchMapping
    @SysRequestLog(description = "修改地区", actionType = ActionType.UPDATE)
    public Result edit(@RequestBody BaseArea baseArea){
        BaseArea baseAreaSrc = baseAreaService.findById(baseArea.getId().intValue());
        int result = baseAreaService.updateSelective(baseArea);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(baseAreaSrc, baseArea,"修改地区");
        }
        return this.result(result == 1);
    }

    /**
     * 条件查询地区IP范围
     */
    @ApiOperation(value = "条件查询地区IP范围")
    @PostMapping(path = "ip/byAreaCode")
    @SysRequestLog(description = "条件查询地区IP范围", actionType = ActionType.SELECT)
    public  VData<List<IpRangeVO>> queryAreaIps(@RequestBody IpRangeVO ipRangeVO) {
        SyslogSenderUtils.sendSelectSyslog();
        BaseAreaIpSegmentVo baseAreaIpSegmentVo = new BaseAreaIpSegmentVo();
        baseAreaIpSegmentVo.setAreaCode(ipRangeVO.getAreaCode());
        Example example = new Example(BaseAreaIpSegment.class);
        example.createCriteria().andEqualTo(AREA_CODE,baseAreaIpSegmentVo.getAreaCode());
        List<BaseAreaIpSegment> baseAreaIpSegments = baseAreaIpSegmentService.findByExample(example);
        List<IpRangeVO> resultList = new ArrayList<>();
        for(BaseAreaIpSegment temp :baseAreaIpSegments){
            IpRangeVO result = new IpRangeVO();
            result.setOrgCode(ipRangeVO.getAreaCode());
            result.setStartIpValue(temp.getStartIpNum());
            result.setEndIpValue(temp.getEndIpNum());
            resultList.add(result);
        }
        return this.vData(resultList);
    }

    /**
     * 根据IP获取区域名称
     */
    @ApiOperation(value = "根据IP获取区域名称")
    @GetMapping(value = "/areaNameByIp/{ip:.+}")
    public VData<BaseArea> getAreaName(@PathVariable("ip") String ip) {
        BaseAreaIpSegment areaIpSegment = baseAreaIpSegmentService.findByIp(ip);
        if (areaIpSegment == null) {
            return this.vData(ErrorCode.AREA_NAME_NOT_FIND);
        }
        List<BaseArea> areas = baseAreaService.findByProperty(BaseArea.class, AREA_CODE, areaIpSegment.getAreaCode());
        if (areas != null && areas.size() > 0) {
            return this.vData(areas.get(0));
        }
        return this.vData(ErrorCode.AREA_NAME_NOT_FIND);
    }
    
    
    /**
     * 根据IP获取区域名称
     */
    @ApiOperation(value = "根据IP获取区域名称(POST)")
    @PostMapping(value = "/areaByIp")
    public VData<BaseArea> areaNameByIp(@RequestBody Map<String,Object> map) {
    	String ip = null;
    	Object ipObj = map.get("ip");
    	if(ipObj!=null){
    		ip = ipObj.toString();
    	}
        BaseAreaIpSegment areaIpSegment = baseAreaIpSegmentService.findByIp(ip);
        if (areaIpSegment == null) {
            return this.vData(ErrorCode.AREA_NAME_NOT_FIND);
        }
        List<BaseArea> areas = baseAreaService.findByProperty(BaseArea.class, AREA_CODE, areaIpSegment.getAreaCode());
        if (areas != null && areas.size() > 0) {
            return this.vData(areas.get(0));
        }
        return this.vData(ErrorCode.AREA_NAME_NOT_FIND);
    }
    


    /**
     *  省厅:
     *     area-code: 510001
     *     ip-ranges: 10.64.0.0-10.64.255.255,10.71.0.0-10.71.255.255
     */
    @ApiOperation(value = "同步至采集器")
    @GetMapping(value = "/sync")
    public Result synCollector(){
        List<BaseArea> resultArea = baseAreaService.findAll();
        List<BaseAreaIpSegment> resultSegment = baseAreaIpSegmentService.findAll();
        if(resultArea == null||resultArea.size() == 0) {
            return this.vData(ErrorCode.AREA_IS_NULL);
        }
        if(resultSegment == null||resultSegment.size() == 0) {
            return this.vData(ErrorCode.SEGMENT_IP_IS_NULL);
        }
        Map<String,Object> configMap = new LinkedHashMap<>();
        for(BaseArea baseArea : resultArea){
            String code = baseArea.getAreaCode();
            String name = baseArea.getAreaName();
            Optional<String> ipOption = resultSegment.stream().filter(p->code.endsWith(p.getAreaCode())).map(p->p.getStartIp()+"-"+p.getEndIp()).reduce((a,b) -> a+"," +b);
            String ips = "";
            if(ipOption.isPresent()){
                ips = ipOption.get();
            }
            if(StringUtils.isEmpty(ips)) {
                continue;
            }

            Map<String,Object> ipMap = new LinkedHashMap<>();
            ipMap.put("area-code",Long.parseLong(code));
            ipMap.put("ip-ranges",ips);
            configMap.put(name,ipMap);
        }

        try {
            logger.info("====同步至采集器路径地址==="+collectorConfigPath);
            File myPath = new File(collectorConfigPath);
            if ( !myPath.exists()){//
                myPath.mkdirs();
                System.out.println("创建文件夹路径为："+collectorConfigPath);
            }
            //collectorConfigPath = collectorConfigPath+ "/area.yml";
            String path = collectorConfigPath+ "/area.yml";
            YmlUtils.addMapIntoYml(new File(path),configMap);
        } catch (IOException e) {
            logger.error("",e);
        }

        return this.result(true);
    }



    @ApiOperation(value = "查询安全域数量")
    @GetMapping(value = "/count")
    public Result calcCount(){
      Integer count =   baseAreaService.count(new BaseArea());
      return this.vData(count);
    }



    /**
     * 查询区域下的IP段 （分页）
     */
    @PostMapping(value = "/ip")
    @ApiOperation(value = "查询区域下的IP段 （分页）")
    @SysRequestLog(description = "查询区域下的IP段", actionType = ActionType.SELECT)
    public VList<BaseAreaIpSegment> queryOrgIp(@RequestBody AreaUserQuery areaUserQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pagination(areaUserQuery, BaseAreaIpSegment.class);
        example.createCriteria().andEqualTo(AREA_CODE, areaUserQuery.getCode());
        return this.vList(baseAreaIpSegmentService.findByExample(example));
    }

    /**
     * 增加区域下的IP段
     */
    @PutMapping(value = "/ip")
    @ApiOperation(value = "增加区域下的IP段")
    @SysRequestLog(description = "增加区域下的IP段", actionType = ActionType.ADD)
    public Result addOrgIp(@RequestBody BaseAreaIpSegment baseAreaIpSegment) {
        Long start = IPUtils.ip2int(baseAreaIpSegment.getStartIp());
        Long end = IPUtils.ip2int(baseAreaIpSegment.getEndIp());
        if (start == 0L || end == 0L) {
            return this.result(ErrorCode.ORG_IP_INVALIDATE);
        }
        if (end < start) {
            return this.result(ErrorCode.ORG_IP_RANGE_VALIDATE);
        }
        List<BaseArea> baseAreas = baseAreaService.findByProperty(BaseArea.class, AREA_CODE,baseAreaIpSegment.getAreaCode());
        BaseArea baseArea = null;
        if(baseAreas.size()>0){
            baseArea = baseAreas.get(0);
        }

//        String parentCode = baseArea.getParentCode();
        Example query;
//        if (parentCode.length() > 2) {
//            query = new Example(BaseAreaIpSegment.class);
//            query.createCriteria().andEqualTo("areaCode", parentCode);
//            List<BaseAreaIpSegment> parents = baseAreaIpSegmentService.findByExample(query);
//            boolean inParent = false;
//            for (BaseAreaIpSegment pIp : parents) {
//                if (start >= pIp.getStartIpNum() && end <= pIp.getEndIpNum()) {
//                    inParent = true;
//                    break;
//                }
//            }
//            if (!inParent) {
//                return this.result(ErrorCode.ORG_IP_NOT_IN_PARENT);
//            }
//        }
        query = new Example(BaseAreaIpSegment.class);
        query.createCriteria().andEqualTo(AREA_CODE, baseAreaIpSegment.getAreaCode());
        List<BaseAreaIpSegment> selfs = baseAreaIpSegmentService.findByExample(query);
        boolean conflict = false;
        for (BaseAreaIpSegment sIp : selfs) {
            if ((start <= sIp.getEndIpNum() && start >= sIp.getStartIpNum()) || (end <= sIp.getEndIpNum() && end >= sIp.getStartIpNum())) {
                conflict = true;
            }
        }
        if (conflict) {
            return this.result(ErrorCode.ORG_IP_IN_SELF);
        }
        baseAreaIpSegment.setAreaCode(baseArea != null ? baseArea.getAreaCode() : null);
        baseAreaIpSegment.setStartIpNum(start);
        baseAreaIpSegment.setEndIpNum(end);
        int result = baseAreaIpSegmentService.save(baseAreaIpSegment);
        if(result>0) {
            SyslogSenderUtils.sendAddSyslog(baseAreaIpSegment,"增加区域下的IP段");
            return this.vData(baseAreaIpSegment);
        }
        return this.vData(false);
    }


    /**
     * 删除区域下的IP段
     */
    @DeleteMapping(value = "/ip")
    @ApiOperation(value = "删除区域下的IP段")
    @SysRequestLog(description = "删除区域下的IP段", actionType = ActionType.DELETE)
    public Result delOrgIp(@RequestBody DeleteQuery deleteQuery) {
        List<BaseAreaIpSegment> baseAreaIpSegments = baseAreaIpSegmentService.findByids(deleteQuery.getIds());
        int result = baseAreaIpSegmentService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            baseAreaIpSegments.forEach(baseAreaIpSegment -> {
                SyslogSenderUtils.sendDeleteSyslog(baseAreaIpSegment,"删除区域下的IP段");
            });
            return this.vData(true);
        }
        return this.vData(false);
    }







}
