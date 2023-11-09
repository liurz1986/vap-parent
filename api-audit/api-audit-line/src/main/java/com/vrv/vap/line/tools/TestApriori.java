package com.vrv.vap.line.tools;

import java.util.*;

public class TestApriori {

    private static Date addTowLevelTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,10);
        return cal.getTime();
    }

    public static void main(String[] args) {
        Map<List<String>,Integer> sItem1FCMap = new HashMap<>();

        List<String> itemList = new ArrayList<>();
        itemList.add("abc");
        sItem1FCMap.put(itemList,1);

        List<String> itemList2 = new ArrayList<>();
        itemList2.add("abc");
        System.out.println(sItem1FCMap.get(itemList2));
        sItem1FCMap.put(itemList2,2);
        System.out.println(sItem1FCMap.get(itemList2));

    }
}

