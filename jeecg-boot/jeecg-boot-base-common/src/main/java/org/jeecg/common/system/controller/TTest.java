package org.jeecg.common.system.controller;

import org.jeecg.common.system.vo.TestVO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @program: jeecg-boot-parent
 * @description:
 * @author: llm
 * @create: 2020-03-21 14:21
 **/
public class TTest {
    public static void main(String[] args) {
        String ss = "NO=42;IMEI=866262045791299;ICCID=89860411101893280735;SS=31;" +
                "ADD=1;STATUS=3;ADD=1001;STATUS=2;PV=28;ADD=1002;STATUS=0;PV=0;" +
                "ADD=1003;STATUS=1;PV=0;ADD=1004;STATUS=1;PV=0;" +
                "ADD=2;STATUS=1;ADD=2001;STATUS=1;PV=0;" +
                "ADD=2002;STATUS=1;PV=0;ADD=2003;STATUS=1;PV=0;ADD=2004;STATUS=1;PV=0;";
        String[] split = ss.split(";");
        List<String> strs = Arrays.asList(split);
        String imei = strs.get(1).split("=")[1];
        String iccid = strs.get(2).split("=")[1];
        String ss2 = "";
        ArrayList<TestVO> testVOS = new ArrayList<>();
        for (int i = 4; i < split.length; i++) {
            if(split[i].split("=")[0].equals("ADD")&&i!=4){
                ss2 += "|"+split[i]+";";
            }else{
                ss2 += split[i]+";";
            }
        }
        String[] split1 = ss2.split("\\|");
        for (int i = 0; i < split1.length; i++) {
            TestVO testVO = new TestVO();
            testVO.setImei(imei);
            testVO.setIccid(iccid);
//            System.out.println("测试：  "+split1[i]);
            String[] device2 = split1[i].split(";");
//            System.out.println(device2[0].split("=")[1]);
            testVO.setAdd(device2[0].split("=")[1]);
            testVO.setStatus(device2[1].split("=")[1]);
            String s = test3(testVO.getStatus());
            System.out.println(s);
            char[] chars = s.toCharArray();
            for (int j = 0; j < chars.length; j++) {
                System.out.println(chars[j]);
            }
            if(device2.length>2){
                testVO.setPv(device2[2].split("=")[1]);
            }
            testVOS.add(testVO);
        }
        //至此已获得实时数据集合
        System.out.println(testVOS);
    }


    public static String test3(String str){
        String tempStr = "";
        Integer num = Integer.valueOf(str);
        String str2 = Integer.toBinaryString(num);
        //判断一下：如果转化为二进制为0或者1或者不满8位，要在数后补0
        int bit = 6-str2.length();
        if(str2.length()<6){
            for(int j=0; j<bit; j++){
                str2 = "0"+str2;
            }
        }
        tempStr += str2;
        System.out.println("数转换"+tempStr);
        return tempStr;
    }

}
