package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service;

import com.google.gson.Gson;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.Attachment;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.FileInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.FileInfoDispose;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.UpEventDTO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;

import java.util.List;
import java.util.Map;

public interface IUpReportCommonService {

    /**
     * 获取系统编码
     */
    String getClientId();

    /**
     * 获取人员涉密等级名称
     */
    String getPersonLevelName(String personLevel);

    /**
     * 设值文件处置
     */
    void setFileInfoDispose(List<FileInfo> fileInfos, FileInfoDispose fileInfoDispose);

    List<Attachment> getAttachmentListByArgs(Map<String, Object> busiArgs, String key);

    /**
     * 获取es中的数据，优先考虑直接获取，如果无法直接获取再考虑根据eventId从es中查询。
     * 1，直接获取
     * 如果无法直接获取的话，再从es中查询获取
     */
    AlarmEventAttribute getAlarmEventAttribute(UpEventDTO eventDTO);

    /**
     * 根据角色id获取角色名称
     */
    String getRoleNameByRoleId(String roleId);

    /**
     * 上报事件的统一入口
     *
     * @param eventDTO 事件对象
     */
    void upReportEvent(UpEventDTO eventDTO);

    /**
     * 通过附件信息JSON数据获取coFileList
     */
    List<CoFile> getColFilesByAttachmentJSON(String attachmentJSON, Gson gsonNotIgnoreNull);


}
