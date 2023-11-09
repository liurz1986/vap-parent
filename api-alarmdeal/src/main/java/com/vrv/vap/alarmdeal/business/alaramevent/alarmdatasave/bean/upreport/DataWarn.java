package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 预警data
 */
@Data
public class DataWarn extends AbstractUpData {
    /**
     *预警id
     */
    @SerializedName(value = "warnning_id", alternate = "warnningId")
    private String warnning_id;
    /**
     * 风险预警说明
     */
    @SerializedName(value = "warnning_description", alternate = "noticeDesc")
    private String warnning_description;
    /**
     * 反馈结果
     */
    @SerializedName(value = "warning_conlusion", alternate = "responseNote")
    private String warning_conlusion;


}
