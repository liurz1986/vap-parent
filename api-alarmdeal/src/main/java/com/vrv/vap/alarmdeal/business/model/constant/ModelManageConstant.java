package com.vrv.vap.alarmdeal.business.model.constant;

import java.util.Arrays;
import java.util.List;

public class ModelManageConstant {
    public static final String modelJobName="modelJob_";  // 定时任务名称前缀

    public static final String SUCSESS="success";

    public static final String ERROR="error";

    public static final String FILETYPEJSON="json";

    public static final List<String> fileTypes = Arrays.asList(new String[]{"json","jar"});  //导入文件类型

    /**
     * 查询时区分页面：模型配置管理页面，该值为1;已发布模型管理页面，该值为2
     */
    public class QueryType{
        public static final String ONE="1";  // 模型配置管理页面

        public static final String TWO="2";  // 已发布模型管理页面

        public static final String ALL="all"; // 其他
    }

    public class ModelStatus{
        public static final int DRAFT=1;  // 待测试

        public static final int VALIDATE=2;  // 已测试

        public static final int DEPLOYE=3; // 发布

        public static final int START=4; // 启动

        public static final int STOP=5; // 停用

        public static final int OFFSHELF=6; // 下架

    }

    public class DataCustomerModelType{
        public static final String ONE="一次性消费";

        public static final String PERIOD="周期消费";
    }

    public class OperationStatus{
        public static final String ADD="add";  // 新增

        public static final String EDIT="edit";  // 编辑


    }


}
