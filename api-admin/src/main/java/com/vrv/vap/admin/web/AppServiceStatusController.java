package com.vrv.vap.admin.web;


import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.model.TModules;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.TModulesService;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.vo.HostNetInfVO;
import com.vrv.vap.admin.vo.ServiceManageVO;
import com.vrv.vap.admin.vo.TModulesVO;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;


@RequestMapping(path = "/service")
@Api(hidden = false)
@RestController
public class AppServiceStatusController extends ApiController {
    private static final String MODULE_NAME = "moduleName";
    @Autowired
    private TModulesService tModulesService;

    @Autowired
    private SystemConfigService systemConfigService;

    private static String NETCONFIGPATH = "/etc/sysconfig/network-scripts/";

    public static final String restartShellName = "vap_restart.sh";

    /**
     * 服务一键重启
     */
    @ApiOperation(value = "服务一键重启", hidden = false)
    @GetMapping("/restart")
    public Result restartAllService() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Global.SESSION.USER);
        String restartShellPath = System.getProperty("java.class.path");
        int lastIndex = restartShellPath.lastIndexOf(File.separator) + 1;
        restartShellPath = restartShellPath.substring(0, lastIndex);
        if (Const.USER_ADMIN.equals(user.getAccount())) {
            String cmd1 = "cd " + restartShellPath;
            String cmd2 = "sh " + restartShellName;
            String cmd3 = cmd1 + ";" + cmd2;
            String[] cmd = new String[]{"/bin/sh", "-c", CleanUtil.cleanString(cmd3)};
            String result = tModulesService.executeLinuxCmd2(cmd);
            return this.vData(result);
        }
        return  new Result("-1","该用户没有一键启动权限");
    }



    /**
     * 查询应用服务状态
     */
    @ApiOperation(value = "查询应用服务状态", hidden = false)
    @GetMapping
    public VList<TModulesVO> appServiceStatus() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Global.SESSION.USER);
        List<TModulesVO> list = new ArrayList<>();
        if (Const.USER_ADMIN.equals(user.getAccount())){
            list = tModulesService.getTmodules();
        }
        return this.vList(list,list.size());
    }

    /**
     * 查询单独服务的版本变更信息
     */
    @ApiOperation(value = "查询单独服务的版本变更信息", hidden = false)
    @PostMapping("/history")
    public VData<List<TModules>> appServiceHistory(@RequestBody TModules tModules) {
        Example example = new Example(TModules.class);
        example.createCriteria().andEqualTo(MODULE_NAME,tModules.getModuleName());
        example.setOrderByClause("create_time desc");
        List<TModules> list = tModulesService.findByExample(example);
        return this.vData(list);
    }


    /**
     * 查询大版本号信息
     */
    @ApiOperation(value = "查询大版本号信息", hidden = false)
    @GetMapping("/big_version")
    public VData<TModules> getBigVersionInfo() {
        Example example = new Example(TModules.class);
        example.createCriteria().andEqualTo(MODULE_NAME,"big_version");
        example.setOrderByClause("create_time desc");
        List<TModules> list = tModulesService.findByExample(example);
        TModules tModule = list.get(0);
        return this.vData(tModule);
    }


    /**
     * 查询大版本号的最新服务信息
     */
    /**
     * 查询大版本号信息
     */
    @ApiOperation(value = "查询大版本号以及对应服务的历史记录", hidden = false)
    @GetMapping("/big_version/include")
    public VData<List<Map<String,Object>>> getBigVersionIncludeService(HttpServletRequest httpServletRequest) {
//        Example example = new Example(TModules.class);
//        example.createCriteria().andEqualTo("moduleName","big_version");
//        example.setOrderByClause("create_time desc");
//        List<TModules> bigVersions = tModulesService.findByExample(example);
//        List<Map<String,Object>> resultList = new ArrayList<>();
//        for(TModules bigVersion : bigVersions){
//            Map<String,Object> result = new HashMap<>();
//            result.put("bigVsersion",bigVersion);
//            String moduleVersion = bigVersion.getModuleVersion();
//            Example example1 = new Example(TModules.class);
//            example1.createCriteria().andEqualTo("currentBigVersion",moduleVersion);
//            List<TModules> includeServices = tModulesService.findByExample(example1);
//            result.put("bigVsersion",bigVersion);
//            result.put("includeServices",includeServices);
//            resultList.add(result);
//        }
        StringBuilder stringBuilder = new StringBuilder();
        try {
            List<HostNetInfVO>  hostNetInfVOList = getNetworkInfInfo();
            if(hostNetInfVOList!=null){
               Optional<HostNetInfVO> optional =  hostNetInfVOList.stream().filter(p->"true".equals(p.getFlag())).findFirst();
               if(optional.isPresent()){
                   HostNetInfVO hostNetInfVO =  optional.get();
                   stringBuilder.append(hostNetInfVO.getIp());
               }
            }
            if(StringUtils.isEmpty(stringBuilder.toString())) {
                //InetAddress inetAddress = getLocalHostLanAddress();
                //stringBuilder.append(inetAddress.getHostAddress());
                HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
                stringBuilder.append(IPUtils.getIpAddress(request));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<Map<String,Object>> rList = new ArrayList<>();
        List<TModules>  allModules = tModulesService.findAll();
        List<TModules> bigVersionModules =  allModules.stream().filter(p->"big_version".equals(p.getModuleName())).sorted(Comparator.comparing(TModules::getCreateTime).reversed()).collect(Collectors.toList()) ;
        bigVersionModules.stream().forEach(p->{
            Map<String,Object> r = new HashMap<>();
            r.put(MODULE_NAME,p.getModuleName());
            r.put("createTime",p.getCreateTime());
            r.put("version",p.getModuleVersion());
            r.put("user","admin");
            List<TModules> includeServices = allModules.stream().filter(s->StringUtils.isNotEmpty(s.getCurrentBigVersion())&&s.getCurrentBigVersion().equals(p.getModuleVersion())).collect(Collectors.toList());
            //去重
            includeServices = includeServices.stream().filter(distinctByKey(o -> o.getModuleName() + "|" + o.getModuleOriginal())).collect(Collectors.toList());

            StringBuilder history = new StringBuilder();
            includeServices.forEach(s->{
                TModules preVersionService = null;
                List<TModules>  historySerivces =  allModules.stream().filter(m->StringUtils.isNotEmpty(m.getModuleName())&&m.getModuleName().equals(s.getModuleName())&&m.getCreateTime().compareTo(s.getCreateTime())<0).sorted(Comparator.comparing(TModules::getCreateTime).reversed()).collect(Collectors.toList());
                if(historySerivces!=null&& historySerivces.size()>0){
                    preVersionService = historySerivces.get(0);
                }
                if(preVersionService == null){
                    history.append(s.getModuleName()+"服务初始化,版本："+s.getModuleOriginal()+"\n");
                }
                if(preVersionService != null){
                    history.append(s.getModuleName()+"服务升级："+preVersionService.getModuleOriginal()+"=>"+s.getModuleOriginal()+"\r\n");
                }
            });
            r.put("history",history.toString());
            r.put("ip",stringBuilder.toString());
            rList.add(r);
        });

        return this.vData(rList);
    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
        Map<Object, Boolean> seen = new ConcurrentHashMap<>();
        System.out.println("这个函数将应用到每一个item");
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }


    /**
     * 启动，重启，停止服务
     */
    @ApiOperation(value = "启动，重启，停止服务", hidden = false)
    @PostMapping("/cmd")
    @SysRequestLog(description = "启动，重启，停止服务", actionType = ActionType.AUTO)
    public Result serviceManage(@RequestBody ServiceManageVO serviceManageVO) {
        SyslogSenderUtils.sendSelectSyslog();
        List<TModules> list = tModulesService.findAll();
        long count = list.stream().filter(p->p.getModuleName().equals(serviceManageVO.getServiceName())).count();
        if(count == 0){
            return this.result(ErrorCode.SERVICE_NOT_FOUND);
        }

        if("start".equals(serviceManageVO.getOperType())){
            String cmd = "systemctl start   "+ serviceManageVO.getServiceName();
            String result = tModulesService.executeLinuxCmd(cmd);
            return this.vData(result);
        }
        if("restart".equals(serviceManageVO.getOperType())){
            String cmd = "systemctl restart  "+ serviceManageVO.getServiceName();
            String result = tModulesService.executeLinuxCmd(cmd);
            return this.vData(result);

        }
        if("stop".equals(serviceManageVO.getOperType())){
            String cmd = "systemctl stop  "+ serviceManageVO.getServiceName();
            String result = tModulesService.executeLinuxCmd(cmd);
            return this.vData(result);

        }
        return this.result(ErrorCode.CMD_NOT_FOUND);
    }

    public List<HostNetInfVO> getNetworkInfInfo() throws Exception {
        List<HostNetInfVO> result = new ArrayList<HostNetInfVO>();
        File nfile = new File(NETCONFIGPATH);
        String[] files = nfile.list();

        for (String file : files) {
            if (file.startsWith("ifcfg-") && !"ifcfg-lo".equals(file)) {
                String infName = file.substring(file.indexOf("-") + 1);
                String filePath = NETCONFIGPATH + File.separator + file;
                HostNetInfVO vo = readFile2VO(filePath);
                if (infName.equals(vo.getName())) {
                    vo.setFlag("false");
                    if (StringUtils.isBlank(vo.getDefroute())) {
                        vo.setDefroute("no");
                    }
                    result.add(vo);
                }
            }
        }

        Object userconfig = systemConfigService.getStaticConfig("com.vrv.soc.ip");
        if(userconfig != null){
            String mainIP = userconfig.toString();
            for (HostNetInfVO vo : result){
                String ip = vo.getIp();
                if(mainIP.equals(ip)){
                    vo.setFlag("true");
                }else{
                    vo.setFlag("false");
                }
            }
        }else {

            // 使用ArrayList来存储每行读取到的字符串
            ArrayList<String> arrayList = new ArrayList<>();
            try {
                FileReader fr = new FileReader("/etc/profile");
                BufferedReader bf = new BufferedReader(fr);
                String str;
                // 按行读取字符串
                while ((str = bf.readLine()) != null) {
                    arrayList.add(str);
                }
                bf.close();
                fr.close();
            } catch (IOException e) {
                e.printStackTrace();

            }

            HostNetInfVO hostNetInfVO =null;

            for(String line : arrayList) {

                if(line.contains("export LOCAL_SERVER_IP=")) {

                    for (HostNetInfVO vo : result) {
                        String ip = vo.getIp();
                        if(org.apache.commons.lang3.StringUtils.isNotBlank(ip)&&line.split("=").length==2&&line.split("=")[1].trim().equals(ip.trim())) {
                            hostNetInfVO = vo;
                            break;
                        }
                    }
                    break;
                }
            }


            if (hostNetInfVO == null) {
                if (result.size() == 1) {
                    hostNetInfVO = result.get(0);
                } else if (result.size() > 1) {
                    for (HostNetInfVO vo : result) {

                        String ip = vo.getIp();
                        if (org.apache.commons.lang3.StringUtils.isNotEmpty(ip) && hostNetInfVO == null) {
                            hostNetInfVO = vo;
                            break;
                        }
                    }
                }
            }
            if(hostNetInfVO!=null) {
                hostNetInfVO.setFlag("true");
            }

        }


        return result;
    }

    private HostNetInfVO readFile2VO(String file) throws Exception {
        HostNetInfVO vo = new HostNetInfVO();

        BufferedReader reader = null;
        FileInputStream fileInputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(
                fileInputStream, "UTF-8");
        try {
            reader = new BufferedReader(inputStreamReader);
            // 每次读取文件的缓存
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                if (temp.indexOf("DEVICE") >= 0) {
                    String name = temp.substring(temp.indexOf("=") + 1).trim().replace("\"", "");
                    vo.setName(name);
                } else if (temp.indexOf("IPADDR") >= 0) {
                    String ip = temp.substring(temp.indexOf("=") + 1).trim();
                    vo.setIp(ip.replace("\"", ""));
                } else if (temp.indexOf("PREFIX") >= 0) {
                    String submask;
                    String prefix = temp.substring(temp.indexOf("=") + 1).trim();
                    if ("24".equals(prefix)) {
                        submask = "255.255.255.0";
                    } else if ("16".equals(prefix)) {
                        submask = "255.255.0.0";
                    } else {
                        submask = "255.255.255.0";
                    }
                    vo.setSubmask(submask);
                } else if (temp.indexOf("HWADDR") >= 0) {
                    String mac = temp.substring(temp.indexOf("=") + 1).trim();
                    if (mac.contains("\"")) {
                        mac = mac.replace("\"", "");
                    }
                    vo.setMac(mac);
                } else if (temp.indexOf("DEFROUTE") >= 0) {
                    String route = temp.substring(temp.indexOf("=") + 1).trim();
                    if (route.contains("\"")) {
                        route = route.replace("\"", "");
                    }
                    vo.setDefroute(route);
                } else if (temp.indexOf("GATEWAY") >= 0) {
                    String gateway = temp.substring(temp.indexOf("=") + 1).trim();
                    if (gateway.contains("\"")) {
                        gateway = gateway.replace("\"", "");
                    }
                    vo.setGateway(gateway);
                } else if (temp.indexOf("DNS1") >= 0) {
                    String dns1 = temp.substring(temp.indexOf("=") + 1).trim();
                    if (dns1.contains("\"")) {
                        dns1 = dns1.replace("\"", "");
                    }
                    vo.setDns1(dns1);
                } else if (temp.indexOf("DNS2") >= 0) {
                    String dns2 = temp.substring(temp.indexOf("=") + 1).trim();
                    if (dns2.contains("\"")) {
                        dns2 = dns2.replace("\"", "");
                    }
                    vo.setDns2(dns2);
                }
            }
            // 关闭文件流
            if (reader != null) {
                reader.close();
            }
        }catch (Exception e) {
            // TODO: handle exception
            throw e;
        }
        finally {
            inputStreamReader.close();
            fileInputStream.close();
        }
        return vo;
    }

    private static InetAddress getLocalHostLanAddress() throws UnknownHostException {
        try {
            InetAddress candidateAddress = null;
            // 遍历所有的网络接口
            for (Enumeration ifaces = NetworkInterface.getNetworkInterfaces(); ifaces.hasMoreElements();) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                // 在所有的接口下再遍历IP
                for (Enumeration inetAddrs = iface.getInetAddresses(); inetAddrs.hasMoreElements();) {
                    InetAddress inetAddr = (InetAddress) inetAddrs.nextElement();
                    if (!inetAddr.isLoopbackAddress()) {// 排除loopback类型地址
                        if (inetAddr.isSiteLocalAddress()) {
                            // 如果是site-local地址，就是它了
                            return inetAddr;
                        } else if (candidateAddress == null) {
                            // site-local类型的地址未被发现，先记录候选地址
                            candidateAddress = inetAddr;
                        }
                    }
                }
            }
            if (candidateAddress != null) {
                return candidateAddress;
            }
            // 如果没有发现 non-loopback地址.只能用最次选的方案
            InetAddress jdkSuppliedAddress = InetAddress.getLocalHost();
            if (jdkSuppliedAddress == null) {
                throw new UnknownHostException("The JDK InetAddress.getLocalHost() method unexpectedly returned null.");
            }
            return jdkSuppliedAddress;
        } catch (Exception e) {
            UnknownHostException unknownHostException = new UnknownHostException(
                    "Failed to determine LAN address: " + e);
            unknownHostException.initCause(e);
            throw unknownHostException;
        }
    }

}
