package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import com.baomidou.mybatisplus.annotation.TableId;
import java.util.Date;
import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-18
 */
@ApiModel(value="UploadInfo对象", description="")
public class UploadInfoQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String fileName;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String filePath;

    private String describes;

    private Date uploadTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public String getDescribes() {
        return describes;
    }

    public void setDescribes(String describes) {
        this.describes = describes;
    }
    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "UploadInfo{" +
            "id=" + id +
            ", fileName=" + fileName +
            ", filePath=" + filePath +
            ", describes=" + describes +
            ", uploadTime=" + uploadTime +
        "}";
    }
}
