package com.vrv.vap.xc.constants;

public interface LineConstants {
//（过滤类型 1 ：等于 2 大于 3小于 4 in 5like）
    public interface FILTER_TYPE{
        String EQ = "1";
        String GT = "2";
        String LT = "3";
        String IN = "4";
        String LIKE = "5";
        String N_EQ = "6";
    }

    public interface NAME_PRE{
        String PRE_LINE = "base-line-";
        String PRE_SM = "summary-";
    }

    public interface AGG_TYPE{
        String TERMS = "1";
        String SUM = "2";
        String COUNT = "3";
        String AVG = "4";
        String TOP = "5";
        String DEV = "6";
        String DATA = "7";
        String RATIO = "8";
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
        String INIT = "0";
        String ENABLE = "1";
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

    public interface LINE_TYPE{
        String QD = "1";
        String TJ = "2";
        String SD = "3";
        String TS = "4";
    }

    public interface SPECIAL_TYPE{
        String ACTUAL = "1";
        String TJ = "2";
    }
}
