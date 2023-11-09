package com.vrv.vap.alarmdeal.frameworks.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EventConfig{

	private static final String DDOSATTACK = "DDOS攻击";
	private static final String SQLREJECT = "sql注入";
	
	//爬虫
	private static final String[]   webCrawler = new String[] { "/safer/datatheft/Webcrawler" };
	
	//跨域脚本
	private static final String[]   crossDomain = new String[] { "/safer/attack/Crossdomain" };
	
	// 恶意文件
	private static final String[] maliciousFiles  = new String[] { "/safer/threatintelligence/maliciousfiles" };
	private static final String[] ddos = new String[] { "/safer/systemdamage/flowturndown" };
	//信息探测
	private static final String[] scandetect = new String[] {
										"/safer/scandetect/portscan",
										"/safer/scandetect/InformationPenetration",
										"/safer/scandetect/hostscan"
								};
	//信息篡改
	private static final String[] informationtampering = new String[] { "/safer/business/Informationtampering" };
	
	
	//sql注入
	private static final String[] sql = new String[] { "/safer/threatintelligence/sql" };
	//木马病毒
	private static final String[] trojan = new String[] { "/safer/harmfulprogram/Trojan" };
	private static final String[] intrusionattack = new String[] { "/safer/attack/Intrusionattack" };
	private static final String[] injection = new String[] { "/safer/threatintelligence/Injection" };
	private static final String[] violentcracking = new String[] { "/safer/violentcracking/sshorder", "/safer/violentcracking/sshorder",
			"/safer/violentcracking/telnetorder", "/safer/violentcracking/ftporder",
			"/safer/violentcracking/networkdevicepasswordguess", "/safer/violentcracking/3389passwordguess",
			"/safer/violentcracking/blastingipcsharing" };
	static String[] cvevulnerability = new String[] { "/safer/threatintelligence/cvevulnerability" };
	static String[] other = new String[] { "/safer/other" };

	public static final Map<String, String[]> eventConfigArr = new HashMap<>();
	
	public static final  Map<String, List<String> > eventConfigList = new HashMap<>();
	
	public static final Map<String, String> eventNames = new HashMap<>();
	

	public static Map<String, String[]> eventConfigArr(){
		Map<String, String[]> eventConfigArr = new HashMap<>();
		eventConfigArr.put("爬虫", webCrawler);
		eventConfigArr.put("跨域脚本", crossDomain);

		eventConfigArr.put("恶意文件", maliciousFiles);
		eventConfigArr.put(DDOSATTACK, ddos);

		eventConfigArr.put("信息探测", scandetect);// 同内容

		eventConfigArr.put("信息篡改", informationtampering);
		eventConfigArr.put(SQLREJECT, sql);
		eventConfigArr.put("木马病毒", trojan);
		eventConfigArr.put("入侵攻击", intrusionattack);
		eventConfigArr.put("脚本注入", injection);
		eventConfigArr.put("尝试破解", violentcracking);
		eventConfigArr.put("漏洞攻击", cvevulnerability);
		eventConfigArr.put("其他", other);
		return eventConfigArr;
	}

	public static Map<String, List<String>> eventConfigList(){
		Map<String, List<String> > eventConfigList = new HashMap<>();
		eventConfigList.put("爬虫", new ArrayList<>(Arrays.asList(webCrawler)));
		eventConfigList.put("跨域脚本", new ArrayList<>(Arrays.asList(crossDomain)));

		eventConfigList.put("恶意文件", new ArrayList<>(Arrays.asList(maliciousFiles)));
		eventConfigList.put(DDOSATTACK, new ArrayList<>(Arrays.asList(ddos)));

		eventConfigList.put("信息探测", new ArrayList<>(Arrays.asList(scandetect)));// 同内容

		eventConfigList.put("信息篡改", new ArrayList<>(Arrays.asList(informationtampering)));
		eventConfigList.put(SQLREJECT, new ArrayList<>(Arrays.asList(sql)));
		eventConfigList.put("木马病毒", new ArrayList<>(Arrays.asList(trojan)));
		eventConfigList.put("入侵攻击", new ArrayList<String>(Arrays.asList(intrusionattack)));
		eventConfigList.put("脚本注入", new ArrayList<>(Arrays.asList(injection)));
		eventConfigList.put("尝试破解", new ArrayList<>(Arrays.asList(violentcracking)));
		eventConfigList.put("漏洞攻击", new ArrayList<>(Arrays.asList(cvevulnerability)));
		eventConfigList.put("其他", new ArrayList<>(Arrays.asList(other)));
		return eventConfigList;
	}

	public static Map<String, String> eventNames(){
		Map<String, String> eventNames = new HashMap<>();
		eventNames.put("爬虫", "web_crawler");
		eventNames.put("跨域脚本", "cross_domain");

		eventNames.put("恶意文件", "malicious_files");
		eventNames.put(DDOSATTACK, " ddos");

		eventNames.put("信息探测", "scandetect");// 同内容

		eventNames.put("信息篡改", "Informationtampering");
		eventNames.put(SQLREJECT, "sql");
		eventNames.put("木马病毒", "trojan");
		eventNames.put("入侵攻击", "intrusionattack");
		eventNames.put("脚本注入", "injection");
		eventNames.put("尝试破解", "violentcracking");
		eventNames.put("漏洞攻击", "cvevulnerability");
		eventNames.put("其他", "other");
		return eventNames;
	}
}
