package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.util.ExcelUtil;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.BasePersonZjgMapper;
import com.vrv.vap.admin.model.BaseKoalOrg;
import com.vrv.vap.admin.model.BasePersonZjg;
import com.vrv.vap.admin.model.FileUpLoadInfo;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BasePersonZjgService;
import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.vo.BasePersonZjgExcel;
import com.vrv.vap.admin.vo.BasePersonZjgQuery;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Created by CodeGenerator on 2021/08/09.
 */
@Service
@Transactional
@Slf4j
public class BasePersonZjgServiceImpl extends BaseServiceImpl<BasePersonZjg> implements BasePersonZjgService {
    @Resource
    private BasePersonZjgMapper basePersonZjgMapper;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Autowired
    private FileUploadInfoService fileUploadInfoService;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    @Autowired
    StringRedisTemplate redisTemplate;

    private static final String CACHE_PERSON_ZJG_KEY = "_BASEINFO:BASE_PERSON_ZJG:ALL";

    @Value("${collector.configPath}")
    private String collectorConfigPath;

    @Override
    public List<Map> queryBasePersonTrend(BasePersonZjgQuery basePersonZjgQuery) {
        List<Map> resultList = new ArrayList<>();
        List<String> monthList = this.getLatest12Month();
        if (CollectionUtils.isNotEmpty(monthList)) {
            for (String month : monthList) {
                Map result = new HashMap();
                basePersonZjgQuery.setMonth(month);
                Map countMap = basePersonZjgMapper.queryBasePersonTrend(basePersonZjgQuery);
                result.put("date",month);
                result.put("count", countMap!= null ? countMap.get("count") : 0);
                resultList.add(result);
            }
        }
       return resultList;
    }

    @Override
    @Transactional
    public void importOrg(List<BasePersonZjgExcel> basePersonZjgs) {
        baseDictAllService.generateDicMap();
        Map<String, Map<String,String>> dicMap = baseDictAllService.getDicValueToCodeMap();

        if(basePersonZjgs.size()>0){
            String personType;
            String secretLevel;
            List<BasePersonZjg> saveList = new ArrayList<>();
            List<BasePersonZjg> updateList = new ArrayList<>();
            for(BasePersonZjgExcel basePersonZjgExcel:basePersonZjgs){
                BasePersonZjg basePersonZjg = new BasePersonZjg();
                BeanUtils.copyProperties(basePersonZjgExcel,basePersonZjg);
                personType = dicMap.get("人员信息-人员类型").get(basePersonZjgExcel.getPersonType());
                secretLevel = dicMap.get("人员信息-人员密级/SM等级").get(basePersonZjgExcel.getSecretLevel());
                if (StringUtils.isNotEmpty(personType)) {
                    basePersonZjg.setPersonType(personType);
                }
                if (StringUtils.isNotEmpty(secretLevel)) {
                    basePersonZjg.setSecretLevel(Integer.parseInt(secretLevel));
                }

                basePersonZjg.setUserIdnEx(basePersonZjg.getUserIdnEx()==null?"":basePersonZjg.getUserIdnEx());
                //如果存在则更新
                List<BasePersonZjg> basePersonZjgList = this.findByProperty(BasePersonZjg.class,"userNo",basePersonZjg.getUserNo());
                if(basePersonZjgList.size()==0){
                    basePersonZjg.setCreateTime(new Date());
                    saveList.add(basePersonZjg);
                    if (saveList.size() % 1000 == 0) {
                        log.info("批量保存开始：" + System.currentTimeMillis());
                        this.save(saveList);
                        log.info("批量保存结束：" + System.currentTimeMillis());
                        saveList.clear();
                    }
                }else {
                    basePersonZjg.setId(basePersonZjgList.get(0).getId());
                    basePersonZjg.setCreateTime(basePersonZjgList.get(0).getCreateTime());
                    updateList.add(basePersonZjg);
                }

            }
            if (CollectionUtils.isNotEmpty(saveList)) {
                log.info("批量保存开始：" + System.currentTimeMillis());
                this.save(saveList);
                log.info("批量保存结束：" + System.currentTimeMillis());
            }
            if (CollectionUtils.isNotEmpty(updateList)) {
                log.info("批量更新开始：" + System.currentTimeMillis());
                updateList.stream().forEach(item -> this.update(item));
                log.info("批量更新结束：" + System.currentTimeMillis());
            }
        }
    }

    @Override
    public String sync() {
        List<BasePersonZjg> basePersonZjgList = findAll();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"table\":\"baseinfo_user\",\"join\":\"std_user_no\",\"add\":[\"std_user_role\",\"std_user_type\"],sep:\",\"}");
        stringBuilder.append("\n");
        stringBuilder.append("std_user_no,std_user_role,std_user_type");
        stringBuilder.append("\n");
        AtomicInteger count = new AtomicInteger(0);
        basePersonZjgList.stream().forEach(basePersonZjg -> {
            stringBuilder.append(basePersonZjg.getUserNo());
            stringBuilder.append(",");
            stringBuilder.append("-1");
            stringBuilder.append(",");
            stringBuilder.append(StringUtils.isNotEmpty(basePersonZjg.getPersonType())?basePersonZjg.getPersonType():"-1");
            stringBuilder.append("\n");
        });


        return stringBuilder.toString();
    }

    @Override
    public void deleteAllPerson() {
        List<BasePersonZjg> personZjgList = findAll();
        if (CollectionUtils.isNotEmpty(personZjgList)) {
            for (BasePersonZjg personZjg : personZjgList) {
                this.deleteById(personZjg.getId());
            }
        }
    }

    @Override
    public void cachePerson() {
        List<BasePersonZjg> personZjgList = this.findAll();
        redisTemplate.opsForValue().set(CACHE_PERSON_ZJG_KEY, JSON.toJSONString(personZjgList));
    }

    @Override
    public Map<String, Object> validateImportPerson(String id) {
        Map<String, Object> result = new HashMap<>();
        List<BasePersonZjgExcel> successList = new ArrayList<>();
        List<BasePersonZjgExcel> errorList = new ArrayList<>();
        result.put("false", errorList);
        result.put("true", successList);
        List<FileUpLoadInfo> fileUpLoadInfoList = fileUploadInfoService.findByProperty(FileUpLoadInfo.class, "fileId", id);
        if (CollectionUtils.isNotEmpty(fileUpLoadInfoList)) {
            FileUpLoadInfo fileUpLoadInfo = fileUpLoadInfoList.get(0);
            if (fileUpLoadInfo.getUploadType() == 0) {
                String filePath = fileUpLoadInfo.getFilePath();
                List<BasePersonZjgExcel> basePersonZjgExcels = null;
                try (InputStream st = new FileInputStream(filePath)) {
                    basePersonZjgExcels = ExcelUtil.importExcel(st, 1, 1, BasePersonZjgExcel.class);
                } catch (Exception e) {
                    log.error("", e);
                }
                if (CollectionUtils.isEmpty(basePersonZjgExcels)) {
                    return result;
                }
                Set<String> set = new HashSet<>();
                List<BaseKoalOrg> orgList = baseKoalOrgService.findAll();
                for (BasePersonZjgExcel basePersonZjgExcel : basePersonZjgExcels) {
                    String reason = "";
                    String code = basePersonZjgExcel.getUserNo();
                    if (!set.contains(code)) {
                        set.add(code);
                    } else {
                        reason += "员工编号重复;";
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getUserNo())) {
                        reason += "员工编号为空;";
                    } else {
                        String singleReg = "^[0-9A-Za-z]{0,64}$";
                        String complexReg = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{0,64}$";
                        if (!code.matches(singleReg) && !code.matches(complexReg)) {
                            reason += "员工编号不合法;";
                        }
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getUserName())) {
                        reason += "姓名为空;";
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getPersonType())) {
                        reason += "用户类型为空;";
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getPersonRank())) {
                        reason += "职务为空;";
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getSecretLevel())) {
                        reason += "保密等级为空;";
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getOrgCode())) {
                        reason += "单位编码为空;";
                    }
                    if (StringUtils.isEmpty(basePersonZjgExcel.getOrgName())) {
                        reason += "单位名称为空;";
                    }
                    //校验组织机构编码是否存在
                    if (StringUtils.isNotEmpty(basePersonZjgExcel.getOrgCode())) {
                        List<BaseKoalOrg> baseKoalOrgs = orgList.stream().filter(p -> (basePersonZjgExcel.getOrgCode() != null && basePersonZjgExcel.getOrgCode().equals(p.getCode()))).collect(Collectors.toList());
                        if (baseKoalOrgs.size() == 0) {
                            reason += "单位编码不存在;";
                        }
                    }
                    if (StringUtils.isNotEmpty(reason)) {
                        basePersonZjgExcel.setReason(reason);
                        errorList.add(basePersonZjgExcel);
                    } else {
                        successList.add(basePersonZjgExcel);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void sendChangeMessage() {
        Map<String,Object> result = new HashMap<>();
        result.put("item","person");
        result.put("time", System.currentTimeMillis());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(result);
            kafkaSenderService.send("vap_base_data_change_message",null,content);
        } catch (Exception e) {
            log.error("",e);
        }
    }



    /**
     * 获取最近12个月
     * @return
     */
    private List<String> getLatest12Month() {
        List<String> dateList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        // 月份最大为11 最小为0 所以设置初始月份时加1
        calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DATE,1);
        for (int i = 0; i < 12; i++) {
            calendar.set(Calendar.MONTH,calendar.get(Calendar.MONTH) - 1);
            // 需要判断月份是否为0，为0则需要转换成12
            Integer month = calendar.get(Calendar.MONTH) == 0 ? 12 : calendar.get(Calendar.MONTH);
            dateList.add(calendar.get(Calendar.YEAR) + "-" + formatMonth(month));
        }
        Collections.sort(dateList);
        return dateList;
    }

    // 月份需要加0 则调用此方法
    private String formatMonth (Integer month){
        if (month < 10) {
            return "0" + month;
        }
        return "" + month;
    }

    /**
     * 获取所有数据--审批类型功能
     * 2023-08
     */
    @Override
    public List<Map> getAllUsersAuth() {
        return basePersonZjgMapper.getAllUsersAuth();
    }
}
