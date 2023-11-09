package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

/**
 * 未知文件
 */
@Data
public class UnKnownFile {
    /**
     * 文件名称
     */
    @SerializedName(value = "file_name",alternate = "fileName")
    private String file_name;
    /**
     * 文件存储路径
     */
    @SerializedName(value = "file_path",alternate = "filePath")
    private String file_path;
    /**
     * 文件相关启动项
     */
    @SerializedName(value = "boot_option",alternate = "bootOption")
    private String boot_option;
    /**
     * 文件注册表位置
     */
    @SerializedName(value = "regedit_path",alternate = "regeditPath")
    private String regedit_path;
    /**
     * 进程名称
     */
    @SerializedName(value = "process_name",alternate = "processName")
    private String process_name;


}
