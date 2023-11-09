package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="RptPkiCount对象", description="")
public class RptPkiCountQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "身份证")
    private String userId;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "最后一次插拔时间")
    private String lastTime;

    @ApiModelProperty(value = "插拔次数")
    private Integer count;

    @ApiModelProperty(value = "插入时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
    public String getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(String insertTime) {
        this.insertTime = insertTime;
    }

    @Override
    public String toString() {
        return "RptPkiCount{" +
            "id=" + id +
            ", ip=" + ip +
            ", userId=" + userId +
            ", userName=" + userName +
            ", lastTime=" + lastTime +
            ", count=" + count +
            ", insertTime=" + insertTime +
        "}";
    }
}
