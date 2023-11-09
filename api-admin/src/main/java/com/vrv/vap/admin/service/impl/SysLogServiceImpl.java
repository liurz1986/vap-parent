package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.common.enums.EventTypeEnum;
import com.vrv.vap.admin.common.enums.LoginTypeEnum;
import com.vrv.vap.admin.common.enums.TypeEnum;
import com.vrv.vap.admin.mapper.SysLogMapper;
import com.vrv.vap.admin.model.SysLog;
import com.vrv.vap.admin.service.SysLogService;
import com.vrv.vap.admin.vo.ListSysLogQuery;
import com.vrv.vap.admin.vo.LoginThirtyDayVO;
import com.vrv.vap.admin.vo.SysRequestLogVO;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author huipei.x
 * @data 创建时间 2019/6/14
 * @description 类说明 :
 */
@Service
@Slf4j
public class SysLogServiceImpl extends BaseServiceImpl<SysLog> implements SysLogService {


    private static final String PARAMMODE = "{}";

    @Resource
    private SysLogMapper sysLogMapper;

    private static final String COMMON = "common";

    @Override
    public List querySysLog(ListSysLogQuery listSysLogQuery, List<String> orgNameList) {
        List<SysLog> sysLogList = sysLogMapper.querySysLog(listSysLogQuery,orgNameList);
        return transformSysLog(sysLogList);
    }

    @Override
    public long querySysLogCount(ListSysLogQuery listSysLogQuery, List<String> orgNameList) {
        long total = sysLogMapper.querySysLogCount(listSysLogQuery,orgNameList);
        return total;
    }

    @Override
    public List<LoginThirtyDayVO> loginThirtyDay(List<String> orgNameList,List<String> roleNameList) {
        return sysLogMapper.loginThirtyDay( orgNameList,roleNameList);
    }

    /**
     *   将List<SysLog>转化为List<SysRequestLogVO>
     */
    private List<SysRequestLogVO> transformSysLog(List<SysLog> sysLogList) {
        List<SysRequestLogVO> sysRequestLogVOList = new ArrayList<>();
        if (CollectionUtils.isEmpty(sysLogList)) {
            return new ArrayList<>();
        }
        sysRequestLogVOList = sysLogList.stream().map(p -> {
            SysRequestLogVO sysRequestLogVO = new SysRequestLogVO();
            BeanUtils.copyProperties(p, sysRequestLogVO);
                    return sysRequestLogVO;
        }).collect(Collectors.toList());
        return getSysRequestLogDTO(sysRequestLogVOList);
    }

    private List<SysRequestLogVO> getSysRequestLogDTO(List<SysRequestLogVO> list) {
        for (SysRequestLogVO SysRequestLogVO : list) {
            if (PARAMMODE.equals(SysRequestLogVO.getParamsValue())) {
                SysRequestLogVO.setParamsValue("");
            }
            if (SysRequestLogVO.getLoginType()!=null) {
                Map loginTypes = new HashMap();
                loginTypes.put("id", SysRequestLogVO.getLoginType());
                loginTypes.put("value", LoginTypeEnum.loginTypeEnumEscape(SysRequestLogVO.getLoginType()));
                SysRequestLogVO.setLoginTypes(loginTypes);
            }
            if (SysRequestLogVO.getType()!=null) {
                Map loginTypes = new HashMap();
                loginTypes.put("id", SysRequestLogVO.getType());
                loginTypes.put("value", TypeEnum.typeEnum(SysRequestLogVO.getType()));
                SysRequestLogVO.setTypes(loginTypes);
            }
            if (StringUtils.isNotBlank(SysRequestLogVO.getUserName())) {
                SysRequestLogVO.setEventType(EventTypeEnum.eventTypeEscape(SysRequestLogVO.getUserName()));
            }
        }
        return list;
    }


    @Override
    public long getActiceUserCount() {
        return sysLogMapper.getActiceUserCount();
    }

    @Override
    public List<Map> getLoginCount(Integer day) {
        String roleCode = this.getRoleCode();
        return sysLogMapper.getLoginCount(roleCode,day);
    }

    public List<Map> loginTrend(Integer day) {
        String roleCode = this.getRoleCode();
        return sysLogMapper.loginTrend(roleCode,day);
    }

    @Override
    public List<Map> getResponsResultCount() {
        String roleCode = this.getRoleCode();
        return sysLogMapper.getResponsResultCount(roleCode);
    }

    @Override
    public List<Map> getVisitPageCount(Integer day,String type) {
        String roleCode = this.getRoleCode();
        if (COMMON.equals(type)) {
            return sysLogMapper.getCommonVisitPageCount(roleCode,day);
        } else {
            return sysLogMapper.getUnCommonVisitPageCount(roleCode,day);
        }
    }

    private static Long comparingByCount(Map<String, Object> map){
        return (Long) map.get("count");
    }

    private String getRoleCode() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        String roleCode = "";
        if (user != null) {
            List<String> roleCodes = user.getRoleCode();
            roleCode = roleCodes.get(0);
        }
        return roleCode;
    }

    @Override
    public void cleanSyslog(Integer cleanDate) {
        sysLogMapper.cleanSyslog(cleanDate);
    }

    @Override
    public List<Map> getResponseErrorCount(Integer day) {
        String roleCode = this.getRoleCode();
        return sysLogMapper.getResponseErrorCount(roleCode,day);
    }

    @Override
    public List<Map> getOperateTypeCount(Integer day) {
        String roleCode = this.getRoleCode();
        List<Map> typeList = sysLogMapper.getOperateTypeCount(roleCode,day);
        if (CollectionUtils.isNotEmpty(typeList)) {
            for (Map map : typeList) {
                Integer type = (Integer) map.get("type");
                List<Map> details = sysLogMapper.getOperateTypeDetail(roleCode,day,type);
                map.put("detail",details);
                map.put("type",TypeEnum.typeEnum(type));
            }
        }
        return typeList;
    }
}