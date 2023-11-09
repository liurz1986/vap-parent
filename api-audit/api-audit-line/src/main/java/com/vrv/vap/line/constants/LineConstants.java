package com.vrv.vap.line.constants;

public interface LineConstants {
//（过滤类型 1 ：等于 2 大于 3小于 4 in 5like）
    public interface FILTER_TYPE{
        //等于
        String EQ = "1";
        //大于
        String GT = "2";
        //小于
        String LT = "3";
        //介于
        String IN = "4";
        //模糊匹配
        String LIKE = "5";
        //不等于
        String N_EQ = "6";
    }
    public interface LINE_TYPE{
        //清单
        String QD = "1";
        //统计
        String TJ = "2";
        //时段
        String SD = "3";
        //内置
        String TS = "4";
    }


    public interface AGG_TYPE{
        //分组
        String TERMS = "1";
        //求和
        String SUM = "2";
        //计数
        String COUNT = "3";
        //均值
        String AVG = "4";
        //直接取值
        String TOP = "5";
        //均方差
        String DEV = "6";
        //时间分桶
        String DATA = "7";
        //比率（数据源mysql时使用）
        String RATIO = "8";
        //去重（数据源mysql时使用）
        String DISTIC = "9";
    }

    public interface AGG_NAME{
        String TERMS = "terms";
        String SUM = "sum";
        String COUNT = "count";
        String AVG = "avg";
        String TOP = "top";
        String DEV = "dev";
        String DATA = "data";
    }

    public interface LINE_STATUS{
        //初始化
        String INIT = "0";
        //启用
        String ENABLE = "1";
        //禁用
        String DISABLE = "2";
    }

    public interface STRATEY_STATUS{
        //启用
        String ENABLE = "1";
        //禁用
        String DISABLE = "2";
    }

    public interface SAVE_TYPE{
        String ES = "1";
        String KAFKA = "2";
        String ES_AND_KAFAK = "3";
        String MYSQL = "4";
        String MYSQL_AND_KAFAK = "5";
    }

    public interface SOURCE_TYPE{
        String ES = "1";
        String MYSQL = "2";
    }

    public interface OPEN_GROUP{
        String NO = "0";
        String YES = "1";
    }

    public interface LINE_RESULT{
        String SUCCESS = "1";
        String FAILED = "0";
    }

    public interface CONTINUE{
        String YES = "1";
        String NO = "0";
    }

    public interface SQ{
        int timeSplit = 3;//切分时间间隔 单位 s
        int minSupport = 2;//切分时间间隔 单位 s
        int towLevelTimeSplit = 10;//二级切分时间间隔 单位 min
        int days = 365;//处理数据天数
        int BATCH = 1000;//单次入库量
        int AprioriMax = 1000;//先验算法最大事务量
        String index = "netflow-http-2022";//处理数据天数
        String timeField = "event_time";//处理数据天数
        String urlField = "url";//主体字段
        String orgField = "src_std_org_code";//主体字段
        String roleField = "role";//主体字段
        String separator = ";";//项分隔符
        String userField = "sip";//项分隔符
        String dipField = "dip";//项分隔符
        String dportField = "dport";//项分隔符
        String pckField = "content_length";//项分隔符
        Integer count = 10000;//项分隔符
        String resultIndex = "base-line-sequence";
        final String indexSufFormat = "-yyyy";
        String itemSeparator = "#";//项集分隔符
        //        String MYSQL_HOST = "192.168.121.131";
        String MYSQL_HOST = "127.0.0.1";
//        String START_TIME = "2022-07-17T16:00:00.000Z";
//        String END_TIME = "2022-07-22T15:59:59.000Z";

        String START_TIME = "2022-07-19T02:40:10.000Z";
        String END_TIME = "2022-07-30T15:59:59.000Z";
        String FOLDER = "E:\\vrv\\apriori\\";
        Integer parallelism = 1;
        Integer maxcount = 200000;
        boolean isasc = true;
        String[] resultField = {userField,timeField,pckField,urlField};
    }

    public interface MYSQL{
        String DRIVER = "com.mysql.cj.jdbc.Driver";
        String USER = "root";
        String PASSWORD = "mysql";
    }

    public interface NAME_PRE{
        String PRE_LINE = "base-line-";
        String PRE_SM = "summary-";
    }

    public interface TABLE_COMMENT{
        String PRE_LINE = "结果表";
        String PRE_SM = "中间值表";
    }

    public interface TABLE_TYPE{
        String LINE = "1";
        String SM = "2";
    }

    public interface SPECIAL_TYPE{
        //实时类型（存在flink流处理任务）
        String ACTUAL = "1";
        //统计类型（flink批处理）
        String TJ = "2";
    }

    public interface FRAME_TYPE{
        String JAVA = "1";
        String FLINK = "2";
    }
}
