package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.model.AttackModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.model.PrintBurnModel;
import com.vrv.vap.xc.service.BigScreenService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class BigScreenController {
    @Autowired
    BigScreenService bigScreenService;
    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }
    
    /**
     *被攻击ip个数
     * @param 
     * @return
     */
    @PostMapping("/screen/attackIpCount")
    @ApiOperation("被攻击ip个数")
    public VData<Integer> attackIpCount(@RequestBody AttackModel model){
        return bigScreenService.attackIpCount(model) ;
    }

    /**
     * 被攻击应用排行
     * @return
     */
    @PostMapping("/screen/attackSysCount")
    @ApiOperation("被攻击应用排行")
    public VData<List<Map<String,Object>>> attackSysCount(@RequestBody PageModel model){
        return bigScreenService.attackSysCount(model) ;
    }
    /**
     * 被攻击事件类别
     * @return
     */
    @PostMapping("/screen/attackType")
    @ApiOperation("被攻击事件类别")
    public VData<List<Map<String,Object>>> attackType(@RequestBody PageModel model){
        return bigScreenService.attackType(model) ;
    }

    /**
     * 攻击趋势变化
     * @return
     */
    @PostMapping("/screen/attackTrend")
    @ApiOperation("攻击趋势变化")
    public VData<List<Map<String,Object>>> attackTrend(@RequestBody PrintBurnModel model){
        return bigScreenService.attackTrend(model) ;
    }

    /**
     * 用户行为态势-访问设备排名
     * @return
     */
    @PostMapping("/screen/userVisitDev")
    @ApiOperation("用户行为态势-访问设备排名")
    public VData<List<Map<String,Object>>> userVisitDev(@RequestBody PageModel model){
        return bigScreenService.userVisitDev(model) ;
    }

    @PostMapping("/screen/cpuTrend")
    @ApiOperation("CPU排行")
    public VData<List<Map<String,Object>>> cpuTrend(@RequestBody PageModel model){
        return bigScreenService.cpuTrend(model) ;
    }

    @PostMapping("/screen/memoryTrend")
    @ApiOperation("内存排行")
    public VData<List<Map<String,Object>>> memoryTrend(@RequestBody PageModel model){
        return bigScreenService.memoryTrend(model) ;
    }

    @PostMapping("/screen/diskTrend")
    @ApiOperation("磁盘排行")
    public VData<List<Map<String,Object>>> diskTrend(@RequestBody PageModel model){
        return bigScreenService.diskTrend(model) ;
    }

    @PostMapping("/screen/visitRank")
    @ApiOperation("访客排名")
    public VData<List<Map<String,Object>>> visitRank(@RequestBody PageModel model){
        return bigScreenService.visitRank(model) ;
    }

    /**
     * 被攻击应用排行
     * @return
     */
    @PostMapping("/screen/attackDistribution")
    @ApiOperation("攻击态势-实时攻击分布")
    public VData<List<Map<String,Object>>> attackDistribution(@RequestBody PageModel model){
        return bigScreenService.attackDistribution(model) ;
    }

    /**
     * 攻击态势-实时监测
     * @return
     */
    @PostMapping("/screen/attackMonitor")
    @ApiOperation("攻击态势-实时监测")
    public VData<List<Map<String,Object>>> attackMonitor(@RequestBody PageModel model){
        return bigScreenService.attackMonitor(model) ;
    }
}
