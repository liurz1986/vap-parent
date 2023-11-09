package com.vrv.vap.line.tools;

import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.model.BaseLineFrequentAttr;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class SplitTools {

    private String ukey;

    public SplitTools(String ukey) {
        this.ukey = ukey;
    }

    public SplitTools() {
    }

    public List<List<List<Map<String, String>>>> towLevelSplit(List<Map<String, String>> datas){
        List<List<List<Map<String, String>>>> result = new ArrayList<>();
        //一级切分
        List<List<Map<String, String>>> firstSplit = new ArrayList<>();
        List<Map<String, String>> temp = new ArrayList<>();
        int i = 0;
        for(Map<String, String> mp : datas){
            i++;
            if(temp.size() == 0){
                temp.add(mp);
            }else{
                Date predate = TimeTools.parseDate(temp.get(temp.size()-1).get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                Date nowdate = TimeTools.parseDate(mp.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                if(nowdate.after(MyTimeTools.addMini(predate,LineConstants.SQ.towLevelTimeSplit))){
                    firstSplit.add(temp);
                    temp = new ArrayList<>();
                }
                temp.add(mp);
            }
            if(i == datas.size()){
                firstSplit.add(temp);
            }
        }
        //二级切分
        for(List<Map<String, String>> itemdata  : firstSplit){
            StringBuffer item = new StringBuffer();
            Date preDate = null;
            Date nextDate = null;
            List<Map<String,String>> temp2 = new ArrayList<>();
            int j = 0;
            List<List<Map<String,String>>> twolevel = new ArrayList<>();
            for(Map<String, String> mp : itemdata){
                j++;
                if(mp.containsKey(LineConstants.SQ.urlField) && StringUtils.isNotEmpty(mp.get(LineConstants.SQ.urlField))){
                    if(j == 1){
                        temp2.add(mp);
                    }else{
                        Date predate = TimeTools.parseDate(temp2.get(temp2.size()-1).get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                        Date nowdate = TimeTools.parseDate(mp.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                        if(nowdate.after(MyTimeTools.addSecond(predate,LineConstants.SQ.timeSplit))){
                            twolevel.add(temp2);
                            temp2 = new ArrayList<>();
                        }
                        temp2.add(mp);
                    }
                }
                if(j == itemdata.size()){
                    twolevel.add(temp2);
                }
            }
            result.add(twolevel);
        }
        return result;
    }

    public void printTowLevel(List<Map<String, String>> datas){
        towLevelSplit(datas).forEach(l ->{
            System.out.println("#####################");
            l.forEach(i ->{
                i.forEach(u ->{
                    String ul = StringUtil.filterParam(u.get(LineConstants.SQ.urlField).toString());
                    String time = u.get(LineConstants.SQ.timeField).toString();
                    System.out.println(time+">>>"+ul);
                });
                System.out.println("");
            });
            System.out.println("#####################");
        });
    }

    public List<List<List<Map<String, String>>>> towLevelSplit(Iterator<Map<String, String>> iterator){
        List<List<List<Map<String, String>>>> result = new ArrayList<>();
        //一级切分
        List<List<Map<String, String>>> firstSplit = new ArrayList<>();
        List<Map<String, String>> temp = new ArrayList<>();
        while (iterator.hasNext()){
            Map<String, String> mp = iterator.next();
            if(temp.size() == 0){
                temp.add(mp);
            }else{
                Date predate = TimeTools.parseDate(temp.get(temp.size()-1).get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                Date nowdate = TimeTools.parseDate(mp.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                if(nowdate.after(MyTimeTools.addMini(predate,LineConstants.SQ.towLevelTimeSplit))){
                    firstSplit.add(temp);
                    temp = new ArrayList<>();
                }
                temp.add(mp);
            }
            if(!iterator.hasNext()){
                firstSplit.add(temp);
            }
        }
        //二级切分
        for(List<Map<String, String>> itemdata  : firstSplit){
            StringBuffer item = new StringBuffer();
            Date preDate = null;
            Date nextDate = null;
            List<Map<String,String>> temp2 = new ArrayList<>();
            int j = 0;
            List<List<Map<String,String>>> twolevel = new ArrayList<>();
            for(Map<String, String> mp : itemdata){
                j++;
                if(mp.containsKey(LineConstants.SQ.urlField) && StringUtils.isNotEmpty(mp.get(LineConstants.SQ.urlField))){
                    if(j == 1){
                        temp2.add(mp);
                    }else{
                        Date predate = TimeTools.parseDate(temp2.get(temp2.size()-1).get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                        Date nowdate = TimeTools.parseDate(mp.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1);
                        if(nowdate.after(MyTimeTools.addSecond(predate,LineConstants.SQ.timeSplit))){
                            twolevel.add(temp2);
                            temp2 = new ArrayList<>();
                        }
                        temp2.add(mp);
                    }
                }
                if(j == itemdata.size()){
                    twolevel.add(temp2);
                }
            }
            result.add(twolevel);
        }
        return result;
    }

    public List<String> compressWithAttr(List<List<List<Map<String, String>>>> lists,List<BaseLineFrequentAttr> attrs){
        List<String> result = new ArrayList<>();
        lists.forEach(first ->{
            StringBuffer towStr = new StringBuffer();
            first.forEach(tow ->{
                StringBuffer str = new StringBuffer();
                int allhr = 0;
                float pckall = 0;
                List<String> urls = new ArrayList<>();
                for(Map<String,String> map : tow){
                    urls.add(StringUtil.filterParam(map.get(LineConstants.SQ.urlField)));
                    //频繁项属性计算
                    int hr = Integer.parseInt(TimeTools.format(TimeTools.parseDate(map.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1),"HH"));
                    allhr = allhr+hr;
                    String pck = map.get(LineConstants.SQ.pckField);
                    if(StringUtils.isNotEmpty(pck)){
                        pckall += Float.valueOf(pck);
                    }
                }
                Collections.sort(urls);
                urls.forEach(s ->{
                    str.append(s);
                });
                String startTime = tow.get(0).get(LineConstants.SQ.timeField);
                String endTime = tow.get(tow.size()-1).get(LineConstants.SQ.timeField);
                String id = Base64Util.compressString(str.toString());
                if(towStr.length() != 0){
                    towStr.append(LineConstants.SQ.separator);
                }
                towStr.append(id);
                int hour = Math.round(allhr/tow.size());
                attrs.add(new BaseLineFrequentAttr(id,hour,pckall,startTime,endTime,this.ukey));
            });
            if(StringUtils.isNotEmpty(towStr.toString())){
                result.add(towStr.toString());
            }
        });
        return result;
    }

    public List<String> compress(List<List<List<Map<String, String>>>> lists){
        List<String> result = new ArrayList<>();
        lists.forEach(first ->{
            StringBuffer towStr = new StringBuffer();
            first.forEach(tow ->{
                if(towStr.length() != 0){
                    towStr.append(LineConstants.SQ.separator);
                }
                towStr.append(StringUtil.compressList(tow));
            });
            if(StringUtils.isNotEmpty(towStr.toString())){
                result.add(towStr.toString());
            }
        });
        return result;
    }

    public List<String> splitAndCompressWithAttr(List<Map<String, String>> datas, List<BaseLineFrequentAttr> attrs){
        return compressWithAttr(towLevelSplit(datas),attrs);
    }

    public List<String> splitAndCompressWithAttr(Iterator<Map<String, String>> iterator, List<BaseLineFrequentAttr> attrs){
        return compressWithAttr(towLevelSplit(iterator),attrs);
    }

    public List<BaseLineFrequentAttr> buildAttrs(List<Map<String, String>> datas){
        List<List<List<Map<String, String>>>> lists = this.towLevelSplit(datas);
        List<BaseLineFrequentAttr> attrs = new ArrayList<>();
        lists.forEach(first ->{
            StringBuffer towStr = new StringBuffer();
            first.forEach(tow ->{
                StringBuffer str = new StringBuffer();
                int allhr = 0;
                float pckall = 0;
                List<String> urls = new ArrayList<>();
                for(Map<String,String> map : tow){
                    urls.add(StringUtil.filterParam(map.get(LineConstants.SQ.urlField)));
                    //频繁项属性计算
                    int hr = Integer.parseInt(TimeTools.format(TimeTools.parseDate(map.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1),"HH"));
                    allhr = allhr+hr;
                    String pck = map.get(LineConstants.SQ.pckField);
                    if(StringUtils.isNotEmpty(pck)){
                        pckall += Float.valueOf(pck);
                    }
                }
                Collections.sort(urls);
                urls.forEach(s ->{
                    str.append(s);
                });
                String startTime = tow.get(0).get(LineConstants.SQ.timeField);
                String endTime = tow.get(tow.size()-1).get(LineConstants.SQ.timeField);
                String id = Base64Util.compressString(str.toString());
                if(towStr.length() != 0){
                    towStr.append(LineConstants.SQ.separator);
                }
                towStr.append(id);
                int hour = Math.round(allhr/tow.size());
                attrs.add(new BaseLineFrequentAttr(id,hour,pckall,startTime,endTime,this.ukey));
            });
        });
        return attrs;
    }

    public List<BaseLineFrequentAttr> buildAttrsBylist(List<List<List<Map<String, String>>>> lists){
        System.out.println("构造单项集开始时间："+System.currentTimeMillis());
        List<BaseLineFrequentAttr> attrs = new ArrayList<>();
        lists.forEach(first ->{
            StringBuffer towStr = new StringBuffer();
            first.forEach(tow ->{
                StringBuffer str = new StringBuffer();
                int allhr = 0;
                float pckall = 0;
                List<String> urls = new ArrayList<>();
                for(Map<String,String> map : tow){
                    urls.add(StringUtil.filterParam(map.get(LineConstants.SQ.urlField)));
                    //频繁项属性计算
                    int hr = Integer.parseInt(TimeTools.format(TimeTools.parseDate(map.get(LineConstants.SQ.timeField), TimeTools.TIME_FMT_1),"HH"));
                    allhr = allhr+hr;
                    String pck = map.get(LineConstants.SQ.pckField);
                    if(StringUtils.isNotEmpty(pck)){
                        pckall += Float.valueOf(pck);
                    }
                }
                Collections.sort(urls);
                urls.forEach(s ->{
                    str.append(s);
                });
                String startTime = tow.get(0).get(LineConstants.SQ.timeField);
                String endTime = tow.get(tow.size()-1).get(LineConstants.SQ.timeField);
                String id = Base64Util.compressString(str.toString());
                if(towStr.length() != 0){
                    towStr.append(LineConstants.SQ.separator);
                }
                towStr.append(id);
                int hour = Math.round(allhr/tow.size());
                attrs.add(new BaseLineFrequentAttr(id,hour,pckall,startTime,endTime,this.ukey));
            });
        });
        lists.clear();
        lists = null;
        System.out.println("构造单项集结束时间："+System.currentTimeMillis());
        return attrs;
    }
}
