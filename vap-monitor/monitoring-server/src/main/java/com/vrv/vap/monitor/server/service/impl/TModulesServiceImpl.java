package com.vrv.vap.monitor.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.monitor.server.common.util.CleanUtil;
import com.vrv.vap.monitor.server.common.util.HttpRequestUtil;
import com.vrv.vap.monitor.server.mapper.TModulesMapper;
import com.vrv.vap.monitor.server.model.TModules;
import com.vrv.vap.monitor.server.service.TModulesService;
import com.vrv.vap.monitor.server.vo.TModulesVO;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TModulesServiceImpl extends BaseServiceImpl<TModules> implements TModulesService {

    private static Logger logger = LoggerFactory.getLogger(TModulesServiceImpl.class);

    @Resource
    private TModulesMapper tModulesMapper;
    //nacos地址
    @Value("${SERVER_ADDR}")
    private String nacosUrl;
    //nacos命名空间
    @Value("${NAMESPACE}")
    private String nacosNameSpace;



    @Override
    public List<TModulesVO> getTmodules() {
        List<TModulesVO> tModulesVoList = new ArrayList<>();
        List<TModules> tModules =  this.findAll();
        if(tModules==null ) {
            return tModulesVoList;
        }
        tModules = tModules.stream().filter(p->"jar".equals(p.getModuleType())||"tool".equals(p.getModuleType())).collect(Collectors.toList());
        //获取时间最新的版本
        List<String> allServiceName = tModules.stream().map(p->p.getModuleName()).distinct().collect(Collectors.toList());
        final  List<TModules>  tm = tModules;
        List<TModules> maxTmodules = new ArrayList<>();
        allServiceName.stream().forEach(p->{
          Optional<TModules> optionalModules = tm.stream().filter(f->p.equals(f.getModuleName())).max((m, n)->m.getCreateTime().compareTo(n.getCreateTime()));
          if(optionalModules.isPresent()){
              maxTmodules.add(optionalModules.get());
          }
        });
        tModules = maxTmodules;
        if(tModules.size() == 0 ) {
            return tModulesVoList;
        }
        List<String> serverList = getServerList();
        List<Map<String,Object>> mapList = getServerListStatus(serverList);
        tModulesVoList = initVO(tModules,mapList);
        return tModulesVoList;
    }

    @Override
    public String executeLinuxCmd(String cmd) {
        System.out.println("got cmd job : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
            Process process = run.exec(CleanUtil.cleanString(cmd));
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            // System.out.println("[check] now size \n"+bs.readLine());
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            System.out.println("job result [" + out.toString() + "]");
            in.close();
            // process.waitFor();
            process.destroy();
            return out.toString();
        } catch (IOException e) {
            logger.error("",e);
        }
        return null;
    }


    @Override
    public String executeLinuxCmd2(String [] cmd) {
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            InputStream in = process.getInputStream();
            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            // System.out.println("[check] now size \n"+bs.readLine());
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1;) {
                out.append(new String(b, 0, n));
            }
            System.out.println("job result [" + out.toString() + "]");
            in.close();
            // process.waitFor();
            process.destroy();
            return out.toString();
        } catch (Exception e) {
            logger.error("",e);
        }
        return null;
    }


    private List<TModulesVO> initVO(List<TModules> tModules, List<Map<String, Object>> mapList) {
        List<TModulesVO> tModulesVoList = new ArrayList<>();
        for (TModules tmodule : tModules) {
            List<Map<String, Object>> moduelInfoList = mapList.stream().filter(p -> tmodule.getModuleName().equals((String) p.get("serviceName"))).collect(Collectors.toList());
            List<TModulesVO> tvoList = new ArrayList<>();
            dealModuelInfo(moduelInfoList, tvoList);
            if (tvoList.size() == 0) {
                TModulesVO tModulesVO = new TModulesVO();
                tModulesVO.setModuleInstancesStatus(0);
                tvoList.add(tModulesVO);
            }
            tvoList.stream().forEach(p ->
            {
                p.setId(tmodule.getId());
                p.setModuleName(tmodule.getModuleName());
                p.setModuleDesc(tmodule.getModuleDesc());
                p.setModuleType(tmodule.getModuleType());
                p.setModuleOriginal(tmodule.getModuleOriginal());
                p.setModuleVersion(tmodule.getModuleVersion());
                p.setModuleInstancesNumber(tvoList.size() > 0 ? tvoList.size() : 1);
                p.setCreateTime(tmodule.getCreateTime());
            });
            tModulesVoList.addAll(tvoList);
        }
        return tModulesVoList;

    }

    private void dealModuelInfo(List<Map<String, Object>> moduelInfoList, List<TModulesVO> tvoList) {
        for(Map serverStatusMap :  moduelInfoList){
            TModulesVO tModulesVO = new TModulesVO();
            if(serverStatusMap.containsKey("ip")){
                tModulesVO.setModuleInstancesIp((String)serverStatusMap.get("ip"));
            }
            if(serverStatusMap.containsKey("port")){
                tModulesVO.setModuleInstancesPort((Integer)serverStatusMap.get("port"));
            }
            if(serverStatusMap.containsKey("healthy")){
                tModulesVO.setModuleInstancesStatus((Boolean)serverStatusMap.get("healthy")?1:0);
            }
            tvoList.add(tModulesVO);
        }
    }

    private List<Map<String,Object>> getServerListStatus(List<String> serverList){
        //获取服务实例API
        String getInstanceApi = "/nacos/v1/ns/instance/list";
        String instanceUrl = "http://"+nacosUrl+getInstanceApi;
        List<Map<String,Object>> serverStatusList = new ArrayList<>();
        for(String serverName:serverList) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("serviceName=");
            stringBuilder.append(serverName);
            stringBuilder.append("&namespaceId=");
            stringBuilder.append(nacosNameSpace);
            String responseString =  HttpRequestUtil.sendGet(instanceUrl,stringBuilder.toString());
            if (StringUtils.isEmpty(responseString)) {
               continue;
            }
            Map<String, Object> map = jsonToMap(responseString);
            if (map == null) {
                continue;
            }
            if (!map.containsKey("hosts")) {
                continue;
            }
            List<Map<String,Object>> list = (List) map.get("hosts");
            serverStatusList.addAll(list);
        }
        return serverStatusList;

    }

    private List<String> getServerList() {
        //获取服务列表、、
        //获取服务实例API
        String getServerStatusApi = "/nacos/v1/ns/service/list";
        String instanceUrl = "http://" + nacosUrl + getServerStatusApi;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("pageNo=1&pageSize=1000");
        stringBuilder.append("&namespaceId=");
        stringBuilder.append(nacosNameSpace);
        String responseString = HttpRequestUtil.sendGet(instanceUrl, stringBuilder.toString());
        List<String> serverList = new ArrayList<>();
        if (StringUtils.isEmpty(responseString)) {
            return serverList;
        }
        Map<String, Object> map = jsonToMap(responseString);
        if (map == null) {
            return serverList;
        }
        if (!map.containsKey("doms")) {
            return serverList;
        }
        List<String> list = (List) map.get("doms");
        return list;
    }

    private Map jsonToMap(String source){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(JsonSanitizer.sanitize(source), Map.class);
            return map;
        }catch (IOException ex){
            return null;
        }
    }
}
