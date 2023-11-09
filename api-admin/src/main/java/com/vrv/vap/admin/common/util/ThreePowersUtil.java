package com.vrv.vap.admin.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author lilang
 * @date 2019/8/9
 * @description 三权分立获取角色对应的菜单权限
 */
public class ThreePowersUtil {

    public static List<Integer> getRolePowers(Integer roleId){
        ArrayList<Integer> result = null;
        String str;
        switch (roleId) {
            case 1 :
                str ="1,3,5,7";
                result =  new ArrayList(Arrays.asList(str.split(","))) ;
                break;
            case 2 :
                str ="2,3,6,7";
                result =  new ArrayList(Arrays.asList(str.split(","))) ;
                break;
            case 4 :
                str ="4,5,6,7";
                result =  new ArrayList(Arrays.asList(str.split(","))) ;
                break;
            default:
                result = null;
        }
        return result;
    }

}
