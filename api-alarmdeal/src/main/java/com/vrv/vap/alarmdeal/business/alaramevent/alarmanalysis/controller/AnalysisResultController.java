package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmModel.model.WarnAnalysisVO;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.impl.SrcIpScoreService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AlarmAttackPath;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AlarmScoreVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.AnalysisVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ExpertVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmDealServer;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AnalysisStatusVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.DealTaskstaticVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.WarnResultForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.AssetIpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.LogTableNameVO;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.impl.WarnResultCreateService;
import com.vrv.vap.alarmdeal.business.analysis.vo.AttackVO;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessIntance;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.controller.BaseController;
import com.vrv.vap.alarmdeal.frameworks.util.GwParamsUtil;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(description="预警分析")
@RestController
@RequestMapping("/analysis")
public class AnalysisResultController extends BaseController {

	private Logger logger = LoggerFactory.getLogger(AnalysisResultController.class);

	@Autowired
	private WarnResultForESService warnResultForEsService;
	@Autowired
	private AlarmAnalysisService alarmAnalysisService;
	@Autowired
	private SrcIpScoreService srcIpScoreService;
	@Autowired
	private WarnResultCreateService warnResultCreateService;
	@Autowired
	private AlarmDealServer alarmDealServer;
	@Autowired
	private FileConfiguration fileConfiguration;
	/**
	 * 获得预警分页信息
	 * 
	 * @param analysisVO
	 * @param pageReq
	 * @return
	 */
	@PostMapping("/getAnalysisPager")
	@ApiOperation(value="预警分析-获得预警分页信息（昨日失陷终端事件）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="stime（stime和etime相同）",value="日期",required=true,dataType="String"),
		@ApiImplicitParam(name="etime",value="日期",required=true,dataType="String"),
		@ApiImplicitParam(name="eventtypelevel",value="事件类型",required=true,dataType="String"),
		@ApiImplicitParam(name="orgCode",value="区域",required=true,dataType="String")
})
	@SysRequestLog(description="预警分析-获得预警分页信息（昨日失陷终端事件）", actionType = ActionType.SELECT,manually=false)
	public PageRes_ES<WarnResultLogTmpVO> getAlarmPager(@RequestBody AnalysisVO analysisVO, PageReq_ES pageReq) {
		try{
			PageRes_ES<WarnResultLogTmpVO> pageRes = warnResultForEsService.getAlarmPager(analysisVO, pageReq);
			List<WarnResultLogTmpVO> warnResultLogVoS=pageRes.getList();
			for(WarnResultLogTmpVO warnResultLogVO:warnResultLogVoS){
				List<Map<String,Object>> logsReplace=new ArrayList<>();
				if(StringUtils.isBlank(warnResultLogVO.getRelatedIps())){
					warnResultLogVO.setRelatedIps(warnResultLogVO.getSrc_ips());
				}else if(StringUtils.isBlank(warnResultLogVO.getSrc_ips())){
					warnResultLogVO.setSrc_ips(warnResultLogVO.getRelatedIps());
				}
				Map<String,String[]> idRoom=warnResultLogVO.getIdRoom();

				if(idRoom!=null){  //id窗口
					for(Map.Entry<String,String[]> entry : idRoom.entrySet()){
						Integer valueSize=entry.getValue().length;
						for(Integer i=0;i<valueSize;i++){
							logsReplace.add(new HashMap<>());
						}

					}
					warnResultLogVO.setLogsInfo(JSON.toJSONString(logsReplace));
				}
			}

			return pageRes;			
		}catch(Exception e) {
			logger.error(e.getMessage());
			PageRes_ES<WarnResultLogTmpVO> pageRes = new PageRes_ES<>();
			pageRes.setCode("0");
			pageRes.setList(null);
			pageRes.setTotal(Long.valueOf("0"));
			pageRes.setMessage("告警为空");
			return pageRes;
		}
		
	}
	
	
	@PostMapping("/getAttackAlarmPager")
	@ApiOperation(value="获得web攻击事件分页信息",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="stime（stime和etime相同）",value="开始日期",required=true,dataType="String"),
		@ApiImplicitParam(name="etime",value="结束日期",required=true,dataType="String"),
		@ApiImplicitParam(name="riskEventName",value="事件类型名称",required=true,dataType="String"),
		@ApiImplicitParam(name="weight",value="区域",required=true,dataType="String")
})
	@SysRequestLog(description="预警分析-获得web攻击事件分页信息", actionType = ActionType.SELECT,manually=false)
	public PageRes_ES<WarnAnalysisVO> getAttackAlarmPager(@RequestBody AnalysisVO analysisVO, PageReq_ES pageReq) {
		try{
			PageRes_ES<WarnAnalysisVO> pageRes = warnResultForEsService.getAttackAlarmPager(analysisVO, pageReq);
			return pageRes;			
		}catch(Exception e) {
			PageRes_ES<WarnAnalysisVO> pageRes = new PageRes_ES<>();
			pageRes.setCode("0");
			pageRes.setList(null);
			pageRes.setTotal(Long.valueOf("0"));
			pageRes.setMessage("查询告警错误："+e.getMessage());
			return pageRes;
		}
		
	}
	
	

	/**
	 * 获得riskEventName
	 * 
	 * @return
	 */
	@GetMapping("/getSafeAlarmTitle")
	@ApiOperation(value="获得riskEventName",notes="")
	@SysRequestLog(description="预警分析-获得riskEventName", actionType = ActionType.SELECT,manually=false)
	public List<String> getSafeAlarmTitle() {
		List<String> list = warnResultForEsService.getSafeAlarmTitle();
        return list;
	}

	/**
	 * 获得30天的告警趋势
	 * @param map
	 * @return
	 */
	@PostMapping("/getSafeAlarmTrendBy30Days")
	@ApiOperation(value="获得30天的告警趋势",notes="")
	@SysRequestLog(description="预警分析-获得30天的告警趋势", actionType = ActionType.SELECT,manually=false)
	public List<List<Map<String, Object>>> getSafeAlarmTrendBy30Days(@RequestBody Map<String,Object> map) {
		Object riskEvent = map.get("riskeventname");
		if(riskEvent!=null) {
			String riskEventName = riskEvent.toString();
			List<List<Map<String, Object>>> list = warnResultForEsService.getSafeAlarmTrendBy30Days(riskEventName);
			return list;
		}else {
			List<List<Map<String, Object>>> list = new ArrayList<>();
			return list;
		}
		
	}

	
	@PostMapping("/getSafeAlarmTrendBy30DaysByEventCategory")
	@ApiOperation(value="获得事件分类30天的告警趋势",notes="")
	@SysRequestLog(description="预警分析-获得事件分类30天的告警趋势", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getSafeAlarmTrendBy30DaysByEventCategory(@RequestBody Map<String,Object> map) {
		Object riskEvent = map.get("riskEventCode");
		if(riskEvent!=null) {
			String riskEventCode = riskEvent.toString();
			List<Map<String, Object>> list = warnResultForEsService.getSafeAlarmTrendBy30DaysByEventCategory(riskEventCode);
			return ResultUtil.success(list);
		}else {
			List<Map<String, Object>> list = new ArrayList<>();
			return ResultUtil.success(list);
		}
		
	}
	
	
	@GetMapping("/getSafeAlarmTrendBy7Days")
	@ApiOperation(value="获得7天的告警趋势",notes="")
	@SysRequestLog(description="预警分析-获得7天的告警趋势", actionType = ActionType.SELECT,manually=false)
	public List<Map<String, Object>> getSafeAlarmTrendBy7Days() {
		List<Map<String,Object>> list = warnResultForEsService.getSafeAlarmTrendBy7Days();
		return list;
	}
	
	/**
	 * 获得一年的告警统计
	 * 
	 * @return
	 */
	@GetMapping("/getAlarmDealCountByOneYear")
	@ApiOperation(value="获得一年的告警统计",notes="")
	@SysRequestLog(description="预警分析-获得一年的告警统计", actionType = ActionType.SELECT,manually=false)
	public List<Map<String, Object>> getAlarmDealCountByOneYear() {
		List<Map<String, Object>> list = warnResultForEsService.getAlarmDealCountByOneYear();
		return list;
	}

	/**
	 * 获得半年的告警处置情况
	 * 
	 * @return
	 */
	@GetMapping("/getDealTaskAssignList")
	@ApiOperation(value="预警分析-获得半年的告警处置情况",notes="")
	@SysRequestLog(description="预警分析-获得半年的告警处置情况", actionType = ActionType.SELECT,manually=false)
	public List<DealTaskstaticVO> getDealTaskAssignList() {
		List<DealTaskstaticVO> list = alarmDealServer.getDealTaskAssignList();
		return list;
	}

	/**
	 * 告警名称分类
	 * 
	 * @param analysisVO
	 * @return
	 */
	@PostMapping("/getAlarmNames")
	@ApiOperation(value="预警分析-告警名称分类",notes="")
	@SysRequestLog(description="预警分析-告警名称分类", actionType = ActionType.SELECT,manually=false)
	public List<Map<String, Object>> getAlarmNames(@RequestBody AnalysisVO analysisVO) {
		try{
			List <Map<String, Object>> list = warnResultForEsService.getAlarmNames(analysisVO);
			return list;
		}catch(Exception e) {
			List <Map<String, Object>> list = new ArrayList<>();
			return list;
		}
	}

	/**
	 * 根据guid获得预警表（需要确定以后改）
	 * 
	 * @param logTableNameVO
	 * @return
	 */
	@PostMapping("/getAnalysisTable")
	@ApiOperation(value="预警分析-获得追溯原始日志",notes="")
	@SysRequestLog(description="预警分析-获得追溯原始日志", actionType = ActionType.SELECT,manually=false)
	public Map<String, Object> getAnalysisTable(@RequestBody LogTableNameVO logTableNameVO) {
		Map<String, Object> map = new HashMap<String,Object>();
		map = warnResultForEsService.getAnalysisTable(logTableNameVO);
		return map;
	}

	@GetMapping("/getAnalysisLogName/{guid}")
	@ApiOperation(value="预警分析-获得告警日志名称",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="guid",value="告警ID",required=true,dataType="String")
	})
	@SysRequestLog(description="预警分析-获得告警日志名称", actionType = ActionType.SELECT,manually=false)
	public Result<List<LogTableNameVO>> getLogTableNamVos(@PathVariable("guid") String guid){
		List<LogTableNameVO> logTableNamVos = warnResultForEsService.getLogTableNamVOs(guid);
		Result<List<LogTableNameVO>> result = ResultUtil.success(logTableNamVos);
		return result;
	}
	

	/**
	 * 查询告警Bar信息
	 */
	@PostMapping("/getAnalysisBarList")
	@ApiOperation(value=" 查询告警Bar信息",notes="")
	@SysRequestLog(description="预警分析-查询告警Bar信息", actionType = ActionType.SELECT,manually=false)
	public List<Map<String, Object>> getAnalysisBarList(@RequestBody AnalysisVO analysisVO) {
		List<Map<String, Object>> list = warnResultForEsService.analysisBarList(analysisVO);
		return list;
	}

	/**
	 * 根据ID获得对应的预警信息
	 * 
	 * @param guid
	 * @return
	 */
	@GetMapping(value = "/getAlarmById/{guid}")
	@ApiOperation(value="根据guid获得对应的预警信息",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="guid",value="告警类型ID",required=true,dataType="String")
	})
	@SysRequestLog(description="预警分析-根据guid获得对应的预警信息", actionType = ActionType.SELECT,manually=false)
	public Result<List<WarnResultLogTmpVO>> getAlarmById(@PathVariable("guid") String guid) {
		List<WarnResultLogTmpVO> list = warnResultForEsService.getAlarmByIds(guid);
		Result<List<WarnResultLogTmpVO>> result = ResultUtil.success(list);
		return result;
	}

	/**
	 * 修改预警状态
	 * 
	 * @param analysisStatusVO
	 * @return
	 */
	@PostMapping(value = "/changeAnalysisResultStatus")
	@ApiOperation(value="修改预警状态",notes="")
	@SysRequestLog(description="预警分析-修改预警状态", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> changeAnalysisResultStatus(@RequestBody AnalysisStatusVO analysisStatusVO) {
		Result<Boolean> result = new Result<>();
		boolean status = false;
		status = warnResultForEsService.changeAnalysisResultStatus(analysisStatusVO);
		Result<Boolean> success = ResultUtil.success(status);
		return success;
	}
	
	/**
	 * 获得预警分类数
	 * @return
	 */
	@PostMapping(value = "/getCountByAlarmType")
	@ApiOperation(value="获得预警分类数",notes="")
	@SysRequestLog(description="预警分析-获得预警分类数", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getAlarmById(@RequestBody AnalysisVO analysisVO) {
		List<Map<String,Object>> list = warnResultForEsService.getCountByAlarmType(analysisVO);
		Result<List<Map<String, Object>>> result = ResultUtil.success(list);
		return result;
	}

	@PostMapping(value="/transferExpert")
	@ApiOperation(value="告警转专家",notes="")
	@SysRequestLog(description="预警分析-告警转专家", actionType = ActionType.SELECT,manually=false)
    public Result<BusinessIntance> transferExpert(@RequestBody ExpertVO experVO){
		Result<BusinessIntance> result = warnResultForEsService.transferExpert(experVO);
		return result;
	}
	
	
	@PostMapping(value="/transferAlarm")
	@ApiOperation(value="告警转处置",notes="")
	@SysRequestLog(description="预警分析-告警转处置", actionType = ActionType.SELECT,manually=false)
	public Result<BusinessIntance> transferAlarm(@RequestBody ExpertVO experVO){
		Result<BusinessIntance> result = warnResultForEsService.transferAlarm(experVO);
		return result;
	}

	
	/**
	 * 获得原始日志信息
	 * @return
	 */
	@GetMapping(value="/getOriginalLogInfo")
	@ApiOperation(value="获得原始日志",notes="")
	@SysRequestLog(description="预警分析-获得原始日志", actionType = ActionType.SELECT,manually=false)
    public Result<List<Map<String,Object>>> getOriginalLogInfo(){
		 List<Map<String,Object>> list = warnResultForEsService.getOrignalLogInfo();
		 Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		 return result;
	}
	
	@GetMapping(value="/getRiskEventListTop10")
	@ApiOperation(value="获得告警分类的top10",notes="")
	@SysRequestLog(description="预警分析-获得告警分类的top10", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getRiskEventListTop10(){
		List<Map<String,Object>> list = warnResultForEsService.getStasticsByRelateField("riskEventName",10);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	@PostMapping(value="/getRiskEventListTop")
	@ApiOperation(value="获得告警分类的top",notes="")
	@SysRequestLog(description="预警分析-获得告警分类的top", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getRiskEventListTop(@RequestBody Map<String,Object> map){
		Integer topCount=(Integer) map.get("size");
		List<Map<String,Object>> list = warnResultForEsService.getStasticsByRelateField("riskEventName",topCount);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}


	@PostMapping(value="/getRiskEventListTop10")
	@ApiOperation(value="条件筛选后，获得告警分类的top10",notes="")
	@SysRequestLog(description="预警分析-条件筛选后，获得告警分类的top10", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getRiskEventListTop10(@RequestBody AnalysisVO analysisVO){
		List<Map<String,Object>> list = warnResultForEsService.getStasticsByRelateField(analysisVO,"riskEventName",10);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	@GetMapping(value="/getAlarmResultByStatusNum")
	@ApiOperation(value="根据告警状态进行分类",notes="")
	@SysRequestLog(description="预警分析-根据告警状态进行分类", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmResultByStatusNum(){
		List<Map<String,Object>> list = warnResultForEsService.getStasticsByRelateField("statusEnum",10);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	@PostMapping(value="/getAlarmResultByStatusNum")
	@ApiOperation(value="条件筛选后，根据告警状态进行分类",notes="")
	@SysRequestLog(description="预警分析-条件筛选后，根据告警状态进行分类", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmResultByStatusNum(@RequestBody AnalysisVO analysisVO){
		List<Map<String,Object>> list = warnResultForEsService.getStasticsByRelateField(analysisVO,"statusEnum",10);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	@ApiOperation(value="根据告警威胁分布情况",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="filter",value="时间过滤类型",required=true,dataType="String"),
		@ApiImplicitParam(name="ip",value="ip",required=true,dataType="String"),
})
	@PostMapping(value="/getALarmRiskEvent")
	@SysRequestLog(description="预警分析-根据告警威胁分布情况", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmRiskEvent(@RequestBody Map<String, Object> map){
		List<Map<String,Object>> list = warnResultForEsService.getALarmRiskEvent(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	
	

	@ApiOperation(value="获得告警等级分布统计（终端告警级别分布统计）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="ip",value="ip",required=true,dataType="String"),
		@ApiImplicitParam(name="start_time",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=true,dataType="String")
})
	@PostMapping(value="/getAlarmByWeight")
	@SysRequestLog(description="预警分析-获得告警等级分布统计（终端告警级别分布统计）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmByWeight(@RequestBody AnalysisVO analysisVO){
		List<Map<String,Object>> list = warnResultForEsService.getAlarmByWeight(analysisVO);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	
	@ApiOperation(value="获得告警等级和时间相关分布图（告警事件日期分布情况统计）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="ip",value="ip",required=true,dataType="String"),
		@ApiImplicitParam(name="start_time",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=true,dataType="String")
})
	@PostMapping(value="/getAlarmdByWeightAndTime")
	@SysRequestLog(description="预警分析-获得告警等级和时间相关分布图（告警事件日期分布情况统计）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmdByWeightAndTime(@RequestBody Map<String, Object> map){
		List<Map<String,Object>> list = warnResultForEsService.getAlarmdByWeightAndTime(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	
	@ApiOperation(value="告警类型区域统计（昨日失陷终端区域统计）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="type",value="告警类型：失陷终端",required=true,dataType="String"),
		@ApiImplicitParam(name="date",value="时间：yyyy-mm-dd",required=true,dataType="String")
})
	@PostMapping(value="/getAlarmTypeStaticsByRegion")
	@SysRequestLog(description="预警分析-告警类型区域统计（昨日失陷终端区域统计）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmTypeStaticsByRegion(@RequestBody Map<String, Object> map){
		List<Map<String,Object>> list = warnResultForEsService.getAlarmTypeStaticsByRegion(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	@ApiOperation(value="告警按区域和事件分类统计",notes = "")
	@PostMapping(value="/getAlarmStaticsByRegionAndType")
	@SysRequestLog(description="预警分析-告警按区域和事件分类统计", actionType = ActionType.SELECT,manually=false)
	public  Result<List<Map<String, Object>>> getAlarmStaticsByRegionAndType(@RequestBody AnalysisVO analysisVO){

		List<Map<String,Object>> list = warnResultForEsService.getAlarmStaticsByRegionAndType(analysisVO);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	@ApiOperation(value="告警类型趋势（失陷终端个数趋势）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="type",value="告警类型",required=true,dataType="String"),
		@ApiImplicitParam(name="start_time",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=true,dataType="String")
})
	@PostMapping(value="/getAlarmTypeStaticsCount")
	@SysRequestLog(description="预警分析-告警类型趋势（失陷终端个数趋势）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmTypeStaticsCount(@RequestBody Map<String, Object> map){
		List<Map<String,Object>> list = warnResultForEsService.getAlarmTypeStaticsCount(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	
	
	@ApiOperation(value="告警类型端个数单位排行（失陷终端个数单位排行）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="type",value="告警类型",required=true,dataType="String"),
		@ApiImplicitParam(name="start_time",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="end_time",value="结束时间",required=true,dataType="String"),
		@ApiImplicitParam(name="statEnum",value="处置状态：5（已处置）",required=true,dataType="String")
})
	@PostMapping(value="/getAlarmTypeStaticsByRegionTop20")
	@SysRequestLog(description="预警分析-告警类型端个数单位排行（失陷终端个数单位排行）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmTypeStaticsByRegionTop20(@RequestBody Map<String, Object> map){
		List<Map<String,Object>> list = warnResultForEsService.getAlarmTypeStaticsByRegionTop20(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}
	
	@ApiOperation(value="告警类型类型总数排序（失陷规则-失陷终端总数排序）",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="type",value="告警类型",required=true,dataType="String"),
})
	@PostMapping(value="/getAlarmTypeOrderByCount")
	@SysRequestLog(description="预警分析-告警类型类型总数排序（失陷规则-失陷终端总数排序）", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAlarmTypeOrderByCount(@RequestBody Map<String, Object> map){
		List<Map<String,Object>> list = warnResultForEsService.getAlarmTypeOrderByCount(map);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	@PostMapping(value="/getDstIpTop5")
	@ApiOperation(value="遭受攻击最多的资产top5",notes="")
	@SysRequestLog(description="预警分析-遭受攻击最多的资产top5", actionType = ActionType.SELECT,manually=false)
	public Result<List<AssetIpVO>> getDstIpTop5(@RequestBody Map<String,Object> map){
		String riskEventCode = map.get("riskEventCode").toString();
		List<AssetIpVO> list = warnResultForEsService.getDstIpTop5(riskEventCode);
		Result<List<AssetIpVO>> result = ResultUtil.success(list);
		return result;
	}
	
	
	@PostMapping(value="/getSrcIpTop5")
	@ApiOperation(value="攻击源top5",notes="")
	@SysRequestLog(description="预警分析-攻击源top5", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getSrcIpTop5(@RequestBody Map<String,Object> map){
		String riskEventCode = map.get("riskEventCode").toString();
		AnalysisVO analysisVO =new AnalysisVO();
		analysisVO.setEventtypelevel(riskEventCode);//eventtypelevel
		List<Map<String,Object>> list = warnResultForEsService.getSrcIpTop5(analysisVO);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}


	@PostMapping(value="/getSrcIpTop")
	@ApiOperation(value="攻击源top5",notes="")
	@SysRequestLog(description="预警分析-攻击源top5", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getSrcIpTop(@RequestBody Map<String,Object> map){
		AnalysisVO analysisVO =new AnalysisVO();
		List<Map<String,Object>> list = warnResultForEsService.getSrcIpTop5(analysisVO);
		Result<List<Map<String,Object>>> result = ResultUtil.success(list);
		return result;
	}

	
	@PostMapping(value="/getAlarmAttackPath")
	@ApiOperation(value="获得攻击路径",notes="")
	@SysRequestLog(description="预警分析-获得攻击路径", actionType = ActionType.SELECT,manually=false)
	public Result<AlarmAttackPath> getAlarmAttackPath(@RequestBody Map<String,Object> map){
		String riskEventCode = map.get("riskEventCode").toString();
		AlarmAttackPath alarmAttackPath = warnResultForEsService.getAlarmAttackPath(riskEventCode);
		Result<AlarmAttackPath> result = ResultUtil.success(alarmAttackPath);
		return result;
	}

	@PostMapping(value="/getAlarmAttackPathAndNode")
	@ApiOperation(value="获得攻击路径和节点",notes="")
	@SysRequestLog(description="预警分析-获得攻击路径和节点", actionType = ActionType.SELECT,manually=false)
	public Result<AlarmAttackPath> getAlarmAttackPathAndNode(){
		AlarmAttackPath alarmAttackPath = warnResultForEsService.getAlarmAttackPath(null);
		Result<AlarmAttackPath> result = ResultUtil.success(alarmAttackPath);
		return result;
	}

	@PostMapping(value="/getAttackPathAndNode")
	@ApiOperation(value="获得攻击网段分布路径和节点",notes="")
	@SysRequestLog(description="预警分析-获得攻击网段分布路径和节点", actionType = ActionType.SELECT,manually=false)
	public Result<AttackVO> getAttackPathAndNode(){
		AttackVO attackVO = warnResultForEsService.getAttackPath(null);
		Result<AttackVO> result = ResultUtil.success(attackVO);
		return result;
	}

	
	
	@PostMapping(value="/queryThreatValueByThreatType")
	@ApiOperation(value="按威胁类型统计威胁值的和",notes="")
	@SysRequestLog(description="预警分析-按威胁类型统计威胁值的和", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> queryThreatValueByThreatType(@RequestBody Map<String,Object> map){
		Result<List<Map<String,Object>>> result = alarmAnalysisService.queryThreatValueByThreatType();
		return result;
	}
	
	@PostMapping(value="/queryThreatLevelCountByThreatLevel")
	@ApiOperation(value="根据威胁等级统计威胁等级的个数",notes="")
	@SysRequestLog(description="预警分析-根据威胁等级统计威胁等级的个数", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> queryThreatLevelCountByThreatLevel(@RequestBody Map<String,Object> map){
		Result<List<Map<String,Object>>> result = alarmAnalysisService.queryThreatLevelCountByThreatLevel();
		return result;
	}
	
	@PostMapping(value="/queryThreatRankByDepartMent")
	@ApiOperation(value="根据部门进行威胁排分组",notes="")
	@SysRequestLog(description="预警分析-根据部门进行威胁排分组", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> queryThreatRankByDepartMent(@RequestBody Map<String,Object> map){
		Result<List<Map<String,Object>>> result = alarmAnalysisService.queryThreatRankByDepartMent();
		return result;
	}
	
	
	@PostMapping(value="/queryThreatRankByEmployee")
	@ApiOperation(value="根据负责人进行威胁排名",notes="")
	@SysRequestLog(description="预警分析-根据负责人进行威胁排名", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> queryThreatRankByEmployee(@RequestBody Map<String,Object> map){
		Result<List<Map<String,Object>>> result = alarmAnalysisService.queryThreatRankByEmployee();
		return result;
	}
	
	
	
	@PostMapping(value="/attackTypeDistribute")
	@ApiOperation(value="攻击类型分布攻击类型分布)",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="field",value="riskEventName（攻击类型），weight（事件等级）",required=true,dataType="String")
})
	@SysRequestLog(description="预警分析-攻击类型分布攻击类型分布", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> attackTypeDistribute(@RequestBody Map<String,Object> map){
		String field = map.get("field").toString();
		List<Map<String,Object>> result = warnResultForEsService.attackTypeDistribute(field);
		Result<List<Map<String,Object>>> success = ResultUtil.success(result);
		return success;
	}
	
	
	@PostMapping(value="/attackTypeHotPoint")
	@ApiOperation(value="攻击热点",notes="")
	@SysRequestLog(description="预警分析-攻击热点", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> attackTypeHotPoint(@RequestBody Map<String,Object> map){
		List<Map<String,Object>> result = warnResultForEsService.attackTypeHotPoint();
		Result<List<Map<String,Object>>> success = ResultUtil.success(result);
		return success;
	}
	
	
	
	@PostMapping(value="/alarmAttackByTimeStatics")
	@ApiOperation(value="攻击逐时图",notes="")
	@SysRequestLog(description="预警分析-攻击逐时图", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> alarmAttackByTimeStatics(){
		List<Map<String,Object>> list = warnResultForEsService.alarmAttackByTimeStatics();
		Result<List<Map<String,Object>>> success = ResultUtil.success(list);
		return success;
	}
	
	
	
	@PostMapping(value="/getAnalysisInfoByArea")
	@ApiOperation(value="获得告警的攻击区域",notes="")
	@SysRequestLog(description="预警分析-获得告警的攻击区域", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getAnalysisInfoByArea(@RequestBody Map<String,Object> map){
		AnalysisVO analysisVO =new AnalysisVO();
		List<Map<String,Object>> result = warnResultForEsService.getAnalysisInfoByArea(analysisVO);
		Result<List<Map<String,Object>>> success = ResultUtil.success(result);
		return success;
	}
	
	@PostMapping(value="/getSrcIpList")
	@ApiOperation(value="获得攻击告警",notes="")
	@SysRequestLog(description="预警分析-获得攻击告警", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getSrcIpList(@RequestBody Map<String,Object> map){
		Object startTimeObj = map.get("start_time");
		Object endTimeObj = map.get("end_time");
		if(startTimeObj!=null&&endTimeObj!=null) {
			List<Map<String,Object>> list = warnResultForEsService.getSrcIpList(startTimeObj.toString(), endTimeObj.toString());
			Result<List<Map<String,Object>>> result = ResultUtil.success(list);
			return result;
		}else {
			return ResultUtil.success(new ArrayList<Map<String,Object>>());
		}
	}
	
	
	@PostMapping(value="/getSumAlarmScore")
	@ApiOperation(value="最新总扣分接口",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="ip",value="ip",dataType="String")
	})
	@SysRequestLog(description="预警分析-最新总扣分接口", actionType = ActionType.SELECT,manually=false)
	public Result<Integer> getSumAlarmScore(@RequestBody Map<String,Object> map){
		String ip = map.get("ip").toString();
		Integer score = srcIpScoreService.getSumAlarmScore(ip);
		return ResultUtil.success(score);
	}
	
	
	@PostMapping(value="/calculateAlarmScore")
	@ApiOperation(value="计算每个srcIp对应的规则以及分数",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="start_time",value="开始时间",dataType="String"),
		@ApiImplicitParam(name="end_time",value="结束时间",dataType="String"),
		@ApiImplicitParam(name="src_ip",value="源IP",dataType="String")
	})
	@SysRequestLog(description="预警分析-计算每个srcIp对应的规则以及分数", actionType = ActionType.SELECT,manually=false)
	public Result<List<AlarmScoreVO>> calculateAlarmScore(@RequestBody Map<String,Object> map){
        List<AlarmScoreVO> list = srcIpScoreService.calculateAlarmScore(map);
		Result<List<AlarmScoreVO>> result = ResultUtil.successList(list);
		return result;
	}
	
	
	
	@PostMapping(value="/getAlarmScoreTrend")
	@ApiOperation(value="一周间隔内的总扣分趋势",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="src_ip",value="源IP",dataType="String")
	})
	@SysRequestLog(description="预警分析-一周间隔内的总扣分趋势", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getAlarmScoreTrend(@RequestBody Map<String,Object> map){
		String ip = map.get("src_ip").toString();
		List<Map<String,Object>> list = srcIpScoreService.getAlarmScoreTrend(ip);
		return ResultUtil.success(list);
	}
	
	/***************************************国网信息*****************************************/

	@PostMapping(value="/eventAlarmTotal")
	@ApiOperation(value="告警总数查询",notes="")
	@SysRequestLog(description="预警分析-告警总数查询", actionType = ActionType.SELECT,manually=false)
	public Result<Long> eventAlarmTotal(@RequestBody AnalysisVO analysisVO){
		Result<Long> result = warnResultForEsService.eventAlarmTotal(analysisVO);
		return result;
	}
	
	
	@PostMapping(value="/getAlarmTrendBy7Days")
	@ApiOperation(value="七天告警统计趋势图",notes="")
	@SysRequestLog(description="预警分析-七天告警统计趋势图", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String,Object>>> getAlarmTrendBy7Days(@RequestBody AnalysisVO analysisVO){
		Result<List<Map<String,Object>>> result = warnResultForEsService.getAlarmTrendBy7Days(analysisVO);
		return result;
	}
	
	
	@PostMapping(value="/getAlarmEventLevel")
	@ApiOperation(value="获得安全等级",notes="")
	@SysRequestLog(description="预警分析-获得安全等级", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String, Object>> getAlarmEventLevel(@RequestBody AnalysisVO analysisVO){
		Result<Map<String,Object>> result = warnResultForEsService.getAlarmEventLevel(analysisVO);
		return result;
	}
	
	
	@PostMapping(value="/getLevelEventCateoryByTriggerTime")
	@ApiOperation(value="获得等级事件分类(时间)",notes="")
	@SysRequestLog(description="预警分析-获得等级事件分类(时间)", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getLevelEventCateoryByTriggerTime(@RequestBody AnalysisVO analysisVO){
		Result<List<Map<String,Object>>> result = warnResultForEsService.getLevelEventCateoryByTriggerTime(analysisVO);
		return result;
	}
	
	@PostMapping(value="/getLevelEventCateoryByRegion")
	@ApiOperation(value="获得等级事件分类(区域)",notes="")
	@SysRequestLog(description="预警分析-获得等级事件分类(区域)", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getLevelEventCateoryByRegion(@RequestBody AnalysisVO analysisVO){
		Result<List<Map<String,Object>>> result = warnResultForEsService.getLevelEventCateoryByRegion(analysisVO);
		return result;
	}
	
	
	@PostMapping(value="/getSelfMultiAlarmTrendBy7Days")
	@ApiOperation(value="七天告警统计趋势图(获取弱口令、基线不合规、未安装重要)",notes="")
	@SysRequestLog(description="预警分析-七天告警统计趋势图(获取弱口令、基线不合规、未安装重要)", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String,Object>> getSelfMultiAlarmTrendBy7Days(@RequestBody Map<String,Object> map){
		List<AnalysisVO> list = GwParamsUtil.getSelfAnalysisList(map);
		Result<Map<String,Object>> result = warnResultForEsService.getMultiAlarmTrendBy7Days(list);
		return result;
	}
	
	@PostMapping(value="/getBehaveorMultiAlarmTrendBy7Days")
	@ApiOperation(value="七天告警统计趋势图(用户行为，本体行为 )",notes="")
	@SysRequestLog(description="预警分析-七天告警统计趋势图(用户行为，本体行为)", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String,Object>> getBehaveorMultiAlarmTrendBy7Days(@RequestBody Map<String,Object> map){
		List<AnalysisVO> list = GwParamsUtil.getBehaveorAnalysisList(map);
		Result<Map<String,Object>> result = warnResultForEsService.getMultiAlarmTrendBy7Days(list);
		return result;
	}
	
	
	@PostMapping(value="/getMultiEventAlarmTotal")
	@ApiOperation(value="事件总数(用户行为、网络行为)",notes="")
	@SysRequestLog(description="预警分析-事件总数(用户行为、网络行为)", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String,Object>> getMultiEventAlarmTotal(@RequestBody Map<String,Object> map){
		List<AnalysisVO> list = GwParamsUtil.getAnalysisCount(map);
		Result<Map<String,Object>> result = warnResultForEsService.getMultiEventAlarmTotal(list);
		return result;
	}
	

	
	@PostMapping(value="/getMultiSelfAlarmTotal")
	@ApiOperation(value="本体事件总数(弱口令设备(事件总数)、基线不合规、病毒库未更新、高危端口开放、创建用户、用户权限变更、注册表告警事件)",notes="")
	@SysRequestLog(description="预警分析-本体事件总数(弱口令设备(事件总数)、基线不合规、病毒库未更新、高危端口开放、创建用户、用户权限变更、注册表告警事件)", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String,Object>> getMultiSelfAlarmTotal(@RequestBody Map<String,Object> map){
		List<AnalysisVO> list = GwParamsUtil.getSelfAlarmCount(map);
		Result<Map<String,Object>> result = warnResultForEsService.getMultiEventAlarmTotal(list);
		return result;
	}
	
	
	@PostMapping(value="/getCountByRegion")
	@ApiOperation(value="获取全网各区域的用户行为和网络行为告警事件统计数据",notes="")
	@SysRequestLog(description="预警分析-获取全网各区域的用户行为和网络行为告警事件统计数据", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String,Object>> getCountByRegion(@RequestBody Map<String,Object> map){
		List<AnalysisVO> list = GwParamsUtil.getAnalysisCount(map);
		Result<Map<String,Object>> result = warnResultForEsService.getCountByRegion(list);
		return result;
	}
	
	@PostMapping(value="/deleteRelateEsData")
	@ApiOperation(value="删除相关的数据",notes="")
	@SysRequestLog(description="预警分析-删除相关的数据", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> deleteRelateEsData(@RequestBody AnalysisVO analysisVO){
		Boolean deleteRelateEsData = warnResultCreateService.deleteRelateEsData(analysisVO);
		Result<Boolean> result = ResultUtil.success(deleteRelateEsData);
		return result;
	}
	
	@PostMapping(value = "/getAccessAlarmCount")
	@ApiOperation(value="获得准入五大类告警数",notes="")
	@SysRequestLog(description="预警分析-获得准入五大类告警数", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getAccessAlarmCount(@RequestBody Map<String,Object> map) {
	    List<Map<String,Object>> mapList = new ArrayList<>();
		List<AnalysisVO> accessAlarm = GwParamsUtil.getAccessAlarm(map);
		for (AnalysisVO analysisVO : accessAlarm) {
			Map<String,Object> map1 = new HashMap<>();
			Result<Long> total = warnResultForEsService.eventAlarmTotal(analysisVO);
			map1.put("name", analysisVO.getExtraField());
			map1.put("value", total.getData());
			mapList.add(map1);
		}
		Result<List<Map<String, Object>>> result = ResultUtil.success(mapList);
		return result;
	}
	
	
	
	@GetMapping(value = "/getKnowledgeByTag/{ruleId}")
	@ApiOperation(value="根据标签查询知识库的相关信息",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="ruleId",value="规则Id",required=true,dataType="String"),
   })
	@SysRequestLog(description="预警分析-根据标签查询知识库的相关信息", actionType = ActionType.SELECT,manually=false)
	public ResultObjVO<Map<String,Object>> getKnowledgeByTag(@PathVariable String ruleId) {
		ResultObjVO<Map<String,Object>> knowledgeByTag = warnResultForEsService.getKnowledgeByTag(ruleId);
		return knowledgeByTag;
	}
	
	@PostMapping(value="/fixOldEsData")
	@ApiOperation(value="修复旧的ES数据",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="stime（格式yyyy-mm-dd）",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="etime（格式yyyy-mm-dd）",value="结束日期",required=true,dataType="String"),
	})
	@SysRequestLog(description="预警分析-修复旧的ES数据", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> fixOldEsData(@RequestBody Map<String,Object> map){
		String stime = null;
		String etime = null;
		Object stimeObj = map.get("stime");
		Object etimeObj = map.get("etime");
		if(stimeObj!=null&&etimeObj!=null){
			stime = stimeObj.toString();
			etime = etimeObj.toString();
		}
		Result<Boolean> result = warnResultCreateService.fixOldEsDataByDate(stime, etime);
		return result;
	}

	@PostMapping(value="/deleteOldEsData")
	@ApiOperation(value="删除ES脏数据",notes="")
	@ApiImplicitParams({
			@ApiImplicitParam(name="stime（格式yyyy-mm-dd）",value="开始时间",required=true,dataType="String"),
			@ApiImplicitParam(name="etime（格式yyyy-mm-dd）",value="结束日期",required=true,dataType="String"),
	})
	@SysRequestLog(description="预警分析-删除ES脏数据", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> deleteOldEsData(@RequestBody Map<String,Object> map){
		String stime = null;
		String etime = null;
		Object stimeObj = map.get("stime");
		Object etimeObj = map.get("etime");
		if(stimeObj!=null&&etimeObj!=null){
			stime = stimeObj.toString();
			etime = etimeObj.toString();
		}
		Result<Boolean> result = warnResultCreateService.deleteOldEsDataByDate(stime, etime);
		return result;
	}
	
	
	@PostMapping(value="/createIndexAndMapping")
	@ApiOperation(value="旧索引迁移工作",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="stime（格式yyyy-mm-dd）",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="etime（格式yyyy-mm-dd）",value="结束日期",required=true,dataType="String"),
	})
	@SysRequestLog(description="预警分析-旧索引迁移工作", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> createIndex(@RequestBody Map<String,Object> map){
		String stime = null;
		String etime = null;
		Object stimeObj = map.get("stime");
		Object etimeObj = map.get("etime");
		if(stimeObj!=null&&etimeObj!=null){
			stime = stimeObj.toString();
			etime = etimeObj.toString();
		}
		Result<Boolean> result = warnResultCreateService.transformOldIndexDataToNewIndex(stime, etime);
		return result;
	}
	
	
	
	@PostMapping(value="/deleteRepeatEsData")
	@ApiOperation(value="删除重复数据",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="stime（格式yyyy-mm-dd）",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="etime（格式yyyy-mm-dd）",value="结束日期",required=true,dataType="String"),
	})
	@SysRequestLog(description="预警分析-删除重复数据", actionType = ActionType.SELECT,manually=false)
	public Result<Boolean> deleteRepeatEsData(@RequestBody Map<String,Object> map){
		String stime = null;
		String etime = null;
		Object stimeObj = map.get("stime");
		Object etimeObj = map.get("etime");
		if(stimeObj!=null&&etimeObj!=null){
			stime = stimeObj.toString();
			etime = etimeObj.toString();
		}
		Result<Boolean> result = warnResultCreateService.deleteRepeatESData(stime,etime);
		return result;
	}


	/**
	 * 告警信息导入excel
	 * isAll 是否导出全部
	 */
	@PostMapping("exportAlarmDeal")
	@ApiOperation(value="guids",notes="告警信息下载")
	@SysRequestLog(description="预警分析-告警信息下载", actionType = ActionType.SELECT,manually=false)
	public Result<String> exportAlarmDeal(@RequestBody Map<String,Object> map){
		Result<String> result=warnResultForEsService.exportAlarmDeal(map);
		return  result;
	}
	/**
	 * 告警规则浏览器下载
	 */
	@GetMapping("/downAlarmDealExcel/{fileName:.+}")
	@ApiOperation(value="fileName",notes="告警信息浏览器导出")
	@SysRequestLog(description="预警分析-告警信息浏览器导出", actionType = ActionType.SELECT,manually=false)
	public void  downAlarmDealExcel(HttpServletResponse response, @PathVariable("fileName") String fileName){
		FileUtil.downLoadFile(fileName, fileConfiguration.getFilePath(), response);
	}



	@PostMapping(value = "/eventAlarmStatusTotalByWorkBench")
	@ApiOperation(value="工作台告警状态统计",notes="")
	@ApiImplicitParams({
		@ApiImplicitParam(name="stime（格式yyyy-mm-dd）",value="开始时间",required=true,dataType="String"),
		@ApiImplicitParam(name="etime（格式yyyy-mm-dd）",value="结束日期",required=true,dataType="String"),
	})
	@SysRequestLog(description="预警分析-工作台告警状态统计", actionType = ActionType.SELECT,manually=false)
	public Result<Map<String, Object>> eventAlarmStatusTotalByWorkBench(@RequestBody AnalysisVO analysisVO) {
		Result<Map<String,Object>> result = warnResultForEsService.eventAlarmStatusTotalByWorkBench(analysisVO);
		return result;
	}


	@PostMapping(value="/getWeightTrend")
	@ApiOperation(value="获得等级事件分类时间趋势",notes="")
	@SysRequestLog(description="预警分析-获得等级事件分类时间趋势", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getWeightTrend(@RequestBody AnalysisVO analysisVO){
		Result<List<Map<String,Object>>> result = warnResultForEsService.getWeightTrend(analysisVO);
		return result;
	}

	@PostMapping(value="/getStatusBar")
	@ApiOperation(value="获得告警状态统计柱状图",notes="")
	@SysRequestLog(description="预警分析-获得告警状态统计柱状图", actionType = ActionType.SELECT,manually=false)
	public Result<List<Map<String, Object>>> getStatusBar(@RequestBody AnalysisVO analysisVO){
		Result<List<Map<String,Object>>> result = warnResultForEsService.getStatusBar(analysisVO);
		return result;
	}


	@PostMapping(value="/getSrcIpSum")
	@ApiOperation(value="攻击数统计",notes="")
	@SysRequestLog(description="预警分析-攻击数统计", actionType = ActionType.SELECT,manually=false)
	public Result<Integer> getSrcIpSum(@RequestBody AnalysisVO analysisVO){
		int getSrcIpSum = warnResultForEsService.getSrcIpSum(analysisVO);
		return ResultUtil.success(getSrcIpSum);

	}

	@PostMapping(value="/getDstIpSum")
	@ApiOperation(value="被攻击数统计",notes="")
	@SysRequestLog(description="预警分析-被攻击数统计", actionType = ActionType.SELECT,manually=false)
	public Result<Integer> getDstIpSum(@RequestBody AnalysisVO analysisVO){
		int getSrcIpSum = warnResultForEsService.getDstIpSum(analysisVO);
		return ResultUtil.success(getSrcIpSum);

	}



	
	
}
