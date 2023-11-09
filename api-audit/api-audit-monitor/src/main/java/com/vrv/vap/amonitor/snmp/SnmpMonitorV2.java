package com.vrv.vap.amonitor.snmp;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.amonitor.command.MonitorRunnerV2;
import com.vrv.vap.amonitor.entity.Monitor2AssetInfo;
import com.vrv.vap.amonitor.model.Monitor2Setting;
import com.vrv.vap.amonitor.model.Monitor2SnmpInfo;
import com.vrv.vap.amonitor.model.MonitorDataInfo;
import com.vrv.vap.amonitor.model.OidAlgEx;
import com.vrv.vap.amonitor.snmp.impl.CommonSnmpMonitorV2;
import com.vrv.vap.amonitor.snmp.impl.TestSnmpMonitorV2;
import com.vrv.vap.amonitor.tools.IpTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.CommunityTarget;
import org.snmp4j.Snmp;
import org.snmp4j.Target;
import org.snmp4j.UserTarget;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.Priv3DES;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class SnmpMonitorV2 implements ISnmpMonitor, SnmpAlgo {

    private final static String MONITOR_INDEX_PREFIX = "monitor-asset-v2-";

    protected final static String MONITOR_DATA_INDEX = MONITOR_INDEX_PREFIX;
    protected final static String MONITOR_DATA_TOPIC = MONITOR_INDEX_PREFIX;
    protected final static String CONNECT_INDEX = MONITOR_INDEX_PREFIX + "connect";

    protected final static ObjectMapper OBJECT_MAPPER;
    protected JexlEngine jexlEngine;

    static {
        OBJECT_MAPPER = new ObjectMapper();
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private static final Log log = LogFactory.getLog(SnmpMonitorV2.class);

    /**
     * 配置发生改变
     */
    protected boolean settingChanged = false;

    /**
     * snmp开启状态
     */
    protected boolean snmpOpen = true;

    protected Monitor2Setting setting;

    protected Monitor2SnmpInfo snmpInfo;

    protected Snmp snmp;

    protected Target target;

    public SnmpMonitorV2(Monitor2Setting setting) throws IOException {
        this.setting = setting;
        if (setting.getOidAlgs() == null) {
            snmpOpen = false;
            return;
        }
        jexlEngine = new JexlEngine();
        this.snmpInfo = OBJECT_MAPPER.readValue(setting.getAssetInfo().getMonitorSetting(), Monitor2SnmpInfo.class);

        String communityName = snmpInfo.getCommunityName();
        String hostIp = setting.getAssetInfo().getDevIp();
        int version = SnmpConstants.version2c;
        switch (snmpInfo.getSnmpVersion()) {
            case 1:
                version = SnmpConstants.version1;
                break;
            case 2:
                version = SnmpConstants.version2c;
                break;
            case 3:
                version = SnmpConstants.version3;
                break;
        }
        try {
            DefaultUdpTransportMapping dm = new DefaultUdpTransportMapping();
            dm.setSocketTimeout(2000);
            snmp = new Snmp(dm);
            if (version == SnmpConstants.version3) {
                USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(MPv3.createLocalEngineID()), 0);
                SecurityModels.getInstance().addSecurityModel(usm);
            }

            snmp.listen();
            OID authId = "HMAC_MD5_96".equals(snmpInfo.getPassEncWay()) ? AuthMD5.ID : AuthSHA.ID;
            OID privId = "CBC_DES".equals(snmpInfo.getPrivacyEncWay()) ? PrivDES.ID : ("3DES".equals(snmpInfo.getPrivacyEncWay()) ? Priv3DES.ID : PrivAES128.ID);
            if (version == SnmpConstants.version3) {
                UsmUser user = new UsmUser(new OctetString(snmpInfo.getUser()), authId, new OctetString(snmpInfo.getAuthPassphrase()), privId, new OctetString(snmpInfo.getPrivacyPassphrase()));
                snmp.getUSM().addUser(user);
            }

        } catch (IOException e) {
            throw e;
        }
        target = version == SnmpConstants.version3 ? new UserTarget() : new CommunityTarget();

        target.setVersion(version);
        if (version == SnmpConstants.version3) {
            target.setSecurityLevel(snmpInfo.getSecurityLevel());
            target.setSecurityName(new OctetString(snmpInfo.getUser()));
        } else {
            target.setSecurityName(new OctetString(communityName));
        }
        target.setAddress(new UdpAddress(hostIp + "/" + snmpInfo.getSnmpPort()));
        target.setTimeout(2000);
        target.setRetries(1);

    }

    /**
     * 根据资产类型创建监控实例
     *
     * @param assetInfo
     * @return
     * @throws IOException
     */
    public static SnmpMonitorV2 createMonitor(Monitor2Setting assetInfo) throws IOException {
        return new CommonSnmpMonitorV2(assetInfo);
    }

    public static SnmpMonitorV2 createTestMonitor(Monitor2AssetInfo assetInfo) throws IOException {
        Monitor2Setting setting = new Monitor2Setting();
        setting.setAssetInfo(assetInfo);
        setting.setOidAlgs(Collections.EMPTY_LIST);
        return new TestSnmpMonitorV2(setting);
    }

    public Object execute(OidAlgEx oidAlgEx) {
        JexlContext jc = new MapContext();
        //"Algo"参数指定调用的类或者其实例
        jc.set("Algo", this);
        jc.set("a", oidAlgEx.getOid());
        //evaluate会执行算式或者调用类方法
        return oidAlgEx.getEx().evaluate(jc);
    }

    @Override
    public boolean testSnmp() {
        try {
            VariableBinding[] snmpInfoNext = getSnmpInfoNext(".1");
            if (snmpInfoNext == null || snmpInfoNext.length == 0) {
                return false;
            }
            return snmpInfoNext[0].getVariable() != null;
        } catch (Exception e) {
            log.error("test snmp failed", e);
        }
        return false;
    }

    @Override
    public Object testSnmp(String... oids) {
        Map<String, Object> res = new HashMap<>();
        try {
            VariableBinding[] snmpInfoNext = getSnmpInfo(oids);
            if (snmpInfoNext == null || snmpInfoNext.length == 0) {
                List<VariableBinding[]> snmpInfo = tableSnmpInfo(oids);
                if (snmpInfo != null && !snmpInfo.isEmpty()) {
                    List<String> list = snmpInfo.stream().flatMap(v -> Arrays.stream(v).map(vb -> hexToString(vb.getVariable()))).collect(Collectors.toList());
                    res.put("res", list);
                    res.put("type", "walk");
                }
            } else {
                Object[] params = Arrays.stream(snmpInfoNext).map(v -> v.getVariable().toString()).toArray();
                res.put("res", params);
                res.put("type", "get");
            }

        } catch (Exception e) {
            res.put("res", e.getMessage());
            log.error("test snmp failed", e);
        }
        return res;
    }

    public Object testSnmp(String algo, String oids) {
        JexlContext jc = new MapContext();
        //"Algo"参数指定调用的类或者其实例
        jc.set("Algo", this);
        jc.set("a", oids);
        //evaluate会执行算式或者调用类方法
        Object val = jexlEngine.createExpression(algo).evaluate(jc);
        return val;
    }

    /**
     * 系统信息
     *
     * @return
     */
    public String sysInfo() {
        try {
            VariableBinding[] snmpInfoNext = getSnmpInfoNext(".1");
            if (snmpInfoNext == null || snmpInfoNext.length == 0) {
                return "";
            }
            return snmpInfoNext[0].getVariable().toString();
        } catch (Exception e) {
            log.error("get snmp info failed", e);
        }
        return "";
    }

    /**
     * 返回在线时长
     *
     * @return
     */
    @Override
    public String uptime() {
        try {
            VariableBinding[] snmpInfoNext = getSnmpInfo("1.3.6.1.2.1.1.3.0");
            if (snmpInfoNext == null || snmpInfoNext.length == 0) {
                return "";
            }
            return snmpInfoNext[0].getVariable().toString();
        } catch (Exception e) {
            log.error("get uptime failed", e);
        }
        return "";
    }

    public boolean canConnected() {
        return IpTools.ping(setting.getAssetInfo().getDevIp(), 5);
    }

    @Override
    public Object cal(String oid, String ex) {
        String[] oids = oid.split(",");
        VariableBinding[] snmpInfo = getSnmpInfo(oids);
        if (snmpInfo == null) {
            return null;
        }

        Double[] nums = new Double[oids.length];

        boolean valid = false;
        for (int i = 0; i < snmpInfo.length; i++) {
            Variable totalVariable = snmpInfo[i].getVariable();

            String value = totalVariable.toString().toLowerCase();
            String val = null;
            if (value.indexOf(":") > 0) {
                val = value.substring((value.indexOf(":") + 1), value.length() - 2).trim();
            } else {
                val = value.trim();
            }

            nums[i] = !totalVariable.isException() ? Double.parseDouble(val) : -1;
            if (nums[i] > -1) {
                valid = true;
            }
        }

        if (!valid) {
            return null;
        }

        Double total = (Double) evaluate(jexlEngine.createExpression(ex), nums);

        String format = "%.2f";
        if (total < 1) {
            format = "%.3f";
        }

        return String.format(format, total);
    }

    @Override
    public Object cal2(String oid, String ex) {
        String[] oids = oid.split(",");
        VariableBinding[] snmpInfo = getSnmpInfo(oids);
        if (snmpInfo == null) {
            return null;
        }

        String[] nums = new String[oids.length];

        boolean valid = false;
        for (int i = 0; i < snmpInfo.length; i++) {
            Variable totalVariable = snmpInfo[i].getVariable();

            String value = totalVariable.toString();
            String val = null;
            if (value.indexOf(":") > 0) {
                val = value.substring((value.indexOf(":") + 1), value.length() - 2).trim();
            } else {
                val = value.trim();
            }

            nums[i] = !totalVariable.isException() ? val : "-1";
            if (!"-1".equals(nums[i])) {
                valid = true;
            }
        }

        if (!valid) {
            return null;
        }

        Double total = Double.valueOf(evaluate(jexlEngine.createExpression(ex), nums).toString());

        String format = "%.2f";
        if (total < 1) {
            format = "%.3f";
        }

        return String.format(format, total);
    }

    @Override
    public Object common(String oid) {
        if (oid == null) {
            return null;
        }

        VariableBinding[] snmpInfo = getSnmpInfo(oid.split(","));
        if (snmpInfo == null) {
            return null;
        }
        Variable variable = snmpInfo[0].getVariable();
        if (variable.isException()) {
            return null;
        }

        return hexToString(variable);
    }

    @Override
    public Object cpuUseRate(String oid, String ex) {
        if (oid == null) {
            return null;
        }

        String[] cpuOids = oid.split(",");
        List<VariableBinding[]> snmpInfo = tableSnmpInfo(cpuOids[0]);
        if (snmpInfo == null || snmpInfo.isEmpty()) {
            return null;
        }

        double total = 0;
        int size = 1;
        for (VariableBinding[] variableBindings : snmpInfo) {
            size = snmpInfo.size();
            for (VariableBinding variableBinding : variableBindings) {
                total += Double.parseDouble(variableBinding.getVariable().toString().replace("%", ""));
            }
        }

        return String.format("%.1f", evaluate(jexlEngine.createExpression(ex), total, size));
    }

    @Override
    public Object list(String oid) {
        List<String> list = new ArrayList<>();
        if (oid == null) {
            return list;
        }

        String[] cpuOids = oid.split(",");
        List<VariableBinding[]> snmpInfo = tableSnmpInfo(cpuOids[0]);
        if (snmpInfo == null || snmpInfo.isEmpty()) {
            return list;
        }

        for (VariableBinding[] variableBindings : snmpInfo) {
            for (VariableBinding variableBinding : variableBindings) {
                Variable variable = variableBinding.getVariable();
                list.add(hexToString(variable));
            }
        }
        return list;
    }

    @Override
    public Object cpuCoreCount(String oid) {
        if (oid == null) {
            return null;
        }

        String[] cpuOids = oid.split(",");
        List<VariableBinding[]> snmpInfo = tableSnmpInfo(cpuOids[0]);
        if (snmpInfo == null || snmpInfo.isEmpty()) {
            return null;
        }

        int size = 1;
        if (snmpInfo != null && snmpInfo.size() > 0) {
            size = snmpInfo.size();
        }
        return size;
    }

    @Override
    public Object disk(String oid) {
        List<DiskPartionInfo> diskPartionInfos = new ArrayList<>();
        List<VariableBinding[]> snmpInfo = tableSnmpInfo(oid.split(","));
        if (snmpInfo == null) {
            return diskPartionInfos;
        }

        Map<String, VariableBinding[]> data = snmpInfo.stream().collect(Collectors.toMap(r -> hexToString(r[0].getVariable()), s -> s));

//        List<String> notPartion = Arrays.asList("Physical memory", "Virtual memory", "Memory buffers", "Cached memory", "Swap space", "Shared memory");
//        notPartion.forEach(k -> data.remove(k));

        if (data.isEmpty()) {
            return diskPartionInfos;
        }

        data.forEach((k, v) -> {
            VariableBinding[] variableBindings = data.get(k);
            Variable totalVariable = variableBindings[2].getVariable();
            Double diskTotal = !totalVariable.isException() ? Double.parseDouble(totalVariable.toString()) : -1;

            Variable useVariable = variableBindings[3].getVariable();
            Double diskUse = !useVariable.isException() ? Double.parseDouble(useVariable.toString()) : -1;

            Variable allocUnitVariable = variableBindings[1].getVariable();
            Double allocUnit = !useVariable.isException() ? Double.parseDouble(allocUnitVariable.toString()) : -1;

            /*if (diskTotal < 0) {
                return;
            }*/
            diskPartionInfos.add(new DiskPartionInfo(k, scale(diskTotal * allocUnit / 1024 / 1024 / 1024, 2), scale(diskUse * allocUnit / 1024 / 1024 / 1024, 2), "G"));
        });
        return diskPartionInfos;
    }

    @Override
    public Map<String, Object> memoryFromDisk(String oid) {
        Map<String, Object> res = new HashMap<>(4);
        //从磁盘信息中获取物理内存状态
        List<VariableBinding[]> snmpInfo = tableSnmpInfo(oid.split(","));
        if (snmpInfo == null) {
            return res;
        }

        Map<String, VariableBinding[]> data = snmpInfo.stream().collect(Collectors.toMap(r -> r[0].getVariable().toString().toLowerCase(), s -> s));
        final String PHYSICAL_MEMORY = "physical memory";
        if (!data.containsKey(PHYSICAL_MEMORY)) {
            return res;
        }
        VariableBinding[] variableBindings = data.get(PHYSICAL_MEMORY);
        Variable totalVariable = variableBindings[2].getVariable();
        Double memoryTotal = !totalVariable.isException() ? Double.parseDouble(totalVariable.toString()) : -1;

        Variable useVariable = variableBindings[3].getVariable();
        Double memoryUse = !useVariable.isException() ? Double.parseDouble(useVariable.toString()) : -1;

        Variable allocUnitVariable = variableBindings[1].getVariable();
        Double allocUnit = !useVariable.isException() ? Double.parseDouble(allocUnitVariable.toString()) : -1;

        Expression memoryTotalEx = jexlEngine.createExpression("v1/(1024*1024*1024)");
        Expression memoryUsedEx = jexlEngine.createExpression("v2/(1024*1024*1024)");
        Expression memoryFreeEx = jexlEngine.createExpression("(v1-v2)/(1024*1024*1024)");
        Object total = evaluate(memoryTotalEx, memoryTotal * allocUnit, memoryUse * allocUnit);
        Object free = evaluate(memoryFreeEx, memoryTotal * allocUnit, memoryUse * allocUnit);
        Object used = evaluate(memoryUsedEx, memoryTotal * allocUnit, memoryUse * allocUnit);

        res.put("memory_total", String.format("%.2f", total));
        res.put("memory_free", String.format("%.2f", free));
        res.put("memory_use", String.format("%.2f", used));
        res.put("memory_percent", String.format("%.1f", (Double) used / (Double) total * 100));
        return res;
    }

    @Override
    public Map<String, Object> memoryUSG400(String oid) {
        Map<String, Object> res = new HashMap<>(4);
        VariableBinding[] snmpInfo = getSnmpInfo(oid.split(","));
        if (snmpInfo == null) {
            return res;
        }

        Double memoryTotal;
        Double memoryFree;
        Double memoryUse;

        Variable totalVariable = snmpInfo[0].getVariable();
        if (totalVariable.isException()) {
            return res;
        }
        // 返回结果集_ramUsedRate:856208 used 1162252 free 2018460 total
        String temp = totalVariable.toString().trim();
        int usedindex = temp.indexOf("used");
        int freeindex = temp.indexOf("free");
        int totalindex = temp.indexOf("total");

        memoryUse = Double.parseDouble(temp.substring(0, usedindex).trim());
        memoryFree = Double.parseDouble(temp.substring(usedindex + 4, freeindex).trim());
        memoryTotal = Double.parseDouble(temp.substring(freeindex + 4, totalindex).trim());

        res.put("memory_total", String.format("%.2f", memoryTotal / 1024 / 1024));
        res.put("memory_free", String.format("%.2f", memoryFree / 1024 / 1024));
        res.put("memory_use", String.format("%.2f", memoryUse / 1024 / 1024));
        res.put("memory_percent", String.format("%.1f", (Double) memoryUse / (Double) memoryTotal * 100));
        return res;
    }

    protected boolean getConnectedStatus() {
        MonitorDataInfo connectedData = new MonitorDataInfo(CONNECT_INDEX, CONNECT_INDEX, new HashMap<>(4));
        connectedData.kv("dev_id", setting.getAssetInfo().getDevId()).kv("dev_ip", setting.getAssetInfo().getDevIp()).kv("index", CONNECT_INDEX).kv("event_time", TimeTools.format2(new Date()));
        connectedData.kv("dev_type", setting.getAssetInfo().getAssetType());
        connectedData.kv("sno", setting.getAssetInfo().getSnoUnicode());
        if (!canConnected()) {
            MonitorRunnerV2.pushConnectedData(connectedData.kv("reachable", 0));
            log.error("获取监控信息失败, IP不通: " + setting.getAssetInfo().getDevIp() + ", 时间:" + TimeTools.format2(new Date()));
            return false;
        }
        MonitorRunnerV2.pushConnectedData(connectedData.kv("reachable", 1));
        return true;
    }

    public Monitor2Setting getSetting() {
        return setting;
    }

    @Override
    public int getSnmpVersion() {
        return snmpInfo.getSnmpVersion();
    }

    @Override
    public Snmp getSnmp() {
        return snmp;
    }

    @Override
    public Target getTarget() {
        return target;
    }

    public boolean isSettingChanged() {
        return settingChanged;
    }

    public void setSettingChanged(boolean settingChanged) {
        this.settingChanged = settingChanged;
    }

    public static String hexToString(Variable variable) {
        if (variable instanceof OctetString) {
            String str = null;
            try {
                str = new String(((OctetString) variable).toByteArray(), "GB2312");
            } catch (UnsupportedEncodingException e) {
                log.error("", e);
                str = new String(((OctetString) variable).toByteArray());
            }
            return str.replaceAll("\\u0000", "");
        } else {
            return variable.toString();
        }

       /* String str = "";
        try {
            String[] temps = octetString.split(":");
            byte[] bs = new byte[temps.length];
            for (int i = 0; i < temps.length; i++) {
                if (octetString.length() == 17) {
                    str = str + temps[i] + "-";
                } else {
                    bs[i] = (byte) Integer.parseInt(temps[i], 16);
                }
            }
            if (octetString.length() == 17) {
                str = str.substring(0, str.length() - 1);
            } else {
                str = new String(bs, "GB2312");
            }
        } catch (Exception e) {
            return null;
        }*/
        //return str.replaceAll("\\u0000","");
    }

    /**
     * 保留小数位
     *
     * @param count
     * @param scale
     * @return
     */
    protected double scale(Double count, int scale) {
        return new BigDecimal(count).setScale(scale, RoundingMode.UP).doubleValue();
    }

    protected static class DiskPartionInfo {
        private String name;
        private double total;
        private double used;
        private String unit;

        public DiskPartionInfo(String name, double total, double used, String unit) {
            this.name = name;
            this.total = total;
            this.used = used;
            this.unit = unit;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getUsed() {
            return used;
        }

        public void setUsed(double used) {
            this.used = used;
        }

        public String getUnit() {
            return unit;
        }

        public void setUnit(String unit) {
            this.unit = unit;
        }
    }
}
