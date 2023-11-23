package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.model.DbBackupStrategy;
import org.junit.Test;

/**
 * @author wh1107066
 * @date 2023/9/1
 */
public class StringObjectParse {


    // 写一个测试用例，使用冒泡算法对数组进行排序
    @Test
    public void testSort() {
        int[] arr = {1, 3, 2, 5, 4};
        int temp;
        for (int i = 0; i < arr.length - 1; i++) {
            // 每次循环都会把最大的数放到最后
            for (int j = 0; j < arr.length - 1 - i; j++) {
                // 如果前面的数比后面的数大，则交换位置
                if (arr[j] > arr[j + 1]) {
                    // 交换位置
                    temp = arr[j];
                    arr[j] = arr[j + 1];
                    arr[j + 1] = temp;
                }

            }
        }
        for (int i : arr) {
            System.out.println(i);
        }
    }
    @Test
    public void test(){
        String xx="{\n" +
                "\t\"push_time\": \"2023-09-01 11:41:55\",\n" +
                "\t\"code\": \"200\",\n" +
                "\t\"msg\": \"成功\",\n" +
                "\t\"data\": [{\n" +
                "\t\t\"assis_id\": \"bc71f84457f3c2cb42bd3863d1ac392\",\n" +
                "\t\t\"event_id\": \"bc71f84457f3c2cb42bd3863d1ac392\",\n" +
                "\t\t\"event_name\": \"存在来自外部的异常访问端口\",\n" +
                "\t\t\"event_type\": \"3\",\n" +
                "\t\t\"alert_detail\": \"单位的ip在时间2023-09-01 10:10:10从跟端口成功登录应用系统\",\n" +
                "\t\t\"verify_process\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"cause\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"cause_type\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"disposal_time\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"disposal_process\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"disposal_measure\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"extern_ip\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"ip_source\": \"保密工作部门 2023-09-01 10:10:10 审批登记情况\",\n" +
                "\t\t\"connect_range\": [{\n" +
                "\t\t\t\"device_ip\": \"附件1\",\n" +
                "\t\t\t\"device_type\": \"test\"\n" +
                "\t\t}, {\n" +
                "\t\t\t\"device_ip\": \"附件1\",\n" +
                "\t\t\t\"device_type\": \"test\"\n" +
                "\t\t}],\n" +
                "\t\t\"app_name\": [\"oa\", \"邮件\"],\n" +
                "\t\t\"app_account\": [\"test1\", \"test@163.com\"],\n" +
                "\t\t\"assis_cause\": \"由于无法准确定位外部ip地址来源，发起协办\",\n" +
                "\t\t\"verify_content\": \"ip地址对应的则惹人，部门\",\n" +
                "\t\t\"recommend_disposal_measure\": \"ip地址对应的则惹人，部门\",\n" +
                "\t\t\"apply_unit\": \"单位1\",\n" +
                "\t\t\"apply_contact_name\": \"张三\",\n" +
                "\t\t\"apply_contact_dept\": \"综合部\",\n" +
                "\t\t\"apply_contact_telephone\": \"13477298789\"\n" +
                "\t}]\n" +
                "}";

        System.out.println(xx);
        JSONObject jsonObject = JSONObject.parseObject(xx);
        Object code = jsonObject.get("code");
        System.out.println(code);
    }

    @Test
    public void print(){
        DbBackupStrategy dbBackupStrategy = new DbBackupStrategy();
        dbBackupStrategy.setBackupTime("11:32:33");
        dbBackupStrategy.setBackPeriod(4);
        String[] time = dbBackupStrategy.getBackupTime().split(":");
        if (time[1].startsWith("0")) {
            time[1] = time[1].substring(1);
        }
        if (time[0].startsWith("0")) {
            time[0] = time[0].substring(1);
        }

        String zz = String.format("0 %s %s */%s * ?", time[1], time[0], dbBackupStrategy.getBackPeriod());
        System.out.println(zz);

    }
}
