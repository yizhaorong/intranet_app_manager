package org.yzr.utils;

import com.alibaba.fastjson.JSONObject;
import okhttp3.*;
import org.springframework.http.converter.json.GsonBuilderUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DingdingUtils {

    private static OkHttpClient client = new OkHttpClient();
    /**
     * 发送钉钉消息
     * @param jsonString 消息内容
     * @param webhook 钉钉自定义机器人webhook
     * @return
     */
    public static boolean sendToDingding(String jsonString, String webhook) {
        try{
            String type = "application/json; charset=utf-8";
            RequestBody body = RequestBody.create(MediaType.parse(type), jsonString);
            Request.Builder builder = new Request.Builder().url(webhook);
            builder.addHeader("Content-Type", type).post(body);

            Request request = builder.build();
            Response response = client.newCall(request).execute();
            String string = response.body().string();
            System.out.println(String.format("send ding message:%s", string));
            JSONObject result = JSONObject.parseObject(jsonString);
            System.out.println(result.getInteger("errcode"));
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public static boolean sendMarkdown() {
        Map<String, Object> markdown = new HashMap<>();
        markdown.put("title", "时代的火车向前开");
        markdown.put("text", "[ticket(Android)更新](https://www.baidu.com) \n\n ![alt 啊](https://ali-fir-pro-icon.fir.im/62658a54e48ea4b6115da07955c87e20ef4580b9?auth_key=1565408715-0-0-160b683eadd339c11a4ab1c701cda254&tmp=1565406915.71304) \n\n 链接：[https://fir.im/cq7f](https://fir.im/cq7f) \n\n 版本: 1.0 (Build: 1)");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msgtype", "markdown");
        jsonObject.put("markdown", markdown);

        return sendToDingding(jsonObject.toJSONString(), "https://oapi.dingtalk.com/robot/send?access_token=a2030da1a934473cc8f950d342888321387ed65f92bcf16a6bf4c048903e5a0e");
    }
}
