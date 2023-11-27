package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.*;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.IUpReportCommonService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl.upreport.IUpReportEventService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.BinUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.TicketAttachment;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.jpa.spring.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 上报的公共服务类
 * 有关上报公共的方法写在这里
 */
@Service
public class UpReportCommonServiceImpl implements IUpReportCommonService {

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private TbConfService tbService;
    private static Gson gson = new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd HH:mm:ss").registerTypeAdapter(Boolean.class, new NumericBooleanSerializer())
            .create();

    /**
     * 获取clientId,获取系统编码
     */
    @Override
    public String getClientId() {
        TbConf serverInfo = tbService.getOne("ServerInfo");
        if (serverInfo == null) {
            return "";
        }
        if (StringUtils.isEmpty(serverInfo.getValue())) {
            return "";
        }
        Map<String, String> valueMap = gson.fromJson(serverInfo.getValue(), Map.class);
        return valueMap.get("clientId");
    }

    /**
     * 获取人员涉密等级名称
     */
    public String getPersonLevelName(String personLevel) {
        if (StringUtils.isEmpty(personLevel)) {
            return "";
        }
        //1,2,3,4
        return new String[]{"非密", "一般", "重要", "核心"}[Integer.parseInt(personLevel) - 1];
    }

    /**
     * 设值文件处置
     */
    public void setFileInfoDispose(List<FileInfo> fileInfos, FileInfoDispose fileInfoDispose) {
        //卫语句减少嵌套
        if (fileInfos == null) {
            return;
        }
        //处理
        for (FileInfo fileInfo : fileInfos) {
            if (StringUtils.isNotEmpty(fileInfo.getFileSecurityLevel())) {
                switch (fileInfo.getFileSecurityLevel()) {
                    //绝密
                    case "0":
                        fileInfoDispose.setFile_mm03_count(fileInfoDispose.getFile_mm03_count() + 1);
                        break;
                    case "1":
                        //机密
                        fileInfoDispose.setFile_mm02_count(fileInfoDispose.getFile_mm02_count() + 1);
                        break;
                    case "2":
                        //涉密
                        fileInfoDispose.setFile_mm01_count(fileInfoDispose.getFile_mm01_count() + 1);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 上报表单附件赋值
     *
     * @param busiArgs 事件处置工单流程数据
     * @param key
     */
    public List<Attachment> getAttachmentListByArgs(Map<String, Object> busiArgs, String key) {
        //表单-附件
        List<Map<String, Object>> list = gson.fromJson(gson.toJson(busiArgs.get(key)), List.class);
        List<Attachment> attachments = new ArrayList<>();
        if (list != null) {
            for (Map<String, Object> item : list) {
                Map<String, Object> response = (Map<String, Object>) item.get("response");
                if (response == null) {
                    continue;
                }
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                if (data == null) {
                    continue;
                }
                String fileName = data.get("fileName") != null ? data.get("fileName").toString() : "";
                String filePath = data.get("filePath") != null ? data.get("filePath").toString() : "";
                Attachment attachment = new Attachment();
                attachment.setFile_name(fileName);
                attachment.setFile_path(filePath);
                if (StringUtils.isNotEmpty(filePath)) {
                    attachment.setFile_bin(BinUtil.fileToBinStr(filePath));
                }
                attachments.add(attachment);
            }
        }
        return attachments;
    }

    /**
     * 获取es中的数据，优先考虑直接获取，如果无法直接获取再考虑根据eventId从es中查询。
     * 1，直接获取
     * 如果无法直接获取的话，再从es中查询获取
     */
    public AlarmEventAttribute getAlarmEventAttribute(UpEventDTO eventDTO) {
        AlarmEventAttribute alarmEventAttribute = eventDTO.getDoc();
        return alarmEventAttribute == null ? alarmEventManagementForEsService.getDocByEventId(eventDTO.getEventId()) : alarmEventAttribute;
    }

    /**
     * 根据roleId获取roleName
     */
    public String getRoleNameByRoleId(String roleId) {
        if(StringUtils.isEmpty(roleId)){
            return "";
        }
        String sql="select name from role where id=?";
        return jdbcTemplate.queryForObject(sql,new Object[]{roleId},String.class);
    }

    /**
     * 上报事件，所有上报事件的入口
     *
     * @param eventDTO 事件传输对象
     */
    @Override
    public void upReportEvent(UpEventDTO eventDTO) {
        //1 获取上报bean对象的名称
        String upReportBeanName = eventDTO.getUpReportBeanName();
        //2 根据bean名称获取上报事件服务对象
        IUpReportEventService upReportEventService = (IUpReportEventService) SpringUtil.getBean(upReportBeanName);
        //3 上报事件
        upReportEventService.upEventToKafka(eventDTO);
    }
    /**
     * 通过附件信息JSON数据获取coFileList
     * @param gsonNotIgnoreNull 不会忽略空的字符就会省略字段的json对象
     * @param attachmentJSON 附件json
     */
    @Override
    public List<CoFile> getColFilesByAttachmentJSON(String attachmentJSON,Gson gsonNotIgnoreNull) {
        List<CoFile> coFiles = new ArrayList<>();
        if (StringUtils.isEmpty(attachmentJSON)) {
            return coFiles;
        }
        TicketAttachment[] ticketAttachments = gsonNotIgnoreNull.fromJson(attachmentJSON, TicketAttachment[].class);
        List<Map<String, Object>> responses = new ArrayList<>();
        for (TicketAttachment ticketAttachment : ticketAttachments) {
            TicketAttachment.Response response = ticketAttachment.getResponse();
            if (response == null) {
                continue;
            }
            if (0 == response.getCode()) {
                responses.add(response.getData());
            }
        }
        return fileTransformBinStr(responses);
    }

    /**
     * 文件转二进制数据转为字符串
     */
    private List<CoFile> fileTransformBinStr(List<Map<String, Object>> fileInfos) {
        List<CoFile> listCopy = new ArrayList<>();
        if (fileInfos != null) {
            for (Map<String, Object> fileInfo : fileInfos) {
                CoFile coFile = new CoFile();
                coFile.setFile_name(fileInfo.get("fileName") != null ? fileInfo.get("fileName").toString() : "");
                coFile.setFile_bin(BinUtil.fileToBinStr(fileInfo.get("filePath") != null ? fileInfo.get("filePath").toString() : ""));
                listCopy.add(coFile);
            }
        }
        return listCopy;
    }



}
