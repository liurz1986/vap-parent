package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.service.ProtectionLevelService;
import com.vrv.vap.alarmdeal.business.appsys.vo.InternetInfoManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.NetInfoManageVo;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 防护等级处理
 *
 * 2022-07-08
 */
@Service
public class ProtectionLevelServiceImpl implements ProtectionLevelService {

    private static Logger logger = LoggerFactory.getLogger(ProtectionLevelServiceImpl.class);
    @Autowired
    private AdminFeign adminFeign;
    //互联
    @Value("${protectionLevel.parentType.internetInfo:f5a4ae5b-3cee-a84f-7471-8f23ezjg1200}")
    private String internetInfoParentType;

    // 网络
    @Value("${protectionLevel.parentType.network:f5a4ae5b-3cee-a84f-7471-8f23ezjg1200}")
    private String networkParentType;



    /**
     * 互联所有
     * @return
     */
    @Override
    public List<BaseDictAll> getInternetAll() {
        List<BaseDictAll> queryDatas = getListData(internetInfoParentType);
        return queryDatas;
    }


    /**
     * 互联所有value的数据
     * @return
     */
    @Override
    public  String[] getInternetAllValues(){
        List<String> codeValues = new ArrayList<>();
        List<BaseDictAll> queryDatas = getListData(internetInfoParentType);
        for(BaseDictAll data : queryDatas){
            if(!codeValues.contains(data.getCodeValue())){
                codeValues.add(data.getCodeValue());
            }
        }
        return codeValues.toArray(new String[codeValues.size()]);
    }


    /////////////////////////////////////// 网路/////////////////////////////////////////////////

    /**
     * 网络所有
     * @return
     */
    @Override
    public List<BaseDictAll> getNtworkAll() {
        List<BaseDictAll> queryDatas = getListData(networkParentType);
        return queryDatas;
    }


    /**
     * 网络所有value的数据
     * @return
     */
    @Override
    public  String[] getNtworkAllValues(){
        List<String> codeValues = new ArrayList<>();
        List<BaseDictAll> queryDatas = getListData(networkParentType);
        for(BaseDictAll data : queryDatas){
            if(!codeValues.contains(data.getCodeValue())){
                codeValues.add(data.getCodeValue());
            }
        }
        return codeValues.toArray(new String[codeValues.size()]);
    }



    /**
     * 查询防护等级
     * @param key
     * @return
     */
    private  List<BaseDictAll> getListData(String key) {
        Map<String,Object> param = new HashMap<String,Object>();
        param.put("parentType",key);
        VList<BaseDictAll> result = adminFeign.getPageDict(param);
        if (result != null && result.getCode().equals("0")) {
            List<BaseDictAll> data = result.getList();
            return data;
        }
        return null;
    }

}
