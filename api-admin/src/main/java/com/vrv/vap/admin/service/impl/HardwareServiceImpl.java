package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.util.CmdExecute;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.ShellExecuteScript;
import com.vrv.vap.admin.model.TModules;
import com.vrv.vap.admin.service.HardwareService;
import com.vrv.vap.admin.service.TModulesService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.util.FormatUtil;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class HardwareServiceImpl implements HardwareService {
	
	private Logger logger = LoggerFactory.getLogger(HardwareServiceImpl.class);
	
	Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

	@Value("${SERVER_ADDR}")
	private String nacosAddr;

	@Value("${NAMESPACE}")
	private String nacosNameSpace;

	@Autowired
	private TModulesService tModulesService;
	
	@Override
	public LocalHostInfoVO getHostInfoDetail() {
		LocalHostInfoVO vo = new LocalHostInfoVO();
		CpuInfoVO cpuVo = new CpuInfoVO();

		String name = System.getProperty("os.name");
		String ip = System.getenv("LOCAL_SERVER_IP");
		/*String name = System.getProperty("os.name");
		String ip = "127.0.0.1";
		if (name.startsWith("windows")) {
			try {
				//InetAddress address = InetAddress.getLocalHost();
				//ip = address.getHostAddress();
				HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
				ip = IPUtils.getIpAddress(request);
			} catch (Exception e) {
				logger.error("获取ip地址异常",e);
			}
		} else {
			List<String> ipList = getLocalIp();
			if (CollectionUtils.isNotEmpty(ipList)) {
				ip = ipList.get(0);
			}
		}*/

		double cpuRatio = 0;
		try {
			SystemInfo si = new SystemInfo();
			HardwareAbstractionLayer hal = si.getHardware();
			CentralProcessor processor = hal.getProcessor();

			long ut = processor.getSystemUptime();
			String runningTime = formateRunningTime(ut);
			String bootTime = "";
			try {
				bootTime = getBoottime(ut);
			} catch (Exception e) {
				logger.error("获取boot时间异常",e);
			}

			cpuRatio=processor.getSystemCpuLoad()*100;
			String cpuRate = String.format("%.2f", cpuRatio);

			cpuVo.setUsedRate(cpuRate);
			cpuVo.setLogicalProcessorCount(processor.getLogicalProcessorCount());
			cpuVo.setPhysicalProcessorCount(processor.getPhysicalProcessorCount());
			vo.setRunningTime(runningTime);
			vo.setBootTime(bootTime);
		} catch (Exception e) {
			logger.error("获取处理器信息异常",e);
		}
		vo.setSystemName(name);
		vo.setIp(ip);
		RamInfoVO ramVo = this.getRamInfo();
		vo.setRamInfoVo(ramVo);
		vo.setCpuInfoVo(cpuVo);
		vo.setDiskInfoVo(getDiskInfos());

		vo.setSystemTime(new Date());

		return vo;
	}

	private RamInfoVO getRamInfo() {
		RamInfoVO ramInfoVO = new RamInfoVO();

		String executeCmd = CmdExecute.executeCmd("free -m");
		String[] rows = executeCmd.split(System.lineSeparator());
		for (int i = 1; i < rows.length;i++) {
			String row=rows[i];
			String[] cols = row.split(" {1,50}");
			if (cols[0].equals("Swap:")) {
				continue;
			}
			long ramSize = Long.parseLong(cols[1])*1024*1024;
			long usedRam = Long.parseLong(cols[2])*1024*1024;
			if(cols.length>5){
				usedRam = (Long.parseLong(cols[2])+Long.parseLong(cols[5]))*1024*1024;
			}

			double percentAge = 100d * usedRam / ramSize;
			ramInfoVO.setRamSize(ramSize);
			ramInfoVO.setUsedRam(usedRam);
			ramInfoVO.setPercentAge(Math.round(percentAge));
		}
		return ramInfoVO;
	}

	 private List<DiskInfoVO> getDiskInfos() {
	        List<DiskInfoVO> result = new ArrayList<DiskInfoVO>();

			long allSize = 0;
			 
			String executeCmd = CmdExecute.executeCmd("df -k");
			String[] rows = executeCmd.split(System.lineSeparator());
			for(int i=1;i<rows.length;i++) {
//				文件系统                                                    1K-块              已用           可用                已用% 挂载点
//				/dev/mapper/centos-root  52403200 43403044   9000156   83% /
				String row=rows[i];
				String[] cols = row.split(" {1,50}");
				if(cols[0].equals("tmpfs")) {
					continue;
				}
				Long total = Long.parseLong(cols[1])*1024;
				Long used= Long.parseLong(cols[2])*1024;
				Long free= Long.parseLong(cols[3])*1024;
				
//				allSize+=used+free;
				
				 DiskInfoVO vo = new DiskInfoVO();
				 
				 
		            vo.setDiskName(cols[5]);
		            
		            vo.setDiskTotal(total);
		            vo.setDiskTotalCount(FormatUtil.formatBytes(total));
		            
		            vo.setDiskType(cols[0]);
		            
		            vo.setDiskUsed(used);
		            vo.setDiskUsedCount(FormatUtil.formatBytes(used));
		            
		            vo.setDiskFree(free);
		            vo.setDiskFreeCount(FormatUtil.formatBytes(free));
		            
		            
		            vo.setDiskUsedRate(Math.round(100d*used/(used+free)));
		            vo.setDiskFreeRate(100- Math.round(100d*used/(used+free)));
		            
				 result.add(vo);
			}
	        
	        
	        return result;
	 }
 
		private List<String> getLocalIp() {
			List<String> result = new ArrayList<String>();
			Enumeration<NetworkInterface> netInterfaces = null;
			try {
				netInterfaces = NetworkInterface.getNetworkInterfaces();
				while (netInterfaces.hasMoreElements()) {
					NetworkInterface ni = netInterfaces.nextElement();
					if (null != ni.getDisplayName() && ni.isUp()) {
						List<InterfaceAddress> list = ni.getInterfaceAddresses();
						Iterator<InterfaceAddress> it = list.iterator();
						while (it.hasNext()) {
							InterfaceAddress ia = it.next();
							if (null != ia.getBroadcast()) {
								//String ip = ia.getAddress().getHostAddress();
								HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
								String ip = IPUtils.getIpAddress(request);
								if (!"127.0.0.1".equals(ip)) {
									result.add(ip);
								}
							}
						}
					}

				}
			} catch (Exception e) {
				logger.error("获取ip地址异常",e);
			}

			return result;
		}

    /**
     * 把秒转换成汉字时间
     *
     * @param time
     * @return
     */
    protected String formateRunningTime(long time) {
        String result;
        long day, hour, min, sec;
        int mm = 60;
        int hh = 60 * mm;
        int dd = 24 * hh;
        day = time / dd;
        hour = (time - day * dd) / hh;
        min = (time - day * dd - hour * hh) / mm;
        sec = time - day * dd - hour * hh - min * mm;
        if (day != 0) {
            result = String.valueOf(day).concat("天")
                    .concat(String.valueOf(hour)).concat("小时")
                    .concat(String.valueOf(min)).concat("分钟")
                    .concat(String.valueOf(sec).concat("秒"));
        } else if (hour != 0) {
            result = String.valueOf(hour).concat("小时")
                    .concat(String.valueOf(min)).concat("分钟")
                    .concat(String.valueOf(sec)).concat("秒");
        } else if (min != 0) {
            result = String.valueOf(min).concat("分钟")
                    .concat(String.valueOf(sec)).concat("秒");
        } else {
            result = String.valueOf(sec).concat("秒");
        }
        return result;
    }

    private String getBoottime(long ut) throws Exception {
        long bt = System.currentTimeMillis() - ut * 1000;
        Date date = new Date(bt);
        SimpleDateFormat aDate=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return aDate.format(date);
    }

	@Override
    public boolean  checkServiceStatus(String serviceName) {
        String cmd = "systemctl status "+serviceName.toLowerCase();
        logger.info("将要执行命令：" + LogForgingUtil.validLog(cmd));
        //String result = CmdExecute.executeCmd(cmd);
        List<String> queryExecuteCmd = ShellExecuteScript.querySuccessExecuteCmd(cmd);
        for(String result : queryExecuteCmd) {
            if (StringUtils.isNoneEmpty(result)&&(result.contains("PID:")||result.contains("pid:")|| result.contains("active (running)"))) {
                return true;
            }
        }
        return false;
    }
	
	@Override
	public boolean reStartService(String serviceName) {
    	if ("kafka".equals(serviceName)) {
			String cmd = "systemctl restart zookeeper";
			logger.info("将要执行命令：" + LogForgingUtil.validLog(cmd));
			CmdExecute.executeCmd(cmd);
		}
		String cmd = "systemctl restart " + serviceName.toLowerCase();
		logger.info("将要执行命令：" + LogForgingUtil.validLog(cmd));
		CmdExecute.executeCmd(cmd);
		return checkServiceStatus(serviceName);
	}

	@Override
	public List<TModulesVO> getServiceInfo() {
    	List<TModulesVO> serverListVO = new ArrayList<>();
    	String url = "http://" + nacosAddr + "/nacos/v1/ns/instance/list?serviceName=%s&namespaceId=" + nacosNameSpace;

		Example example = new Example(TModules.class);
		example.createCriteria().andEqualTo("moduleType", "jar");
		List<TModules> serverList = tModulesService.findByExample(example);
    	for (TModules module : serverList) {
			TModulesVO tModulesVO = new TModulesVO();
			tModulesVO.setModuleName(module.getModuleName());
			tModulesVO.setModuleDesc(module.getModuleDesc());
			tModulesVO.setModuleInstancesStatus(0);
			try {
				String res = HTTPUtil.GET(String.format(url, module.getModuleName()),null);
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
				List<Map<String, Object>> hosts = (List<Map<String, Object>>) resMap.get("hosts");
				for (Map<String, Object> host : hosts) {
					if ((Boolean) host.get("healthy")) {
						tModulesVO.setModuleInstancesStatus(1);
						break;
					}
				}
			} catch (Exception e) {
				logger.error("服务状态查询异常", e);
			}
			serverListVO.add(tModulesVO);
		}
		return serverListVO;
	}

	@Override
	public boolean checkService(String serviceName) {
		boolean res = false;
		String cmd = "systemctl status " + serviceName;
		if ("collectAgent".equals(serviceName)) {
			cmd = "lsof -i:7838";
		}
		logger.info("将要执行命令：" + cmd);
		String result = CmdExecute.executeCmd(cmd);
		if (org.apache.commons.lang.StringUtils.isEmpty(result)) {
			return res;
		}
		String[] strings = result.split("\r\n");
		if (strings.length > 2 && strings[2].contains("(running)")) {
			res = true;
		}
		if ("collectAgent".equals(serviceName)) {
			if (strings.length > 1 && strings[1].contains("(LISTEN)")) {
				res = true;
			}
		}
		return res;
	}
}
