package com.vrv.vap.alarmdeal.business.asset.util;

import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import org.apache.commons.lang3.StringUtils;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 资产检验工具类
 *
 * 2022-06-27
 */
public class AssetValidateUtil {

    // USB存储介质、USB外设treeCode
    private static List<String> usbGroopTreeCodes = Arrays.asList("asset-USBMemory-","asset-USBPeripheral-");

    private static String hostGroopTreeCodes = "asset-Host-";


    private static List<String> termTypeCodes = Arrays.asList("1", "2"); // 是否国产
    private static List<String> isMonitorAgentCodes = Arrays.asList("1", "2"); // 是安装终端客户端
    private static List<String> terminalTypeCodes = Arrays.asList("1", "2"); // 终端类型

    private static Map<String,String> termTypes = new HashMap<>();   // 是否国产：1：表示国产 2：非国产
    private static Map<String,String> isMonitorAgents = new HashMap<>();   // 是否安装终端客户端 1.已安装；2.未安装
    private static Map<String,String> terminalTypes = new HashMap<>();   // 终端类型: 1. 用户终端,2.运维终端

    public static Map<String,String> worths = new HashMap<>();   // 资产价值五性值


    static {
        termTypes.put("是","1");
        termTypes.put("否","2");
        isMonitorAgents.put("已安装","1");
        isMonitorAgents.put("未安装","2");
        terminalTypes.put("用户终端","1");
        terminalTypes.put("运维终端","2");
        worths.put("基本无损害","1");
        worths.put("轻度损害","2");
        worths.put("中度损害","3");
        worths.put("严重损害","4");
        worths.put("致命损害","5");
    }

    /**
     * 判断是否国产的code是否符合要求
     *
     */

    public static boolean termTypeCodeValidate(String code){
         if(termTypeCodes.contains(code)){
             return true;
         }
         return  false;
    }

    /**
     *
     * 获取是否国产的code
     *
     */

    public static String getTermTypeCodeByValue(String value){
        return  termTypes.get(value);
    }

    /**
     * 判断是安装终端客户端的code是否符合要求
     *
     */

    public static boolean isMonitorAgentCodeValidate(String code){
        if(isMonitorAgentCodes.contains(code)){
            return true;
        }
        return  false;
    }

    /**
     * 获取是安装终端客户端的code
     *
     */

    public static String getIsMonitorAgentValueValidate(String value){
        return  isMonitorAgents.get(value);
    }

    /**
     * 判断终端类型的code是否符合要求
     *
     */

    public static boolean terminalTypeCodeValidate(String code){
        if(terminalTypeCodes.contains(code)){
            return true;
        }
        return  false;
    }

    /**
     * 获取终端类型的code
     *
     */

    public static String getTerminalTypeCodeByValue(String value){
       return terminalTypes.get(value);
    }

    /**
     * 判断是不是USB存储介质、USB外设
     * @param assetTypetreeCode
     * @return
     */
    public static boolean isUsb(String assetTypetreeCode) {
        for(String treeCode : usbGroopTreeCodes){
            if(assetTypetreeCode.contains(treeCode)){
                return true;
            }
        }
        return false;
    }
    /**
     * 判断是不是终端
     * @param assetTypetreeCode
     * @return
     */
    public static boolean isHost(String assetTypetreeCode) {
        if(assetTypetreeCode.contains(hostGroopTreeCodes)){
            return true;
        }
        return false;
    }


    /**
     * ip格式校验
     * @param ip
     * @return
     */
    public static Result<String> ipFormat(String ip){
        // 为空不处理
        if (StringUtils.isEmpty(ip)) {
            return ResultUtil.success("true");
        }
        boolean checkIPResult = AssetUtil.checkIP(ip);
        if(!checkIPResult){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "ip格式异常");
        }
        return ResultUtil.success("true");
    }
    /**
     * mac格式校验
     * @param mac
     * @return
     */
    public static Result<String> macFormat(String mac){
        // 为空不处理
        if (StringUtils.isEmpty(mac)) {
            return ResultUtil.success("true");
        }
        // mac格式校验
        String macRegex = "((([a-f0-9]{2}:){5})|(([a-f0-9]{2}-){5}))[a-f0-9]{2}|((([A-F0-9]{2}:){5})|(([A-F0-9]{2}-){5}))[A-F0-9]{2}";
        // 格式校验
        if (!mac.matches(macRegex)) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "MAC地址格式异常");
        }
        return ResultUtil.success("true");
    }

    /**
     * 序列号格式校验：不允许输入汉字和特殊字符
     * @param serialNumber
     * @return
     */
    public static Result<String> serialNumberFormat(String serialNumber){
        // 为空不处理
        if (StringUtils.isEmpty(serialNumber)) {
            return ResultUtil.success("true");
        }
        String regEx = "[\\u4e00-\\u9fa5]";  // 汉字
        Pattern pattern = Pattern.compile(regEx);
        Matcher match = pattern.matcher(serialNumber);
        if(match.find()){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"序列号存在汉字！");
        }
        regEx="[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";//特殊字符
        Pattern patter = Pattern.compile(regEx);
        Matcher matc = patter.matcher(serialNumber);
        if(matc.find()){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"序列号存在特殊字符！");
        }
        return ResultUtil.success("true");
    }

    /**
     * 通过二级资产类型treeCode获取一级资产类型treeCode
     * @param assetTypeTreeCode
     * @return
     */
    public static String typeTreeCodeToGroupTreeCode(String assetTypeTreeCode) {
        if (StringUtils.isEmpty(assetTypeTreeCode)) {
            return null;
        }
        // 获取一级资产类型treeCode
        int indexTwo = assetTypeTreeCode.lastIndexOf('-');
        if (-1 == indexTwo) {
            return null;
        }
        String assetTypeGroupTreeCode = assetTypeTreeCode.substring(0, indexTwo);
        return assetTypeGroupTreeCode;

    }
    /**
     * 资产价值五性值：中文转义处理
     */
    public static String worthCodeByName(String name){
       String code= worths.get(name);
       if(StringUtils.isNotEmpty(code)){
           return code;
       }
       return "0";
    }
}
