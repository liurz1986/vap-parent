package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.constant.DisponseConstant;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.IUpReportEventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventDisposal;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventUrge;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventDisposalService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.QueryIdsVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmEventUrgeVO;
import com.vrv.vap.alarmdeal.business.analysis.model.AuthorizationControl;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.common.model.User;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 15:20
 */
@Service
public class AlarmEventDisposalServiceImpl implements AlarmEventDisposalService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(AlarmEventDisposalServiceImpl.class);

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;
    @Autowired
    private IUpReportCommonService upReportCommonService;

    @Override
    public AlarmEventAttribute appendAlarmEventUrge(AlarmEventUrgeVO item, String eventId) {
        User currentUser = SessionUtil.getCurrentUser();
        if (currentUser == null) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "当前用户未登录");
        }
        AlarmEventAttribute event = alarmEventManagementForEsService.getDocByEventId(eventId);
        if (event == null) {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "未找到当前事件");
        }
        List<AlarmEventUrge> urgeInfos = event.getUrgeInfos();
        if (urgeInfos == null) {
            urgeInfos = new ArrayList<>();
        }
        AuthorizationControl authorization = event.getAuthorization();
        if (authorization == null) {
            authorization = new AuthorizationControl();
        }
        AlarmEventUrge info = new AlarmEventUrge();
        info.setUrgeTime(new Date());
        info.setIsAuto(false);
        info.setUrgeRemark(item.getUrgeRemark());
        info.setToRole(item.getToRole());
        info.setToUser(item.getToUser());
        info.setValidityDate(item.getValidityDate());
        urgeInfos.add(info);
        event.setUrgeInfos(urgeInfos);
        event.setIsUrge(true);

        event.setAuthorization(authorization);
        event.setUrgeInfos(urgeInfos);

        if (item.getValidityDate() != null) {
            event.setValidityDate(item.getValidityDate());
        }
        alarmEventManagementForEsService.saveAlarmEventData(event);
        try {
            upEventUrge(currentUser, event);
        } catch (Exception e) {
            logger.error("#############上报事件处置督促失败了，失败的原因为", e);
        }
        return event;
    }


    private void upEventUrge(User currentUser, AlarmEventAttribute event) {
        //注意，一定要是保密办督办也就是督促后，发送事件处置
        if (currentUser.getRoleCode().contains("secretMgr")) {
            UpEventDTO eventDTO = new UpEventDTO();
            eventDTO.setName(currentUser.getName());
            eventDTO.setRoleName(currentUser.getRoleName());
            eventDTO.setDisposeStatus(DisponseConstant.SECRET_SUPERVISE);
            eventDTO.setDoc(event);
            eventDTO.setUpReportBeanName(IUpReportEventService.UpReportDispose_BEAN_NAME);
            upReportCommonService.upReportEvent(eventDTO);
        }
    }

    @Override
    public List<AlarmEventAttribute> appendAlarmEventUrges(QueryIdsVO vo) {
        logger.info("添加督促信息（批量）");
        Gson gson = new Gson();
        try {
            List<AlarmEventAttribute> events = new ArrayList<>();
            for (String eventId : vo.getIds()) {
                AlarmEventUrgeVO item = gson.fromJson(gson.toJson(vo), AlarmEventUrgeVO.class);
                AlarmEventAttribute alarmEventAttribute = appendAlarmEventUrge(item, eventId);
                events.add(alarmEventAttribute);
            }
            return events;
        } catch (Exception e) {
            logger.error("批量督促出现异常", e);
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), e.getMessage());
        }
    }

    @Override
    public AlarmEventDisposal getAlarmEventDisposal(String guid) {
        List<Map<String, Object>> list = alarmEventManagementForEsService.getLogByEventIdAndIndexName("alarmeventdisposal", Arrays.asList("guid"));
        AlarmEventDisposal alarmEventDisposal = null;
        if (CollectionUtils.isNotEmpty(list)) {
            Map<String, Object> map = list.get(0);
            alarmEventDisposal = JSONObject.parseObject(JSON.toJSONString(map), AlarmEventDisposal.class);
        }
        return alarmEventDisposal;
    }
}
