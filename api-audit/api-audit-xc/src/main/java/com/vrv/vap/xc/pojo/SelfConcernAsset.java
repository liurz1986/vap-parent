package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("self_concern_asset")
public class SelfConcernAsset {
	private String guid;
	private String userId;
	private String ip;
	private Integer type; //0资产ip 1应用系统id 2网络边界id
}
