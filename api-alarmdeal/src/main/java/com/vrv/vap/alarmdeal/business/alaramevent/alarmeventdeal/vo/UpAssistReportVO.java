package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.AbstractUpAssist;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean.CoFile;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lps 2021/10/9
 * 协办上报
 */

@Data
public class UpAssistReportVO extends UpAbstractReportVO{
    @SerializedName(value = "co_file",alternate = {"attachment"})
    private List<CoFile> co_file = new ArrayList<>();
    private List<AbstractUpAssist> data;
}
