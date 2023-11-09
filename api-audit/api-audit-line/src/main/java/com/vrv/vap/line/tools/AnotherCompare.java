package com.vrv.vap.line.tools;

import java.util.*;



/*
 * 计算两个字符串(英文字符)的相似度，简单的余弦计算，未添权重
 */


public class AnotherCompare {

    public static float cosListFromString(String s1,String s2){
        return new Double(cosList(Arrays.asList(s1.split(",")),Arrays.asList(s2.split(",")))).floatValue();
    }

    public static double cosList(List<String> string1, List<String> string2) {

        Map<String, int[]> vectorMap = new HashMap<String, int[]>();

        int[] tempArray = null;

        for (String character1 : string1) {  //拆解为向量
            if (vectorMap.containsKey(character1)) {   //该字符是否已经存在map中
                vectorMap.get(character1)[0]++;   //key对应的值是一个数组，第一个字符串的字符出现次数存储在数组第一个位置
            } else {
                tempArray = new int[2];
                tempArray[0] = 1;  //如果字符没有出现过，就添加到map中，数组第一个位置设为1
                tempArray[1] = 0;
                vectorMap.put(character1, tempArray);
            }
        }
        for (String character2 : string2) {  //拆解为向量
            if (vectorMap.containsKey(character2)) {
                vectorMap.get(character2)[1]++;
            } else {
                tempArray = new int[2];
                tempArray[0] = 0;
                tempArray[1] = 1;  //第二个字符串的字符出现次数存储在数组的第二个位置
                vectorMap.put(character2, tempArray);
            }
        }

        double result = 0;
        result = pointMulti(vectorMap) / sqrtMulti(vectorMap);
        return result;    //余弦度结果返回,因为是余弦，返回结果越大，夹角越小，两个向量方向越接近，即两个字符串越相似
        /*
         * 到最后，map里的key对应的数组，第一个位置存储的是第一个字符串字符对应的向量，第二个位置存储的是第二个字符串字符对应的向量。
         */
    }

    //求平方根
    private static double sqrtMulti(Map<String, int[]> paramMap) {
        double result = 0;
        result = squares(paramMap);  //先求平方和
        result = Math.sqrt(result);  //再开根号，就是求模
        return result;
    }

    // 求平方和，分母上，向量求模的平方
    private static double squares(Map<String, int[]> paramMap) {
        double result1 = 0;
        double result2 = 0;
        Set<String> keySet = paramMap.keySet();
        for (String character : keySet) {
            int temp[] = paramMap.get(character);  //获取key对应的值--数组
            result1 += (temp[0] * temp[0]);   //temp[0]存储的是第一个字符串对应的向量
            result2 += (temp[1] * temp[1]);   //temp[1]存储的是第二个字符串对应的向量
        }
        return result1 * result2;
    }

    // 点乘法，在分子上，向量相乘
    private static double pointMulti(Map<String, int[]> paramMap) {
        double result = 0;
        Set<String> keySet = paramMap.keySet();  //返回map中所有key值的列表，这里的set，也可以用list代替吧
        for (String character : keySet) {    //存储的key值都是不重复的
            int temp[] = paramMap.get(character);   //获取key对应的值
            result += (temp[0] * temp[1]);
        }
        return result;
    }

    public static void main(String[] args) {
        String s1 = "abc,bca,kjh,ddfg";
        String s2 = "abc,dfg,ret,sfd";

        double v = cosList(Arrays.asList(s1.split(",")), Arrays.asList(s2.split(",")));
        System.out.println(v);
    }
}