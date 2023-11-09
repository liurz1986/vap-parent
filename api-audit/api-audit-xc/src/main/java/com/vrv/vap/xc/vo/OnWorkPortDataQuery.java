package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="OnWorkPortData对象", description="")
public class OnWorkPortDataQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String srcArea;

    private String dstArea;

    private String srcIp;

    private String dstIp;

    private String gameName;

    private String gamePort;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.TIME_RANGE)
    private Date time;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getSrcArea() {
        return srcArea;
    }

    public void setSrcArea(String srcArea) {
        this.srcArea = srcArea;
    }
    public String getDstArea() {
        return dstArea;
    }

    public void setDstArea(String dstArea) {
        this.dstArea = dstArea;
    }
    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }
    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
    public String getGamePort() {
        return gamePort;
    }

    public void setGamePort(String gamePort) {
        this.gamePort = gamePort;
    }
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OnWorkPortDataQuery{" +
            "id=" + id +
            ", srcArea=" + srcArea +
            ", dstArea=" + dstArea +
            ", srcIp=" + srcIp +
            ", dstIp=" + dstIp +
            ", gameName=" + gameName +
            ", gamePort=" + gamePort +
            ", time=" + time +
        "}";
    }
}
