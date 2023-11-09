package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.enums.InternetTypeNum;
import com.vrv.vap.alarmdeal.business.appsys.model.InternetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.repository.InternetInfoManageRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.InternetInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.ProtectionLevelService;
import com.vrv.vap.alarmdeal.business.appsys.vo.InternetInfoManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.NetInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author lps 2021/8/10
 */

@Service
@Transactional
public class InternetInfoManageServiceImpl extends AbstractBaseServiceImpl<InternetInfoManage,Integer> implements InternetInfoManageService {
    private static Logger logger = LoggerFactory.getLogger(InternetInfoManageServiceImpl.class);
    @Autowired
    private InternetInfoManageRepository internetInfoManageRepository;

    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ProtectionLevelService protectionLevelService;
    @Autowired
    private ClassifiedLevelService classifiedLevelService;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private List<BaseDictAll> protections =null;
    private List<BaseDictAll> classifiedLevels = null;
    private List<String> internetNames = null;
    @Override
    public InternetInfoManageRepository getRepository(){
        return internetInfoManageRepository;
    }


    @Override
    public PageRes<InternetInfoManage> getInternetInfoManagePage(InternetInfoManageVo internetInfoManageVo){
        List<QueryCondition> queryConditions=new ArrayList<>();
        String internetName=internetInfoManageVo.getInternetName();
        String internetType=internetInfoManageVo.getInternetType();
        String protectLevel=internetInfoManageVo.getProtectLevel();
        String secretLevel=internetInfoManageVo.getSecretLevel();
        if(StringUtils.isNotBlank(internetName)){
            queryConditions.add(QueryCondition.like("internetName",internetName));
        }
        if(StringUtils.isNotBlank(internetType)){
            queryConditions.add(QueryCondition.eq("internetType",internetType));
        }
        if(StringUtils.isNotBlank(protectLevel)){
            queryConditions.add(QueryCondition.eq("protectLevel",protectLevel));
        }
        if(StringUtils.isNotBlank(secretLevel)){
            queryConditions.add(QueryCondition.eq("secretLevel",secretLevel));
        }
        PageReq pager = mapperUtil.map(internetInfoManageVo, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        Page<InternetInfoManage> page=findAll(queryConditions,pager.getPageable());
        return PageRes.toRes(page);

    }

    /**
     * 导入数据校验
     * @param file
     * @return
     */
    @Override
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file) {
        logger.info("互联信息导入数据校验开始");
        HSSFSheet sheet= null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheet(InternetInfoManageVo.INTERNET_INFO_MANAGE);
        } catch (IOException e) {
            logger.error("IOException: {}", e);
            return null;
        }
        if(null == sheet){
            logger.error("导入数据为空，当前文件sheet,"+ InternetInfoManageVo.INTERNET_INFO_MANAGE+"不存在");
            throw new AlarmDealException(-1,"当前sheet页不存在,"+InternetInfoManageVo.INTERNET_INFO_MANAGE);
        }
        // 初始化数据
        initData();
        // 获取excel数据
        List<List<String>> excelContent = ImportExcelUtil.getExcelContent(sheet);
        // 数据组装
        List<Map<String,Object>> datas = getAssembleData(excelContent);
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



    // 初始化数据
    private void initData() {
        protections =protectionLevelService.getInternetAll();
        classifiedLevels = classifiedLevelService.getInternetAll();
        internetNames = getInternetNames();
    }

    // 数据组装
    private List<Map<String, Object>> getAssembleData(List<List<String>> excelContent) {
        String[] keys = InternetInfoManageVo.KEYS; //
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


    /**
     * 互联单位导入重复校验
     * @param dataList
     * @return
     */
    protected  List<Map<String, Object>> repeatHandle(List<Map<String, Object>> dataList) {
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        if(null == dataList || dataList.size() == 0){
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        for(Map<String, Object> data: dataList){
            Object internetNameObj = data.get("internetName");
            if(org.springframework.util.StringUtils.isEmpty(internetNameObj)){
                continue;
            }
            String internetName = String.valueOf(internetNameObj);
            if(appNos.contains(internetName)){
                data.put("reason","导入数据中互联单位重复");
                repeatDatas.add(data);
            }else{
                appNos.add(internetName);
            }
        }
        if(repeatDatas.size() >0){
            dataList.removeAll(repeatDatas);
        }
        return repeatDatas;
    }
    // 数据校验
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
     * 校验 :"internetName","internetType","secretLevel","protectLevel"
     * "互联单位", "接入方式", "涉密等级", "防护等级"
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
     * 校验 :"internetName","internetType","secretLevel","protectLevel"
     "  互联单位", "接入方式", "涉密等级", "防护等级"
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "internetName":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("互联单位:"+value+"不能为空");
                }
                break;
            case "internetType":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("接入方式:"+value+"不能为空");
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
     * 校验 :"internetName","internetType","secretLevel","protectLevel"
     * 互联单位", "接入方式", "涉密等级", "防护等级"
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> validateValidity(String key, String value,Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "internetName":
                if(isExistInternetName(value)){
                    return returnEroorResult("互联单位名称:"+value+"重复");
                }
                break;
            case "internetType":
                String netType = InternetTypeNum.getCodeByName(value);
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
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 互联单位名称是不是存在
     * @param value
     * @return
     */
    private boolean isExistInternetName(String value) {
        if(CollectionUtils.isEmpty(internetNames)){
            return false;
        }
        if(internetNames.contains(value)){
            return true;
        }
        return false;
    }

    private List<String> getInternetNames() {
        String sql="select internet_name from internet_info_manage;";
        return jdbcTemplate.queryForList(sql,String.class);
    }

    public String getCodeByValue(String value,List<BaseDictAll> datas) {
        for(BaseDictAll data : datas){
            if(value.equalsIgnoreCase(data.getCodeValue())){
                return data.getCode();
            }
        }
        return null;
    }


    @Override
    protected void dataChangeSendMsg() {
        messageService.sendKafkaMsg("internet");
    }
}
