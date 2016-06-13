package org.code4j.jproxy.util;/**
 * Description : Configuration
 * Created by YangZH on 16-5-29
 *  下午5:39
 */

import com.alibaba.fastjson.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Description : Configuration
 * Created by YangZH on 16-5-29
 * 下午5:39
 */

public class Configuration {


    public static JSONObject params;
    public Configuration() {

    }

    static {
        InputStream is = Configuration.class.getResourceAsStream("/config.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sb = new StringBuffer();
        String str = "";
        try {
            while ((str = br.readLine()) != null){
                sb.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
            try {
                is.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String json = sb.toString();
        params = JSONObject.parseObject(json);
    }
    public static String getStringValue(String param){
        return params.getString(param);
    }
    public static Integer getIntValue(String param){
        return params.getIntValue(param);
    }
}
