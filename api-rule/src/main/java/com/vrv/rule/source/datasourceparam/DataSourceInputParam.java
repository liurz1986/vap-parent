package com.vrv.rule.source.datasourceparam;

import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class DataSourceInputParam implements Serializable {

    private static final long serialVersionUID = 1L;

    private String tag; //数据源类型
    private Exchanges exchanges;  //算子
    private String groupId; //kafka消费组
    private String roomType; //盒子类型
    private FilterConfigObject filterConfigObject; //过滤器配置对象
    private String startConfig; //离线启动参数

    private List<String> sources; //当没有算子的时候，相关的数据源

}
