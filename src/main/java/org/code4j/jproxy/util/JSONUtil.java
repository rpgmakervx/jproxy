package org.code4j.jproxy.util;/**
 * Description : 
 * Created by YangZH on 16-5-29
 *  下午5:50
 */

import com.alibaba.fastjson.JSONObject;

/**
 * Description :
 * Created by YangZH on 16-5-29
 * 下午5:50
 */

public class JSONUtil {

    public static void strToJsonArray(String json){
        JSONObject.parseArray(json);
    }
}
