package com.vrv.vap.xc.tools;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.init.SingleTableBuilder;
import com.vrv.vap.xc.model.BaseAreaModel;
import com.vrv.vap.xc.model.QueryModel;
import com.vrv.vap.xc.model.SingleTableModel;
import com.vrv.vap.xc.service.impl.SingleTableServiceImpl;
import com.vrv.vap.toolkit.vo.VList;
import org.apache.commons.lang.StringUtils;

import java.util.*;


/**
 * 数据处理
 *
 * @author xw
 * @date 2016年6月30日
 */
public final class ExcelValHandleTools {

    public static String find(Object v, String[][] refs) {
        if (null == v) {
            return null;
        }
        for (String[] ref : refs) {
            if (ref[0].equals(v.toString().trim())) {
                return ref[1];
            }
            if (ref[1].equals(v.toString().trim())) {
                return ref[0];
            }
        }
        return null;
    }

    /**
     * 大字典表
     */
    private static final Map<String, String> DICT_MAP = new HashMap<>();

    /**
     * 警员岗位
     */
    private static final Map<String, String> POST_MAP = new HashMap<>();

    /**
     * 警种
     */
    private static final Map<String, String> POLICE_TYPE_MAP = new HashMap<>();

    /**
     * 区域
     */
    private static final Map<String, String> AREA_MAP = new HashMap<>();

    /**
     * 组织机构
     */
    private static final Map<String, String> ORG_MAP = new HashMap<>();

    /**
     * 应用系统
     */
    private static final Map<String, String> SYS_MAP = new HashMap<>();

    /**
     * 应用系统-orgcode
     */
    private static final Map<String, String> SYS_ORG_MAP = new HashMap<>();

    /**
     * 业务分类
     */
    private static final Map<String, String> APP_TYPE_MAP = new HashMap<>();

    /**
     * 账户类型
     */
    private static final Map<String, String> ACCOUNT_TYPE_MAP = new HashMap<>();

    /**
     * 个人登录方式
     */
    private static final Map<String, String> LOGIN_TYPE_MAP = new HashMap<>();

    /**
     * 告警分类
     */
    private static final Map<String, String> WARN_TYPE_MAP = new HashMap<>();
//    private static final String[][] warnType = new String[][]{
//            new String[]{"/safer/business/pkiuse", "证书违规使用"},
//            new String[]{"/safer/business/outreach", "违规外联"},
//            new String[]{"/safer/business/illegalprocess", "危险进程"},
//            new String[]{"/safer/business/gameprocess", "游戏进程"},
//            new String[]{"/safer/business/red", "红名单"},
//            new String[]{"/safer/business/hot", "热点库"}};

    /**
     * 告警处理状态：0-待确认、1-已确认、2-已处置、3-误报、4-待下发、5-已接受、6-已接收
     */
    private static final Map<String, String> WARN_STATE_MAP = new HashMap<>();
//    private static final String[][] warnState = new String[][]{
//            new String[]{"0", "待确认"},
//            new String[]{"1", "已确认"},
//            new String[]{"2", "已处置"},
//            new String[]{"3", "误报"},
//            new String[]{"4", "待下发"},
//            new String[]{"5", "已接受"},
//            new String[]{"6", "已接收"}};
//    /**
//     * 报警类型
//     *
//     * @param val
//     * @return
//     */
//    public static String warnType(Object val) {
//        return find(String.valueOf(val), warnType);
//    }

//    /**
//     * 报警处理状态
//     *
//     * @param val
//     * @return
//     */
//    public static String warnState(Object val) {
//        return find(String.valueOf(val), warnState);
//    }

    /**
     * 告警类型
     *
     * @param val
     * @return
     */
    public static synchronized String warnType(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (WARN_TYPE_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("_parent_type", "fad2c67f-d696-11e8-8a6d-000c29d87e13");
            for (Map<String, Object> temp : querySingleTable("base_dict_all", whereMap)) {
                WARN_TYPE_MAP.put(temp.get("code").toString(), temp.get("codeValue").toString());
            }
        }
        String warnType = WARN_TYPE_MAP.get(val.toString());
        return warnType == null ? "未知".intern() : warnType;
    }

    /**
     * 告警处理状态
     *
     * @param val
     * @return
     */
    public static synchronized String warnState(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (WARN_STATE_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("_parent_type", "ed32a60b-cb6f-11e8-b6f6-000c29d87e13");
            for (Map<String, Object> temp : querySingleTable("base_dict_all", whereMap)) {
                WARN_STATE_MAP.put(temp.get("code").toString(), temp.get("codeValue").toString());
            }
        }
        String warnState = WARN_STATE_MAP.get(val.toString());
        return warnState == null ? "未知".intern() : warnState;
    }

    /**
     * 证书状态
     *
     * @param val
     * @return
     */
    public static String certStatus(Object val) {
        return "5".equals(val) ? "在用" : ("6".equals(val) ? "注销" : ("7".equals(val) ? "停用" : "未知"));
    }

    /**
     * lock状态
     *
     * @param val
     * @return
     */
    public static String lockStatus(Object val) {
        return "1".equals(val) ? "是" : "否";
    }

    /**
     * protect状态
     *
     * @param val
     * @return
     */
    public static String protectStatus(Object val) {
        return "1".equals(val) ? "是" : "否";
    }

    /**
     * forceOut状态
     *
     * @param val
     * @return
     */
    public static String forceOutStatus(Object val) {
        return "1".equals(val) ? "是" : "否";
    }

    /**
     * 警员岗位(原dict_post_type表)
     *
     * @param val
     * @return
     */
    public static synchronized String policeStation(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (POST_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("_parent_type", "22ea3346-c075-11e8-9405-00163e00156d");
            for (Map<String, Object> temp : querySingleTable("base_dict_all", whereMap)) {
                POST_MAP.put(temp.get("code").toString(), temp.get("codeValue").toString());
                POST_MAP.put(temp.get("codeValue").toString(), temp.get("code").toString());
            }
        }
        String post = POST_MAP.get(val.toString());
        return post == null ? "未知".intern() : post;
    }

    /**
     * 警种（原dict_police_type）
     *
     * @param val
     * @return
     */
    public static synchronized String policeType(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (POLICE_TYPE_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            //标准版使用
            whereMap.put("_parent_type", "22ea36ca-c075-11e8-9405-00163e00156d");
            //公安部使用
//           whereMap.put("_parent_type","61124420-c206-11e8-9405-00163e00156d");
            for (Map<String, Object> temp : querySingleTable("base_dict_all", whereMap)) {
                POLICE_TYPE_MAP.put(temp.get("code").toString(), temp.get("codeValue").toString());
                POLICE_TYPE_MAP.put(temp.get("codeValue").toString(), temp.get("code").toString());
            }
        }
        String policeType = POLICE_TYPE_MAP.get(val.toString());
        return policeType == null ? "未知".intern() : policeType;
    }


    /**
     * 区域
     *
     * @param val
     * @return
     */
    public static synchronized String areaCode2Name(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (AREA_MAP.isEmpty()) {
            for (BaseAreaModel baseAreaModel : BaseAreaTools.getAreaList()) {
                AREA_MAP.put(baseAreaModel.getAreaCode(), baseAreaModel.getAreaName());
                // AREA_MAP.put(temp.get("area_name").toString(),
                // temp.get("area_code").toString());
            }
        }
        String areaName = AREA_MAP.get(val.toString());
        return areaName == null ? "未知".intern() : areaName;
    }

    /**
     * 区域编码
     *
     * @param val
     * @return
     */
    public static synchronized String areaNameToCode(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        Optional<BaseAreaModel> opt = BaseAreaTools.getBaseAreaByName(val.toString());
        if (opt.isPresent()) {
            return opt.get().getAreaCode();
        }
        return null;
    }

    /**
     * 业务类型
     *
     * @param val
     * @return
     */
    public static synchronized String appType(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (APP_TYPE_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            // 使用人员警种
            //标准版使用
            whereMap.put("_parent_type", "22ea36ca-c075-11e8-9405-00163e00156d");
            for (Map<String, Object> map : querySingleTable("base_dict_all", whereMap)) {
                APP_TYPE_MAP.put(map.get("code").toString(), map.get("codeValue").toString());
                APP_TYPE_MAP.put(map.get("codeValue").toString(), map.get("code").toString());
            }
        }
        String appType = APP_TYPE_MAP.get(val.toString());
        return appType == null ? "未知".intern() : appType;
    }

    /**
     * 账户类型
     *
     * @param val
     * @return
     */
    public static synchronized String accountType(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (POST_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("_parent_type", "6b88fcce-d696-11e8-8a6d-000c29d87e13");
            for (Map<String, Object> temp : querySingleTable("base_dict_all", whereMap)) {
                POST_MAP.put(temp.get("code").toString(), temp.get("codeValue").toString());
                POST_MAP.put(temp.get("codeValue").toString(), temp.get("code").toString());
            }
        }
        String post = POST_MAP.get(val.toString());
        return post == null ? "未知".intern() : post;
    }

    /**
     * 个人登录方式
     *
     * @param val
     * @return
     */
    public static synchronized String loginType(Object val) {
        if (null == val || StringUtils.isEmpty(val.toString())) {
            return "未知".intern();
        }
        if (POST_MAP.isEmpty()) {
            Map<String, Object> whereMap = new HashMap<>();
            whereMap.put("_parent_type", "530a04a2-d696-11e8-8a6d-000c29d87e13");
            for (Map<String, Object> temp : querySingleTable("base_dict_all", whereMap)) {
                POST_MAP.put(temp.get("code").toString(), temp.get("codeValue").toString());
                POST_MAP.put(temp.get("codeValue").toString(), temp.get("code").toString());
            }
        }
        String post = POST_MAP.get(val.toString());
        return post == null ? "未知".intern() : post;
    }

//    /**
//     * 应用系统
//     *
//     * @param val
//     * @return
//     */
//    public static synchronized String baseSysinfo(Object val) {
//        if (null == val || StringUtils.isEmpty(val.toString())) {
//            return "未知".intern();
//        }
//        if (SYS_MAP.isEmpty()) {
//            BaseSysinfoServiceImpl baseSysinfoService = VapXcApplication.getApplicationContext().getBean(BaseSysinfoServiceImpl.class);
//            BaseSysinfoQuery record = new BaseSysinfoQuery();
//            record.setMyStart(0);
//            record.setMyCount(9999);
//            record.setSystemId("hasSys");
//            List<BaseSysinfo> list = baseSysinfoService.queryBaseSysinfo(record).getList();
//            for (BaseSysinfo baseSysinfo : list) {
//                SYS_MAP.put(String.valueOf(baseSysinfo.getId()), baseSysinfo.getName());
//                SYS_MAP.put(baseSysinfo.getSystemId(), baseSysinfo.getName());
//            }
//        }
//        String sysName = SYS_MAP.get(val.toString());
//        return sysName == null ? "未知".intern() : sysName;
//    }
//
//
//    /**
//     * 应用系统- orgcode
//     *
//     * @param val
//     * @return
//     */
//    public static synchronized String baseSysinfoOrgCode(Object val) {
//        if (null == val || StringUtils.isEmpty(val.toString())) {
//            return "未知".intern();
//        }
//        if (SYS_ORG_MAP.isEmpty()) {
//            BaseSysinfoServiceImpl baseSysinfoService = VapXcApplication.getApplicationContext().getBean(BaseSysinfoServiceImpl.class);
//            BaseSysinfoQuery record = new BaseSysinfoQuery();
//            record.setMyStart(0);
//            record.setMyCount(9999);
//            List<BaseSysinfo> list = baseSysinfoService.queryBaseSysinfo(record).getList();
//            for (BaseSysinfo baseSysinfo : list) {
//                SYS_ORG_MAP.put(String.valueOf(baseSysinfo.getId()), baseSysinfo.getOrgCode());
//                SYS_ORG_MAP.put(baseSysinfo.getSystemId(), baseSysinfo.getOrgCode());
//            }
//        }
//        String orgCode = SYS_ORG_MAP.get(val.toString());
//        return orgCode == null ? "未知".intern() : orgCode;
//    }
//
//    /**
//     * 组织机构
//     *
//     * @param val
//     * @return
//     */
//    public static synchronized String orgCode2Name(Object val) {
//        if (null == val || StringUtils.isEmpty(val.toString())) {
//            return "未知".intern();
//        }
//        if (ORG_MAP.isEmpty()) {
//            BaseCommonServiceImpl baseCommonServiceImpl = VapXcApplication.getApplicationContext().getBean(BaseCommonServiceImpl.class);
//            List<BaseKoalOrg> list = baseCommonServiceImpl.getBaseKoalOrg().getList();
//            for (BaseKoalOrg baseKoalOrg : list) {
//                ORG_MAP.put(baseKoalOrg.getCode(), baseKoalOrg.getName());
//            }
//        }
//        String orgName = ORG_MAP.get(val.toString());
//        return orgName == null ? "未知".intern() : orgName;
//    }
//
    private static List<Map<String, Object>> querySingleTable(String table) {
        SingleTableServiceImpl stsi = VapXcApplication.getApplicationContext()
                .getBean(SingleTableServiceImpl.class);
        QueryModel queryModel = new QueryModel();
        queryModel.setTable(table);
        queryModel.setStart(0);
        queryModel.setCount(9999);
        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        if (singleTableModel.isPresent()) {
            Optional<VList<Map<String, Object>>> list = stsi.query(singleTableModel.get(), queryModel);
            if (list.isPresent()) {
                return list.get().getList();
            }
            return new ArrayList<>();
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * Map中传查询条件：表中字段前加：_（精确查询）*（模糊查询）<= >=（范围查询）
     *
     * @param
     * @return
     */
    private static List<Map<String, Object>> querySingleTable(String table, Map<String, Object> where) {
        SingleTableServiceImpl stsi = VapXcApplication.getApplicationContext()
                .getBean(SingleTableServiceImpl.class);
        QueryModel queryModel = new QueryModel();
        queryModel.setTable(table);
        queryModel.setStart(0);
        queryModel.setCount(9999);
        queryModel.setWhere(where);
        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        if (singleTableModel.isPresent()) {
            Optional<VList<Map<String, Object>>> listOpt = stsi.query(singleTableModel.get(), queryModel);
            if (listOpt.isPresent()) {
                VList<Map<String, Object>> list = listOpt.get();
                return list.getList();
            } else {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 软件进程 可疑状态
     *
     * @param val
     * @return
     */
    public static String processIsSuspicious(Object val) {
        return "1".equals(val) ? "可疑进程" : "正常进程";
    }

    /**
     * 软件进程维护 状态
     *
     * @param val
     * @return
     */
    public static String processState(Object val) {
        return "1".equals(val) ? "已维护" : "未维护";
    }

    /**
     * url 备案状态
     *
     * @param val
     * @return
     */
    public static String urlState(Object val) {
        return "1".equals(val) ? "已备案" : "未备案";
    }

    /**
     * url 有效/无效
     *
     * @param val
     * @return
     */
    public static String urlAlive(Object val) {
        return "1".equals(val) ? "有效" : "无效";
    }

}
