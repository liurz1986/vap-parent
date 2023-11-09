package com.vrv.vap.admin.common.constant;

public interface ReportConstant {

    interface DATA_SOURCE_TYPE{ //1.mysql 2.es
        String MYSQL = "1";
        String ELASTICSEARCH = "2";
    }

    interface DATA_SOURCE_TYPE_ID{ //-1.当前mysql -2.当前es
        Integer MYSQL = -1;
        Integer ELASTICSEARCH = -2;
    }

    interface MODEL_TYPE{ //1：饼图，2：折线图，3：柱状图，4：表格, 5:段落 ,6:引用，7:列表 8:仅标题
        String PIE = "1";
        String LINE = "2";
        String BAR = "3";
        String TABLE = "4";
        String TEXTAREA = "5";
        String QUOTE = "6";
        String LIST = "7";
        String TITLE = "8";
        String STACK_BAR = "9";
    }

    interface REPORT_TYPE{
        String PDF = "1";
        String WORD = "2";
        String HTML = "3";
        String WPS = "4";
    }

    interface REPORT_EXT{
        String PDF = "pdf";
        String DOC = "doc";
        String HTML = "html";
        String WPS = "wps";
    }

    interface  TEM_PREFIX{
        String DEFAULT = "_report_";
    }

    interface  TEM_DIR{
        String DEFAULT = "/tem";
    }

    interface IS_INTERFACE{ //1 数据源为指标 0非指标
        String YES = "1";
        String NO = "0";
    }

    interface INTERFACE_TYPE{ //1 list结构 2map结构 3混合结构 4空数据结构
        String LIST = "1";
        String MAP = "2";
        String LIST_AND_MAP = "3";
        String EMPTY = "4";
    }

}
