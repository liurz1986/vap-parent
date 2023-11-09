package com.vrv.vap.alarmdeal.business.appsys.service.impl;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.dao.DataInfoManageDao;
import com.vrv.vap.alarmdeal.business.appsys.enums.InternetTypeNum;
import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.repository.DataInfoManageRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.DataInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.vo.DataInfoManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.InternetInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
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
import java.util.stream.Collectors;

/**
 * @author lps 2021/8/10
 */

@Service
@Transactional
public class DataInfoManageServiceImpl extends AbstractBaseServiceImpl<DataInfoManage,Integer> implements DataInfoManageService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DataInfoManageServiceImpl.class);
    @Autowired
    private DataInfoManageRepository dataInfoManageRepository;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ClassifiedLevelService classifiedLevelService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataInfoManageDao dataInfoManageDao;

    private List<BaseDictAll> classifiedLevels = null;

    private List<String> dataFlags = null;
    @Override
    public DataInfoManageRepository getRepository(){
        return dataInfoManageRepository;
    }


    @Override
   public PageRes<DataInfoManage> getDataInfoManagePage(DataInfoManageVo dataInfoManageVo){
        List<QueryCondition> queryConditions=new ArrayList<>();
        String businessType=dataInfoManageVo.getBusinessType();
        String secretLevel= dataInfoManageVo.getSecretLevel();
        if(StringUtils.isNotBlank(businessType)){
            queryConditions.add(QueryCondition.like("businessType",businessType));
        }
        if(StringUtils.isNotBlank(secretLevel)){
            queryConditions.add(QueryCondition.eq("secretLevel",secretLevel));
        }
        PageReq pager = mapperUtil.map(dataInfoManageVo, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        Page<DataInfoManage> page=findAll(queryConditions,pager.getPageable());
        return PageRes.toRes(page);

   }
    @Override
    public boolean validateFileSize(String value){
        if(StringUtils.isEmpty(value)){
            return true;
        }
        try{
            Double.parseDouble(value);
        }catch (Exception e){
            LOGGER.error("文件大小格式解析失败",e);
            return false;
        }
        return true;
    }

    @Override
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file) {
        LOGGER.info("数据属性信息导入数据校验开始");
        HSSFSheet sheet= null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheet(DataInfoManageVo.DATA_INFO_MANAGE);
        } catch (IOException e) {
            LOGGER.error("IOException: {}", e);
            return null;
        }
        if(null == sheet){
            LOGGER.error("导入数据为空，当前文件sheet,"+ DataInfoManageVo.DATA_INFO_MANAGE+"不存在");
            throw new AlarmDealException(-1,"当前sheet页不存在,"+DataInfoManageVo.DATA_INFO_MANAGE);
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



    private void initData() {
        classifiedLevels = classifiedLevelService.getDataInfoAll();
        dataFlags = getAllDataFlags();
    }
    // 数据组装
    private List<Map<String, Object>> getAssembleData(List<List<String>> excelContent) {
        String[] keys = DataInfoManageVo.KEYS; //
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
     * 数据标识导入重复校验
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
            Object dataFlagObj = data.get("dataFlag");
            if(org.springframework.util.StringUtils.isEmpty(dataFlagObj)){
                continue;
            }
            String dataFlag = String.valueOf(dataFlagObj);
            if(appNos.contains(dataFlag)){
                data.put("reason","导入数据中数据标识重复:"+dataFlag);
                repeatDatas.add(data);
            }else{
                appNos.add(dataFlag);
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
     * "dataFlag","businessType","secretLevel","fileName","fileType","fileSize","fileStatus"
     * "数据标识", "业务类型", "涉密等级","文件名称","文件类型","文件大小(MB)","文件管理状态"
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
     * 必填：dataFlag、businessType、secretLevel
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "dataFlag":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("数据标识:"+value+"不能为空");
                }
                break;
            case "businessType":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("业务类型:"+value+"不能为空");
                }
                break;
            case "secretLevel":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("涉密等级:"+value+"不能为空");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 有效性：secretLevel、fileSize
     * @param key
     * @param value
     * @param map
     * @return
     */
    private Map<String, String> validateValidity(String key, String value,Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "dataFlag":
                if(dataFlagExist(value.trim())){
                    return returnEroorResult("数据标识:"+value+"重复");
                }
                break;
            case "secretLevel":
                String code = getCodeByValue(value.trim(),classifiedLevels);
                if(StringUtils.isBlank(code)){
                    return returnEroorResult("涉密等级:"+value+"不存在");
                }else{
                    map.put(key,code); // 执行转换
                }
                break;
            case "fileSize":
                if(!validateFileSize(value)){
                    return returnEroorResult("文件大小:"+value+"格式不符合");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    private boolean dataFlagExist(String value) {
        if(CollectionUtils.isEmpty(dataFlags)){
            return false;
        }
        if(dataFlags.contains(value)){
            return true;
        }
        return false;
    }

    private List<String> getAllDataFlags() {
        String sql="select data_flag from data_info_manage";
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
    public void dataChangeSendMsg() {
        messageService.sendKafkaMsg("file");
    }
    /**
     * 数据信息查询(文件查询)---审批类型功能
     * 查询所有文件名称、数据标识
     * @date 2023-08
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> getFilesAuth() {
        return ResultUtil.successList(dataInfoManageDao.getFilesAuth());
    }
}
