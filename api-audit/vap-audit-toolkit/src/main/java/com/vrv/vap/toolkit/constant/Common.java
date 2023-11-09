package com.vrv.vap.toolkit.constant;

/**
 * 通用常量
 *
 * @author xw
 * @date 2018年3月28日
 */
public interface Common {

    // 数据总数
    String TOTAL = "total";

    String DATAS = "data";

    // es实际查询数据总数
    String TOTAL_ACC = "total_acc";

    // 分页
    String START = "start";
    String COUNT = "count";

    String START_ = "start_";
    String COUNT_ = "count_";

    // 请求类型小写
    String GET_METHOD = "get";
    String POST_METHOD = "post";
    String PUT_METHOD = "put";
    String DELETE_METHOD = "delete";
    String HEAD_METHOD = "head";

    // 请求类型大写
    String PUT = "PUT";
    String POST = "POST";
    String GET = "GET";
    String PATCH = "PATCH";
    String DELETE = "DELETE";

    // 未知
    String UNKONW = "UNKONW";

    /**
     * 默认值
     */
    int START_DEF = 0;
    int COUNT_DEF = 10;

    String EXPORT_REDIS_PRO_PATH = "export:excel:progress:";
    String EXPORT_REDIS_FILE_PATH = "export:excel:file:";

    String EXPORT_ZIP_REDIS_PRO_PATH = "export:zip:progress:";
    String EXPORT_ZIP_REDIS_FILE_PATH = "export:zip:file:";

    String EXPORT_PDF_REDIS_PRO_PATH = "export:pdf:progress:";
    String EXPORT_PDF_REDIS_FILE_PATH = "export:pdf:file:";
}
