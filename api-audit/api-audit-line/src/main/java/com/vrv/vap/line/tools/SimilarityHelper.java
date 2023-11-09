package com.vrv.vap.line.tools;


import com.vrv.vap.line.model.FreScoreDto;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文本相似度相关判断方法
 * 参考链接：https://zhuanlan.zhihu.com/p/91645988
 * @author ylguo
 *
 */
public class SimilarityHelper {

    private static String separator = ",";//项分隔符
    /**
     * 汉明距离
     * @param a
     * @param b
     * @return
     */
    public static float hamming(String a, String b) {
        if (a == null || b == null) {
            return 0f;
        }
        if (a.length() != b.length()) {
            return 0f;
        }

        int disCount = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                disCount++;
            }
        }
        return (float) disCount / (float) a.length();
    }
    /**
     * 余弦相似性
     * @param a
     * @param b
     * @return
     */
    public static float cos(String a, String b) {
        if (a == null || b == null) {
            return 0F;
        }
        Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
        Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());

        // 统计字频
        Map<Integer, Integer> aMap = new HashMap<>();
        Map<Integer, Integer> bMap = new HashMap<>();
        for (Integer a1 : aChar) {
            aMap.put(a1, aMap.getOrDefault(a1, 0) + 1);
        }
        for (Integer b1 : bChar) {
            bMap.put(b1, bMap.getOrDefault(b1, 0) + 1);
        }

        // 向量化
        Set<Integer> union = SetUtils.union(aChar, bChar);
        int[] aVec = new int[union.size()];
        int[] bVec = new int[union.size()];
        List<Integer> collect = new ArrayList<>(union);
        for (int i = 0; i < collect.size(); i++) {
            aVec[i] = aMap.getOrDefault(collect.get(i), 0);
            bVec[i] = bMap.getOrDefault(collect.get(i), 0);
        }

        // 分别计算三个参数
        int p1 = 0;
        for (int i = 0; i < aVec.length; i++) {
            p1 += (aVec[i] * bVec[i]);
        }

        float p2 = 0f;
        for (int i : aVec) {
            p2 += (i * i);
        }
        p2 = (float) Math.sqrt(p2);

        float p3 = 0f;
        for (int i : bVec) {
            p3 += (i * i);
        }
        p3 = (float) Math.sqrt(p3);

        return ((float) p1) / (p2 * p3);
    }
    /**
     * 莱文斯坦距离
     * @param a
     * @param b
     * @return
     */
    public static float Levenshtein(String a, String b) {
        if (a == null && b == null) {
            return 1f;
        }
        if (a == null || b == null) {
            return 0F;
        }
        int editDistance = editDis(a, b);
        return 1 - ((float) editDistance / Math.max(a.length(), b.length()));
    }

    private static int editDis(String a, String b) {

        int aLen = a.length();
        int bLen = b.length();

        if (aLen == 0) return bLen;
        if (bLen == 0) return aLen;

        int[][] v = new int[aLen + 1][bLen + 1];
        for (int i = 0; i <= aLen; ++i) {
            for (int j = 0; j <= bLen; ++j) {
                if (i == 0) {
                    v[i][j] = j;
                } else if (j == 0) {
                    v[i][j] = i;
                } else if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    v[i][j] = v[i - 1][j - 1];
                } else {
                    v[i][j] = 1 + Math.min(v[i - 1][j - 1], Math.min(v[i][j - 1], v[i - 1][j]));
                }
            }
        }
        return v[aLen][bLen];
    }
    /**
     * Jaccard 相似度
     * @param a
     * @param b
     * @return
     */
    public static float jaccard(String a, String b) {
        if (a == null && b == null) {
            return 1f;
        }
        // 都为空相似度为 1
        if (a == null || b == null) {
            return 0f;
        }
        Set<Integer> aChar = a.chars().boxed().collect(Collectors.toSet());
        Set<Integer> bChar = b.chars().boxed().collect(Collectors.toSet());
        // 交集数量
        int intersection = SetUtils.intersection(aChar, bChar).size();
        if (intersection == 0) return 0;
        // 并集数量
        int union = SetUtils.union(aChar, bChar).size();
        return ((float) intersection) / (float)union;
    }
    /**
     * Sorensen Dice 相似度系数
     * @param a
     * @param b
     * @return
     */
    public static float SorensenDice(String a, String b) {
        if (a == null && b == null) {
            return 1f;
        }
        if (a == null || b == null) {
            return 0F;
        }
        Set<Integer> aChars = a.chars().boxed().collect(Collectors.toSet());
        Set<Integer> bChars = b.chars().boxed().collect(Collectors.toSet());
        // 求交集数量
        int intersect = SetUtils.intersection(aChars, bChars).size();
        if (intersect == 0) {
            return 0F;
        }
        // 全集，两个集合直接加起来
        int aSize = aChars.size();
        int bSize = bChars.size();
        return (2 * (float) intersect) / ((float) (aSize + bSize));
    }

    public static float similarityAvg(String a,String b){
        float i = SimilarityHelper.cos(a, b);
        float j =  SimilarityHelper.Levenshtein(a,b);
        float u =  SimilarityHelper.jaccard(a,b);
        float k =  SimilarityHelper.SorensenDice(a,b);
        return (i+j+u+k)/4;
    }

    public static float similarityMini(String a,String b){
        float i = SimilarityHelper.cos(a, b);
        float j =  SimilarityHelper.Levenshtein(a,b);
        float u =  SimilarityHelper.jaccard(a,b);
        float k =  SimilarityHelper.SorensenDice(a,b);
        float min = i;
        if(j < min){
            min = j;
        }
        if(u < min){
            min = u;
        }
        if(k < min){
            min = k;
        }
        return min;
    }

    public static List<String> str2list(String a){
        List<String> result = new ArrayList<>();
        for(String s : a.split(separator)){
            result.add(s);
        }
        return result;
    }

    public static Set<String> str2set(String a){
        Set<String> result = new HashSet<>();
        for(String s : a.split(separator)){
            result.add(s);
        }
        return result;
    }

    public static float jaccard4list(String a, String b) {
        if (StringUtils.isEmpty(a) && StringUtils.isEmpty(b)) {
            return 1f;
        }
        // 都为空相似度为 1
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            return 0f;
        }
        Set<String> astrings = str2set(a);
        Set<String> bstrings = str2set(b);
        // 交集数量
        astrings.retainAll(bstrings);
        int intersection = astrings.size();
        if (intersection == 0) return 0;
        // 并集数量
        astrings = str2set(a);
        astrings.removeAll(bstrings);
        astrings.addAll(bstrings);
        int union = astrings.size();
        return ((float) intersection) / (float)union;
    }

    public static FreScoreDto jaccard4list2(String a, String b) {
        FreScoreDto dto = new FreScoreDto();
        if (StringUtils.isEmpty(a) && StringUtils.isEmpty(b)) {
            dto.setScore(1f);
            return dto;
        }
        // 都为空相似度为 1
        if (StringUtils.isEmpty(a) || StringUtils.isEmpty(b)) {
            dto.setScore(0f);
            return dto;
        }
        Set<String> astrings = str2set(a);
        Set<String> bstrings = str2set(b);
        // 交集数量
        astrings.retainAll(bstrings);
        int intersection = astrings.size();
        dto.getFrequents().addAll(astrings);
        if (intersection == 0) {
            dto.setScore(0f);
            return dto;
        }
        // 并集数量
        astrings = str2set(a);
        astrings.removeAll(bstrings);
        astrings.addAll(bstrings);
        int union = bstrings.size();
        float s = ((float) intersection) / (float)union;
        dto.setScore(s);
        return dto;
    }

/*
    public static Double cosSimilarityByString(String first, String second) {
        try {
            Map<String, Integer> firstTfMap = TfIdfAlgorithm.segStr(first);
            Map<String, Integer> secondTfMap = TfIdfAlgorithm.segStr(second);
            if (firstTfMap.size() < secondTfMap.size()) {
                Map<String, Integer> temp = firstTfMap;
                firstTfMap = secondTfMap;
                secondTfMap = temp;
            }
            return cosSimilarCalc((LinkedHashMap<String, Integer>) firstTfMap,
                    (LinkedHashMap<String, Integer>) secondTfMap);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0d;
    }*/

    private static Double cosSimilarCalc(LinkedHashMap<String, Integer> first,
                                         LinkedHashMap<String, Integer> second) {

        List<Map.Entry<String, Integer>> firstList = new ArrayList<Map.Entry<String, Integer>>(
                first.entrySet());
        List<Map.Entry<String, Integer>> secondList = new ArrayList<Map.Entry<String, Integer>>(
                second.entrySet());
        // similar calculation
        double vectorFirstModulo = 0.00;// mod of first vector
        double vectorSecondModulo = 0.00;// mod of second vector
        double vectorProduct = 0.00; // mul of vector
        int secondSize = second.size();
        for (int i = 0; i < firstList.size(); i++) {
            if (i < secondSize) {
                vectorSecondModulo += secondList.get(i).getValue().doubleValue()
                        * secondList.get(i).getValue().doubleValue();
                vectorProduct += firstList.get(i).getValue().doubleValue()
                        * secondList.get(i).getValue().doubleValue();
            }
            vectorFirstModulo += firstList.get(i).getValue().doubleValue()
                    * firstList.get(i).getValue().doubleValue();
        }
        return vectorProduct / (Math.sqrt(vectorFirstModulo) * Math.sqrt(vectorSecondModulo));
    }



    public static float GetMaxSubStr(String Str, String subStr) {
        int iSize = Str.length();
        int jSize = subStr.length();
        float minsize = jSize;


        char[] strChar = Str.toCharArray();
        char[] subStrChar = subStr.toCharArray();

        int len = iSize > jSize ? iSize : jSize;	// 找出比较长的串的大小
        int MaxSize = 0;	// 最大的公共子串大小

        // 动态规划思想
        int[][] arr = new int[len+1][len+1];
        for (int i = 1; i <= iSize; i++)
        {
            for (int j = 1; j <= jSize; j++)
            {
                // 判断当前匹配的字符是否相等
                if (strChar[i - 1] == subStrChar[j - 1])
                {
                    // 当前的匹配结果  等于前一个字符 i - 1 j - 1　匹配结果 + 1
                    arr[i][j] = arr[i - 1][j - 1] + 1;

                    if (MaxSize < arr[i][j]) 	 // 时刻更新最大的公共子串大小
                        MaxSize = arr[i][j];
                }
            }
        }
        if(minsize == 0){
            return 0f;
        }
        return MaxSize/minsize;		// 返回最大值
    }


    public static void main(String[] args) {
        String s = "abcdefg";
        String s2 = "pbcdedfcdefg";
        System.out.println(GetMaxSubStr(s,s2));
    }
}


