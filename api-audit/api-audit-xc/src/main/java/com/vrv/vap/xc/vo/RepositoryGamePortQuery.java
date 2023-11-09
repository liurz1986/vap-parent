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
@ApiModel(value="RepositoryGamePort对象", description="")
public class RepositoryGamePortQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String gameName;

    private String gamePort;

    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String gameDescription;

    private Date insertTime;

    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
    public String getGameDescription() {
        return gameDescription;
    }

    public void setGameDescription(String gameDescription) {
        this.gameDescription = gameDescription;
    }
    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    @Override
    public String toString() {
        return "RepositoryGamePortQuery{" +
            "id=" + id +
            ", gameName=" + gameName +
            ", gamePort=" + gamePort +
            ", gameDescription=" + gameDescription +
            ", insertTime=" + insertTime +
            ", lastUpdateTime=" + lastUpdateTime +
        "}";
    }
}
