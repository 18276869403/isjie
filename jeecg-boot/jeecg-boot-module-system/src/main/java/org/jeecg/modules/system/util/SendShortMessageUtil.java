package org.jeecg.modules.system.util;

import com.github.qcloudsms.SmsSingleSender;
import com.github.qcloudsms.SmsSingleSenderResult;
import com.github.qcloudsms.httpclient.HTTPException;
import org.json.JSONException;

import java.io.IOException;

public class SendShortMessageUtil {
    // 短信应用SDK AppID
    private static int appId = 1400304421;
    private static String appKey = "b6e21d649eb57211b9b2401439a214a3";
    /*
     * 发送一条短信验证码
     * */
    public static String sendShortMessage(int templateId,String[] phoneNumbers,String[] params){
        // 需要发送短信的手机号码
        // 短信模板ID，需要在短信应用中申请
        // NOTE: 真实的模板ID需要在短信控制台中申请
        //templateId7839对应的内容是"您的验证码是: {1}"
        //数组具体的元素个数和模板中变量个数必须一致，例如事例中templateId:5678对应一个变量，参数数组中元素个数也必须是一个
        //params = {"1234"};
        try {
            SmsSingleSender ssender = new SmsSingleSender(appId, appKey);
            for (String phone:phoneNumbers) {
                SmsSingleSenderResult result1 = ssender.sendWithParam("86", phone,
                        templateId, params, "是杰安全云", "", "");  // 签名参数未提供或者为空时，会使用默认签名发送短信
                System.out.println(result1);
            }

        } catch (HTTPException e) {
            // HTTP响应码错误
            e.printStackTrace();
            return "HTTP响应码错误";
        } catch (JSONException e) {
            // json解析错误
            e.printStackTrace();
            return "json解析错误";
        } catch (IOException e) {
            // 网络IO错误
            e.printStackTrace();
            return "网络IO错误";
        }
        return "短信发送成功";
    }
}
