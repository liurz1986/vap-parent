package com.vrv.vap.alarmdeal.business.flow.core.constant;
import java.util.HashMap;
import java.util.Map;

public class MyTicketConstant {
    public static final String TICKETTYPEONE = "1";  // 内部流程
    public static final String TICKETTYPETWO = "2";   // 外部流程

    public static final String TICKETTYPEONEDESC = "内部流程";  // 内部流程
    public static final String TICKETTYPETWODESC = "外部流程";   // 外部流程

    public static   Map<String,String> datas ;

    static{
        datas = new HashMap<>();
        datas.put(TICKETTYPEONEDESC,TICKETTYPEONE);
        datas.put(TICKETTYPETWODESC,TICKETTYPETWO);
    }

    /**
     * 判断菜单节点是否外内部工单菜单节点
     * @param nodeName
     * @return
     */
    public static  boolean isNodeTrue(String nodeName){
        if(MyTicketConstant.TICKETTYPEONEDESC.equals(nodeName) || MyTicketConstant.TICKETTYPETWODESC.equals(nodeName)){
            return true;
        }
        return false;
    }

    public static String queryCodeByName(String name){
        return datas.get(name);
    }
}
