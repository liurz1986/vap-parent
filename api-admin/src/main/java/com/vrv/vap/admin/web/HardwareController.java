package com.vrv.vap.admin.web;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.common.util.CmdExecute;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.service.HardwareService;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.vo.LocalHostInfoVO;
import com.vrv.vap.admin.vo.TModulesVO;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.Time;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RestController
@RequestMapping(value = "/hardware")
public class HardwareController extends ApiController {

	@Autowired
	HardwareService hardwareService;
 
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

	private static final Integer STATUS_OK = 0;

	private Logger logger = LoggerFactory.getLogger(HardwareController.class);

	@Value("${project.logPath:/opt/test/cloud/logs}")
	private String logDir;

	private final ConcurrentMap<Integer, Map<String,Object>> hostInfoMap = new ConcurrentHashMap<>();

	@GetMapping(value = "/getHostInfo")
	@SysRequestLog(description="获取主机信息", actionType = ActionType.SELECT)
	@ApiOperation(value = "获取主机信息", notes = "")
	public VData getHostInfo(){
		Integer key = TimeTools.getSecondTimestampTwo(TimeTools.getNow());
		Map<String,Object> result = new HashMap<>();
		if(hostInfoMap.containsKey(key)){
			logger.error("1111");
			result = hostInfoMap.get(key);
			return this.vData(result);
		}
		hostInfoMap.clear();
		result.put("status", Global.OK.getCode());
		List<LocalHostInfoVO> extendList = new ArrayList<>();
		List<TModulesVO> serverList = new ArrayList<>();
		serverList = hardwareService.getServiceInfo();
		try {
			LocalHostInfoVO vo = hardwareService.getHostInfoDetail();
			extendList.add(vo);
			serverList = hardwareService.getServiceInfo();
			result.put("name",vo.getSystemName());
			result.put("extends",extendList);
			result.put("serverList",serverList);
			key = TimeTools.getSecondTimestampTwo(TimeTools.getNow());
			hostInfoMap.put(key,result);
			return this.vData(result);
		} catch (Exception e) {
			logger.error("获取主机信息异常",e);
		}
		result.put("name","");
		result.put("extends",extendList);
		result.put("serverList",serverList);
		key = TimeTools.getSecondTimestampTwo(TimeTools.getNow());
		hostInfoMap.put(key,result);
		return this.vData(result);
	}

	@GetMapping(value = "/reStart/{serviceName}")
	@SysRequestLog(description="根据服务名称重启服务", actionType = ActionType.UPDATE)
	@ApiOperation(value = "根据服务名称重启服务", notes = "")
	public VData reStartService(@PathVariable @ApiParam("服务名称")String serviceName) {
		return this.vData(hardwareService.reStartService(serviceName));
	}

	@GetMapping(value = "/restart/all")
	@SysRequestLog(description="重启所有服务", actionType = ActionType.UPDATE)
	@ApiOperation(value = "重启所有服务")
	public void restartAllService() {
		// 一键重启
	}

	@GetMapping(value = "/download/{serviceName}")
	@SysRequestLog(description="下载日志", actionType = ActionType.SELECT)
	@ApiOperation(value = "下载日志")
	public void downLoadLog(@PathVariable @ApiParam("服务名称") String serviceName, HttpServletResponse response) {
		String fileName = serviceName + ".log";
		String logPath = logDir + fileName;
		try(InputStream in = new BufferedInputStream(new FileInputStream(CleanUtil.cleanString(logPath)));
			OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
			byte[] buffer = new byte[in.available()];
			in.read(buffer);
			// 设置response的Header
			response.addHeader("Content-Disposition", "attachment;filename=" + CleanUtil.cleanString(new String(fileName.getBytes())));
			response.setContentType("application/octet-stream");
			out.write(buffer);
			out.flush();
		} catch (IOException ex) {
			logger.info("下载失败");
		}
	}

	@GetMapping(value = "/collect/agent")
	@ApiOperation(value = "采集代理模块查询")
	public VData collectAgent() {
		Map<String,Object> resMap = new HashMap<>();
		resMap.put("name", "collectAgent");
		resMap.put("status", "-1");
		String cmd = "lsof -i:7838";
		logger.info("将要执行命令：" + cmd);
		String result = CmdExecute.executeCmd(cmd);
		if (StringUtils.isEmpty(result)) {
			return vData(resMap);
		}
		String[] strings = result.split("\r\n");
		if (strings.length > 1 && strings[1].contains("(LISTEN)")) {
			resMap.put("status", Global.OK.getCode());
		}
		return vData(resMap);
	}

	@GetMapping(value = "/data/collect")
	@ApiOperation(value = "采集模块查询")
	public VData dataCollect() {
		Map<String,Object> resMap = new HashMap<>();
		resMap.put("name", "dataCollect");
		resMap.put("status", "-1");
		if (hardwareService.checkService("datacollect")) {
			resMap.put("status", Global.OK.getCode());
		}
		return vData(resMap);
	}

	@GetMapping(value = "/distribution")
	@ApiOperation(value = "分发模块查询")
	public VData distribution() {
		Map<String,Object> resMap = new HashMap<>();
		resMap.put("name", "distribution");
		resMap.put("status", "-1");
		if (hardwareService.checkService("kafka") && hardwareService.checkService("zookeeper")) {
			resMap.put("status", Global.OK.getCode());
		}
		return vData(resMap);
	}

	@GetMapping(value = "/data/clean")
	@ApiOperation(value = "数据清洗模块查询")
	public VData dataClean() {
		Map<String,Object> resMap = new HashMap<>();
		resMap.put("name", "dataClean");
		resMap.put("status", "-1");
		if (hardwareService.checkService("flume")) {
			resMap.put("status", Global.OK.getCode());
		}
		return vData(resMap);
	}
}
