package com.vrv.vap.xc.service;

import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.xc.model.*;

import java.util.List;
import java.util.Map;

public interface ApplicationAnalysisService {

    /**
     * 分页查询用户访问ip列表
     * @param model
     * @return
     */
    VList<Map<String, String>> userHisVisits(UserVisitModel model);

    /**
     * 账户登陆口令尝试次数
     * @return
     */
    VList<Map<String, String>> aggLoginPwd(ApplicationModel pageModel);

    /**
     * 历史访问入口地址
     * @param appModel
     * @return
     */
    VList<Map<String, Object>> hisVisitsUrl(ApplicationModel appModel);

    /**
     * 用户历史通信使用的入口地址,ip,协议名,及端口号
     * @param appModel
     * @return
     */
    VList<Map<String, Object>> hisVisitAgreePort(ApplicationModel appModel);

    /**
     * 用户周月文件上传下载数量
     * @param fileModel
     * @return
     */
    VData<Long> userFileCount(FileModel fileModel);

    /**
     * 用户月周访问次数
     * @return
     */
    VData<Long> userVisitCount(TimeModel model);

    /**
     * 用户最近一次访问记录
     * @return
     */
    VData<Map<String,Object>> userVisitRecently(ApplicationModel model);

    /**
     * 后台服务列表
     * @return
     */
    VList<Map<String, Object>> serverList(ApplicationModel model);

    /**
     * 服务最近一次被访问记录
     * @return
     */
    VData<Map<String,Object>> serverVisitRecently(ApplicationModel model);

    /**
     * 其他应用列表
     * @return
     */
    VList<Map<String, Object>> otherServerList(ApplicationModel pageModel);

    /**
     * 应用访问趋势
     * @return
     */
    VList<Map<String, Object>> appVisitTrend(TimeModel model);

    /**
     * 文件下载上传趋势
     * @return
     */
    VData<Map<String, Object>> fileTrend(TimeModel model);

    /**
     * 输入/输出、密级-文件分布饼图
     * @return
     */
    VData<Map<String, Object>> inAndOutFileDate(ApplicationModel model);

    /**
     * 文件业务列表密级
     * @return
     */
    VData<List<Map<String, Object>>> fileListLevel(ApplicationModel appModel);

    /**
     * 互联访问趋势
     * @return
     */
    VData<List<Map<String, Object>>> interconTrend(TimeModel model);

    /**
     * 每对联通IP采用的协议及端口号
     * @param model
     * @return
     */
    EsResult interconProPortList(ApplicationModel model);

    /**
     * 协议次数分布
     * @return
     */
    VData<List<Map<String, Object>>> protocolCount(ApplicationModel model);

    /**
     * 内外ip数
     * @return
     */
    VData<Map<String, Object>> ipCount(ApplicationModel model);

    /**
     * 端口top排行
     * @return
     */
    VData<List<Map<String, Object>>> portTop(ApplicationModel model);

    /**
     * （输入/输出）每个密级的业务个数
     * @param model
     * @return
     */
    VData<Map<String, Object>> inoutlevelcount(ApplicationModel model);

    /**
     * 密级-用户数量分布
     * @param model
     * @return
     */
    VData<Map<String, Integer>> userleveldata(ApplicationModel model);

    /**
     * 用户-涉密信息数量TOP
     * @return
     */
    VData<List<Map<String, Object>>> userFileTop(ApplicationModel model);

    /**
     * 业务用户数列表
     * @return
     */
    VData<Map<String, Integer>> userBusinessList(ApplicationModel model);

    /**
     * 密级-应用数量分布
     * @return
     */
    VData<Map<String, Integer>> appleveldata(ApplicationModel model);

    /**
     * 应用-涉密信息数量TOP
     * @return
     */
    VData<List<Map<String, Object>>> appFileTop(ApplicationModel model);

    /**
     * 业务应用数列表
     * @return
     */
    VData<Map<String, Integer>> appBusinessList(ApplicationModel model);

    /**
     * 后台通信次数
     * @param model
     * @return
     */
    VData<Long> appSignalNum(ApplicationModel model);

    /**
     * 应用访问次数分布和应用个数
     * @return
     */
    VData<Map<String, Object>> appVisitCount(ApplicationModel model);

    /**
     * 文件上传下载数量分布
     * @return
     */
    VData<Map<String, Object>> fileDownUploadData(ApplicationModel model);

    /**
     * 业务类别、密级分布
     * @return
     */
    VData<Map<String, Object>> businessLevelData(ApplicationModel model);

    /**
     * 每个业务的密级数
     * @return
     */
    VData<Map<String, Integer>> businessLevelCount(ApplicationModel model);

    /**
     * 部门基础信息
     * @param departCode
     * @return
     */
    VData<Map<String, Integer>> departBaseInfo(ApplicationModel model);

    /**
     * 通信详情
     * @param model
     * @return
     */
    VData<List<Map<String, Object>>> visitDetail(ApplicationModel model);

    /**
     * 其他应用访问本应用次数
     * @param model
     * @return
     */
    VData<Long> otherSysVisitNum(ApplicationModel model);
}
