package com.vrv.rule.vo;

import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Tables;
import lombok.Builder;
import lombok.Data;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import java.io.Serializable;
import java.util.List;

/**
 * 抽象数据源输入对应数据
 */
@Data
@Builder
public class DataStreamInputVO implements Serializable {

        private static final long serialVersionUID = 1L;


        private  StreamExecutionEnvironment env;

        private String sourceId;  //数据源ID

        private Tables tables; //表对象

        private List<Attach> attachs; //表附着属性

        private String roomType; //盒子类型

        private String startConfig; //离线启动参数

        private String groupId; //kafka消费组

}
