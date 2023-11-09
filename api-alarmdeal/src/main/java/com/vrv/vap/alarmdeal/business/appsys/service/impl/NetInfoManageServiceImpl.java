package com.vrv.vap.alarmdeal.business.appsys.service.impl;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.enums.NetTypeNum;
import com.vrv.vap.alarmdeal.business.appsys.model.NetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.repository.NetInfoManageRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.NetInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.ProtectionLevelService;
import com.vrv.vap.alarmdeal.business.appsys.vo.NetInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author lps 2021/8/10
 */

@Service
@Transactional
public class NetInfoManageServiceImpl extends AbstractBaseServiceImpl<NetInfoManage,Integer> implements NetInfoManageService {
    private static Logger logger = LoggerFactory.getLogger(NetInfoManageServiceImpl.class);
    @Autowired
    private NetInfoManageRepository netInfoManageRepository;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ProtectionLevelService protectionLevelService;

    @Autowired
    private ClassifiedLevelService classifiedLevelService;

    @Autowired
    private AssetBaseDataService assetBaseDataService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<BaseDictAll> protections =null;
    private List<BaseDictAll> classifiedLevels = null;
    private List<BaseSecurityDomain> allDomains = null;
    private List<String> netNames = null;

    @Override
    public  NetInfoManageRepository getRepository(){
        return netInfoManageRepository;
    }


    @Override
    public PageRes<NetInfoManage> getNetInfoManagePage(@RequestBody NetInfoManageVo netInfoManageVo){
        List<QueryCondition> queryConditionList=new ArrayList<>();
        String netName=netInfoManageVo.getNetName();
        String netType=netInfoManageVo.getNetType();
        String protectLevel=netInfoManageVo.getProtectLevel();
        String secretLevel=netInfoManageVo.getSecretLevel();
        if(StringUtils.isNotBlank(netName)){
            queryConditionList.add(QueryCondition.like("netName",netName));
        }
        if(StringUtils.isNotBlank(netType)){
            queryConditionList.add(QueryCondition.eq("netType",netType));
        }
        if(StringUtils.isNotBlank(protectLevel)){
            queryConditionList.add(QueryCondition.eq("protectLevel",protectLevel));
        }
        if(StringUtils.isNotBlank(secretLevel)){
            queryConditionList.add(QueryCondition.eq("secretLevel",secretLevel));
        }
        PageReq pager = mapperUtil.map(netInfoManageVo, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        Page<NetInfoManage> page=findAll(queryConditionList,pager.getPageable());
        return PageRes.toRes(page);


    }

    @Override
    protected void dataChangeSendMsg() {
        messageService.sendKafkaMsg("net");
    }

    /**
     * 导入数据校验：重构
     *
     * 网络名称", "网络类型", "涉密等级","安全域", "防护等级
     * 2022-09-23
     * @param file
     * @return
     */
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file){
        HSSFSheet sheet= null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheet(NetInfoManageVo.NET_INFO_MANAGE);
        } catch (IOException e) {
            logger.error("IOException: {}", e);
            return null;
        }
        if(null == sheet){
            logger.error("导入数据为空，当前文件sheet,"+NetInfoManageVo.NET_INFO_MANAGE+"不存在");
            throw new AlarmDealException(-1,"当前sheet页不存在,"+NetInfoManageVo.NET_INFO_MANAGE);
        }
        // 初始化数据
        initData();
        // 获取excel数据
        List<List<String>> excelContent = ImportExcelUtil.getExcelContent(sheet);
        // 数据组装
        List<Map<String,Object>> datas = getNetInfoData(excelContent);
        // 数据去重处理
        List<Map<String, Object>> repeatDatas = repeatHandle(datas);
        // 数据校验
        Map<String,List<Map<String,Object>>> result=checkData(datas);
        // 存在重复校验数据，将结果加入最终校验结果中
        if(null != repeatDatas){
            List<Map<String, Object>> failDatas = result.get("false");
            failDatas.addAll(repeatDatas);
            result.put("false",failDatas);
        }
        return result;
    }

    /**
     * 数据校验
     * @param datas
     * @return
     */
    private Map<String, List<Map<String, Object>>> checkData(List<Map<String, Object>> datas) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        List<Map<String,Object>> trueList=new ArrayList<>();
        List<Map<String,Object>> falseList=new ArrayList<>();
        for(Map<String,Object> map : datas){
            if(!checkColumn(map)){
                falseList.add(map);
            }else{
                trueList.add(map);
            }
        }
        result.put("true",trueList);
        result.put("false",falseList);
        return result;
    }

    /**
     * 校验 :"netName","netType","secretLevel","domain","protectLevel"
     * 网络名称", "网络类型", "涉密等级","安全域", "防护等级
     * @param map
     * @return
     */
    private boolean checkColumn(Map<String, Object> map) {
        Set<String> keys= map.keySet();
        for(String key : keys){
            if(!validateData(key,map)){
                return false;
            }
        }
        return true;

    }

    /**
     * 校验
     * @param key
     * @param map
     * @return
     */
    private boolean validateData(String key, Map<String, Object> map) {
        String value = map.get(key)==null?"":String.valueOf(map.get(key));
        Map<String,String> validateResult = isMust(key,value);
        // 必填校验
        if(!"success".equals(validateResult.get("status"))){
            map.put("reason",validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String,String> validateValidity =validateValidity(key,value,map);
        if(!"success".equals(validateValidity.get("status"))){
            map.put("reason",validateValidity.get("message"));
            return false;
        }
        return true;
    }



    /**
     * 必填校验
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "netName":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("网络名称:"+value+"不能为空");
                }
                break;
            case "netType":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("网络类型:"+value+"不能为空");
                }
                break;
            case "secretLevel":
                // 必填
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("涉密等级:"+value+"不能为空");
                }
                break;
            case "protectLevel":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("防护等级:"+value+"不能为空");
                }
                break;

            case "domain":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("安全域:"+value+"不能为空");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 有效性校验及转换
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> validateValidity(String key, String value,Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "netName":
                if(isExistNetName(value)){
                    return returnEroorResult("网络名称:"+value+"重复");
                }
                break;
            case "netType":
                String netType = NetTypeNum.getCodeByName(value);
                if(StringUtils.isBlank(netType)){
                    return returnEroorResult("网络类型:"+value+"不存在");
                }else{
                    map.put(key,netType); // 执行转换
                }
                break;
            case "secretLevel":
                // 有效性
                String code = getCodeByValue(value.trim(),classifiedLevels);
                if(StringUtils.isBlank(code)){
                    return returnEroorResult("涉密等级:"+value+"不存在");
                }else{
                    map.put(key,code); // 执行转换
                }
                break;
            case "protectLevel":
                String codePro = getCodeByValue(value.trim(),protections);
                if(StringUtils.isBlank(codePro)){
                    return returnEroorResult("防护等级:"+value+"不存在");
                }else{
                    map.put(key,codePro); // 执行转换
                }
                break;
            case "domain":
                if(!isExistDomain(value)){

                    return returnEroorResult("安全域:"+value+"不存在");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }



    public String getCodeByValue(String value,List<BaseDictAll> datas) {
        for(BaseDictAll data : datas){
            if(value.equalsIgnoreCase(data.getCodeValue())){
                return data.getCode();
            }
        }
        return null;
    }
    /**
     * 安全是否存在
     * @param value
     * @return
     */
    private boolean isExistDomain(String value) {
        for(BaseSecurityDomain domain : allDomains){
            if(domain.getDomainName().equals(value)){
                return true;
            }
        }
        return false;
    }

    /**
     * netname
     * @param value
     * @return
     */
    private boolean isExistNetName(String value) {
        if(CollectionUtils.isEmpty(netNames)){
            return false;
        }
        if(netNames.contains(value)){
            return true;
        }
        return false;
    }
    /**
     * 网络名称重复校验
     * @param dataList
     * @return
     */
    private  List<Map<String, Object>> repeatHandle(List<Map<String, Object>> dataList) {
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> removes = new ArrayList<Map<String, Object>>();
        if(null == dataList || dataList.size() == 0){
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        for(Map<String, Object> data: dataList){
            Object netNameObj = data.get("netName");
            if(org.springframework.util.StringUtils.isEmpty(netNameObj)){
                continue;
            }
            String netName = String.valueOf(netNameObj);
            if(appNos.contains(netName)){
                removes.add(data);
                data.put("reason","导入数据中网络名称重复");
                repeatDatas.add(data);
            }else{
                appNos.add(netName);
            }
        }
        if(repeatDatas.size() >0){
            dataList.removeAll(removes);
        }
        return repeatDatas;
    }

    private List<Map<String, Object>> getNetInfoData(List<List<String>> excelContent) {
        String[] keys = NetInfoManageVo.KEYS; //
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        for(List<String> data : excelContent){
            Map<String,Object> map=new HashMap<>();
            for(int i=0;i<keys.length;i++){
                map.put(keys[i],data.get(i));
            }
            dataList.add(map);
        }
       return dataList;
    }

    private void initData() {
         protections =protectionLevelService.getNtworkAll();
         classifiedLevels = classifiedLevelService.getNetWorkAll();
         allDomains = assetBaseDataService.queryAllDomain();
         netNames =getNetNames();
    }

    private List<String> getNetNames() {
        String sql="select net_name from net_info_manage";
        return jdbcTemplate.queryForList(sql,String.class);
    }
}
